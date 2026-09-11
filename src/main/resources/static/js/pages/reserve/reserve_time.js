// reserve_time.js

document.addEventListener("DOMContentLoaded", () => {
  const calendarEl = document.getElementById("calendar");

  if (!calendarEl) {
    console.error("#calendar 요소를 찾을 수 없습니다.");
    return;
  }

  // 내일 날짜 계산 (당일 예약 차단 정책)
  // - 오늘 날짜는 선택 불가 → 최소 내일부터 예약 가능
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const tomorrowStr = formatDate(tomorrow);

  // 기본 선택값은 내일
  let selectedDate = tomorrowStr;

  // ---------------------------------------------------------
  // FullCalendar 초기화
  // ---------------------------------------------------------
  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",  // 월간 뷰
    locale: "ko",                  // 한국어
    selectable: true,              // 날짜 선택 가능

    // 오늘 이전 날짜는 선택 불가 (당일 예약 차단)
    validRange: {
      start: tomorrowStr,
    },

    // 상단 툴바: [<] 제목 [>] — 제목 양옆에 화살표 배치
    // (iOS/네이버/카카오 캘린더와 동일한 패턴)
    headerToolbar: {
      start: "prev",
      center: "title",
      end: "next",
    },

    // 날짜 셀 클릭 시 실행
    // - info.dateStr: 클릭한 날짜 ("yyyy-MM-dd")
    // - 선택한 날짜로 슬롯 그리드를 다시 그림
    dateClick: (info) => {
      selectedDate = info.dateStr;
      console.log("[날짜 선택]", selectedDate);
      applySelectedDate(selectedDate);
    },
  });

  calendar.render();
  console.log("캘린더 렌더링 완료");

  // ---------------------------------------------------------
  // 슬롯 선택 로그 (이벤트 위임)
  // ---------------------------------------------------------
  // [이벤트 위임을 쓰는 이유]
  //   - 슬롯은 서버 응답마다 새로 생성됨 (innerHTML = ""로 초기화 후 재생성)
  //   - 개별 라디오에 리스너를 달면 매번 새로 등록해야 함
  //   - slotGrid에 리스너 1개만 달면, 안의 라디오 이벤트가 버블링되어 잡힘
  const slotGrid = document.getElementById("slotGrid");
  if (slotGrid) {
    slotGrid.addEventListener("change", (event) => {
      const input = event.target;
      if (input.name === "slotNo") {
        // 라벨 텍스트: 라디오 옆 div(.slot)의 텍스트
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


// ---------------------------------------------------------
// 선택된 날짜를 화면 전체에 반영하는 공통 함수
// - hidden input 값 저장
// - 슬롯 헤더 날짜 갱신
// - 슬롯 그리드 갱신 (서버에서 다시 조회)
// ---------------------------------------------------------
const applySelectedDate = (dateStr) => {
  document.getElementById("reserveDateInput").value = dateStr;
  updateSlotHeader(dateStr);
  loadAvailableTimes(dateStr);
};


// ---------------------------------------------------------
// Date 객체를 "yyyy-MM-dd" 형식의 문자열로 변환
// - 서버 DTO(reserveDate, LocalDate)와 형식을 일치시킴
// ---------------------------------------------------------
const formatDate = (date) => {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");  // 1~9월 → "01"~"09"
  const dd = String(date.getDate()).padStart(2, "0");       // 1~9일 → "01"~"09"
  return `${yyyy}-${mm}-${dd}`;
};


// ---------------------------------------------------------
// 슬롯 헤더의 날짜를 "9월 10일 (목)" 형태로 갱신
// - 상단에 표시되는 "방문 날짜" 제목 영역
// ---------------------------------------------------------
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


// ---------------------------------------------------------
// 서버에서 예약 가능 시간을 조회
// 응답 예시: { slots: [...], reservedSlots: [1, 3] }
// ---------------------------------------------------------
const loadAvailableTimes = async (date) => {
  try {
    const response = await fetch(`/reserve/available-times?date=${date}`);
    const data = await response.json();
    // data.slots         : 전체 슬롯 목록 (TIME_SLOT 테이블)
    // data.reservedSlots : 이미 예약된 슬롯 번호 목록
    renderSlotGrid(data.slots, data.reservedSlots);
  } catch (error) {
    console.error("예약 가능 시간 조회 실패:", error);
  }
};


// ---------------------------------------------------------
// 슬롯 그리드를 그리는 함수
// - slots         : 전체 슬롯 목록 (TIME_SLOT 테이블에서 조회)
// - reservedSlots : 이미 예약된 슬롯 번호 배열
//
// [렌더링 규칙]
//   - 예약된 슬롯   → <div class="slot slot--disabled"> (클릭 불가)
//   - 예약 가능 슬롯 → <label><input type="radio" name="slotNo"></label>
// ---------------------------------------------------------
const renderSlotGrid = (slots, reservedSlots) => {
  const slotGrid = document.getElementById("slotGrid");
  if (!slotGrid) return;

  // 기존 슬롯 모두 삭제 (날짜 바뀔 때마다 다시 그림)
  slotGrid.innerHTML = "";

  slots.forEach((slot) => {
    const isReserved = reservedSlots.includes(slot.slotNo);

    if (isReserved) {
      // 예약된 슬롯은 마감 처리 (라디오 버튼 없음, 클릭 불가)
      const div = document.createElement("div");
      div.className = "slot slot--disabled";
      div.textContent = slot.slotLabel;
      slotGrid.appendChild(div);
    } else {
      // 선택 가능한 슬롯은 라디오 버튼으로 생성
      // (change 이벤트는 상위 slotGrid의 이벤트 위임으로 처리)
      const label = document.createElement("label");
      label.className = "slot-label";

      const input = document.createElement("input");
      input.type = "radio";
      input.name = "slotNo";       // 서버로 전송될 파라미터 이름
      input.value = slot.slotNo;   // 값은 슬롯 번호 (예: 1)
      input.className = "slot-input";

      const div = document.createElement("div");
      div.className = "slot";
      div.textContent = slot.slotLabel;  // 화면 표시용 (예: "10:00 ~ 12:00")

      label.appendChild(input);
      label.appendChild(div);
      slotGrid.appendChild(label);
    }
  });
};


// ---------------------------------------------------------
// "예약 확정하기" 버튼 클릭 시 실행
// - 방문 날짜(hidden)와 슬롯 번호(radio) 검증 후 폼 전송
// - 서버 DTO: reserveDate(LocalDate) + slotNo(Long)
// ---------------------------------------------------------
const goComplete = () => {
  // 1) 날짜가 선택되었는지 확인
  const reserveDate = document.getElementById("reserveDateInput").value;
  if (!reserveDate) {
    alert("방문 날짜를 먼저 선택해주세요.");
    return;
  }

  // 2) 시간 슬롯이 선택되었는지 확인
  const slotInput = document.querySelector('input[name="slotNo"]:checked');
  if (!slotInput) {
    document.getElementById("timeP").textContent = "방문 시간을 선택해주세요.";
    return;
  }

  // 3) 검증 통과 시 폼 전송
  // - reserveDate: "yyyy-MM-dd" (hidden input)
  // - slotNo     : "1" ~ "4" (radio input)
  console.log("[예약 확정] 데이터 전송", {
    reserveDate: reserveDate,
    slotNo: slotInput.value,
  });

  document.getElementById("reserve-time-form").submit();
};