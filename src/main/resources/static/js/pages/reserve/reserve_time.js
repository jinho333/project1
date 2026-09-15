// reserve_time.js

// 현재 선택된 날짜 셀 (클릭할 때마다 이전 선택 표시를 지우기 위해 기억해둠)
let selectedDayEl = null;

// 페이지 로드 시 실행 - 캘린더 초기화
document.addEventListener("DOMContentLoaded", function () {
  const calendarEl = document.getElementById("calendar");
  if (!calendarEl) return;

  // 기본 선택 날짜 = 내일 (당일 예약 불가 정책)
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const tomorrowStr = formatDate(tomorrow);

  // FullCalendar 초기화
  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",
    locale: "ko",
    validRange: { start: tomorrowStr }, // 오늘 이전 선택 불가
    headerToolbar: { start: "prev", center: "title", end: "next" },
    // info.dayEl: 클릭한 날짜의 셀(td) → 선택 표시(class) 토글에 사용
    dateClick: (info) => {
      applySelectedDate(info.dateStr);
      selectDayEl(info.dayEl);
    },
  });
  calendar.render();

  // 페이지 진입 시 내일 날짜로 초기화
  applySelectedDate(tomorrowStr);
  // 렌더 직후라 dateClick이 없으니, 내일 날짜 셀을 직접 찾아서 선택 표시
  selectDayEl(calendarEl.querySelector(`[data-date="${tomorrowStr}"]`));
});

// 클릭된 날짜 셀에 "선택됨" 표시(class)를 옮겨줌
// FullCalendar는 클릭한 날짜를 스스로 강조해주지 않아서, 이전 선택 셀의 class를 지우고
// 새로 클릭한 셀에만 class를 붙이는 방식으로 "선택된 날짜 색 변경"을 구현했다.
const selectDayEl = (dayEl) => {
  if (!dayEl) return;
  if (selectedDayEl) selectedDayEl.classList.remove("fc-day-selected");
  dayEl.classList.add("fc-day-selected");
  selectedDayEl = dayEl;
};

// 선택된 날짜를 화면에 반영 (hidden input + 헤더 + 슬롯 조회)
const applySelectedDate = (dateStr) => {
  document.getElementById("reserveDateInput").value = dateStr;
  updateSlotHeader(dateStr);
  loadAvailableTimes(dateStr);
};

// Date 객체를 "yyyy-MM-dd" 문자열로 변환
const formatDate = (date) => {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
};

// 슬롯 헤더에 날짜 표시 (예: "9월 10일 (목)")
const updateSlotHeader = (dateStr) => {
  const headerEl = document.getElementById("slotDateTitle");
  if (!headerEl) return;

  const date = new Date(dateStr);
  const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
  headerEl.innerHTML = `${date.getMonth() + 1}월 ${date.getDate()}일 (${weekdays[date.getDay()]})`;
};

// 서버에서 해당 날짜의 예약 가능 시간 조회
const loadAvailableTimes = async (date) => {
  try {
    const response = await axios.get("/reserve/available-times", {
      params: { date: date },
    });
    renderSlotGrid(response.data.slots, response.data.reservedSlots);
  } catch (error) {
    console.error("예약 가능 시간 조회 실패:", error);
  }
};

// 슬롯 그리드 렌더링 (마감 슬롯은 비활성, 나머지는 라디오 버튼)
const renderSlotGrid = (slots, reservedSlots) => {
  const slotGrid = document.getElementById("slotGrid");
  if (!slotGrid) return;

  slotGrid.innerHTML = "";

  slots.forEach((slot) => {
    if (reservedSlots.includes(slot.slotNo)) {
      // 예약 마감된 슬롯 (클릭 불가)
      const div = document.createElement("div");
      div.className = "slot slot--disabled";
      div.textContent = slot.slotLabel;
      slotGrid.appendChild(div);
    } else {
      // 선택 가능한 슬롯 (라디오 버튼)
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

// "예약 확정하기" 클릭 시 실행 (AJAX)
const goComplete = async () => {
  // 날짜 검증
  const reserveDate = document.getElementById("reserveDateInput").value;
  if (!reserveDate) {
    alert("방문 날짜를 먼저 선택해주세요.");
    return;
  }

  // 슬롯 검증
  const slotInput = document.querySelector('input[name="slotNo"]:checked');
  if (!slotInput) {
    document.getElementById("timeP").textContent = "방문 시간을 선택해주세요.";
    return;
  }

  // 이전 에러 메시지 초기화
  document.getElementById("completeErrorP").textContent = "";

  // 서버에 전송 후 결과에 따라 분기
  try {
    const formData = new FormData(document.getElementById("reserve-time-form"));
    const response = await axios.post("/reserve/complete", formData);
    const data = response.data;

    if (data.success || data.redirect) {
      // 성공 또는 로그인 실패 → 서버가 알려준 URL로 이동
      location.href = data.redirect;
    } else {
      // 검증 실패 → 페이지 리로드 없이 에러 메시지만 표시
      document.getElementById("completeErrorP").textContent = data.error;
    }
  } catch (err) {
    console.error("예약 확정 실패:", err);
    alert("예약 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
  }
};
