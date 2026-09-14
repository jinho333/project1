// reserve_time.js

// 초기화 (FullCalendar + 슬롯 그리드)
document.addEventListener("DOMContentLoaded", function () {
  const calendarEl = document.getElementById("calendar");

  if (!calendarEl) {
    console.error("❌ #calendar 요소를 찾을 수 없습니다.");
    return;
  }

  // 기본 선택 날짜 = 내일 (당일 예약 불가)
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const tomorrowStr = formatDate(tomorrow);

  let selectedDate = tomorrowStr;

  // FullCalendar 초기화
  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",
    locale: "ko",
    selectable: true,

    // 오늘 이전 날짜는 선택 불가 (당일 예약 차단)
    validRange: {
      start: tomorrowStr,
    },

    // 상단 툴바: [<] 제목 [>]
    headerToolbar: {
      start: "prev",
      center: "title",
      end: "next",
    },

    // 날짜 셀 클릭 시 실행
    dateClick: (info) => {
      selectedDate = info.dateStr;
      console.log("[날짜 선택]", selectedDate);
      applySelectedDate(selectedDate);
    },
  });

  calendar.render();
  console.log("캘린더 렌더링 완료");

  // 슬롯 선택 로그 (이벤트 위임)
  // - 슬롯이 서버 응답마다 새로 생성되므로 위임 방식 사용
  const slotGrid = document.getElementById("slotGrid");
  if (slotGrid) {
    slotGrid.addEventListener("change", (event) => {
      const input = event.target;
      if (input.name === "slotNo") {
        const labelEl = input.parentElement?.querySelector(".slot");
        const labelText = labelEl ? labelEl.textContent : "";
        console.log(
          "[슬롯 선택] slotNo =",
          input.value,
          "/ label =",
          labelText,
        );
      }
    });
  }

  // 페이지 진입 시 내일 날짜로 초기화
  applySelectedDate(selectedDate);
});

// 선택된 날짜를 화면 전체에 반영
const applySelectedDate = (dateStr) => {
  document.getElementById("reserveDateInput").value = dateStr;
  updateSlotHeader(dateStr);
  loadAvailableTimes(dateStr);
};

// Date → "yyyy-MM-dd" 문자열 변환
const formatDate = (date) => {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
};

// 슬롯 헤더 날짜 표시 (예: "9월 10일 (목)")
const updateSlotHeader = (dateStr) => {
  const headerEl = document.getElementById("slotDateTitle");
  if (!headerEl) return;

  const date = new Date(dateStr);
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
  const weekday = weekdays[date.getDay()];

  headerEl.innerHTML = `${month}월 ${day}일 (${weekday})`;
};

// 서버에서 예약 가능 시간 조회
const loadAvailableTimes = async (date) => {
  try {
    const response = await fetch(`/reserve/available-times?date=${date}`);
    const data = await response.json();
    renderSlotGrid(data.slots, data.reservedSlots);
  } catch (error) {
    console.error("예약 가능 시간 조회 실패:", error);
  }
};

// 슬롯 그리드 렌더링
// - 예약된 슬롯 → .slot--disabled (클릭 불가)
// - 예약 가능 → <label><input type="radio" name="slotNo"></label>
const renderSlotGrid = (slots, reservedSlots) => {
  const slotGrid = document.getElementById("slotGrid");
  if (!slotGrid) return;

  // 기존 슬롯 초기화
  slotGrid.innerHTML = "";

  slots.forEach((slot) => {
    const isReserved = reservedSlots.includes(slot.slotNo);

    if (isReserved) {
      // 예약 마감 슬롯
      const div = document.createElement("div");
      div.className = "slot slot--disabled";
      div.textContent = slot.slotLabel;
      slotGrid.appendChild(div);
    } else {
      // 선택 가능 슬롯
      const label = document.createElement("label");
      label.className = "slot-label";

      const input = document.createElement("input");
      input.type = "radio";
      input.name = "slotNo";
      input.value = slot.slotNo;
      input.className = "slot-input";

      const div = document.createElement("div");
      div.className = "slot";
      div.textContent = slot.slotLabel;

      label.appendChild(input);
      label.appendChild(div);
      slotGrid.appendChild(label);
    }
  });
};

// "예약 확정하기" 클릭 시 실행
// - 방문 날짜(hidden) + 슬롯 번호(radio) 검증 후 폼 전송
const goComplete = () => {
  // 1) 날짜 검증
  const reserveDate = document.getElementById("reserveDateInput").value;
  if (!reserveDate) {
    alert("방문 날짜를 먼저 선택해주세요.");
    return;
  }

  // 2) 슬롯 검증
  const slotInput = document.querySelector('input[name="slotNo"]:checked');
  if (!slotInput) {
    document.getElementById("timeP").textContent = "방문 시간을 선택해주세요.";
    return;
  }

  console.log("[예약 확정] 데이터 전송", {
    reserveDate: reserveDate,
    slotNo: slotInput.value,
  });

  document.getElementById("reserve-time-form").submit();
};
