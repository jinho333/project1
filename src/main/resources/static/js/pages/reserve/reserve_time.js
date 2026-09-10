// reserve_time.js
// 예약 시간 선택 페이지의 캘린더 및 슬롯 처리

document.addEventListener("DOMContentLoaded", () => {
  const calendarEl = document.getElementById("calendar");

  if (!calendarEl) {
    console.error("#calendar 요소를 찾을 수 없습니다.");
    return;
  }

  // 오늘 날짜를 기본 선택값으로 설정
  const today = new Date();
  let selectedDate = formatDate(today); // "2026-09-10"

  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",
    locale: "ko",
    selectable: true,

    // 상단 툴바 배치
    headerToolbar: {
      start: "prev,next",
      center: "title",
      end: "today"
    },

    // 날짜 셀 클릭 시 실행
    dateClick: (info) => {
      selectedDate = info.dateStr;
      console.log("선택한 날짜:", selectedDate);
      applySelectedDate(selectedDate);
    },
  });

  calendar.render();
  console.log("캘린더 렌더링 완료");

  // today 버튼 클릭 시 별도 처리
  // - FullCalendar의 today 버튼은 뷰만 이동할 뿐 dateClick을 발생시키지 않음
  // - 그래서 버튼에 직접 리스너를 붙여 오늘 날짜로 슬롯을 갱신
  const todayButton = document.querySelector(".fc-today-button");
  if (todayButton) {
    todayButton.addEventListener("click", () => {
      const todayStr = formatDate(new Date());
      selectedDate = todayStr;
      console.log("today 버튼 클릭:", todayStr);
      applySelectedDate(todayStr);
    });
  }

  // 페이지 진입 시 오늘 날짜로 초기화
  applySelectedDate(selectedDate);
});


// 선택된 날짜를 화면 전체에 반영하는 공통 함수
// - hidden input 값 저장
// - 슬롯 헤더 날짜 갱신
// - 슬롯 그리드 갱신
const applySelectedDate = (dateStr) => {
  document.getElementById("reserveDateInput").value = dateStr;
  updateSlotHeader(dateStr);
  loadAvailableTimes(dateStr);
};


// Date 객체를 "yyyy-MM-dd" 형식의 문자열로 변환
const formatDate = (date) => {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
};


// 슬롯 헤더의 날짜를 "9월 10일 (목)" 형태로 갱신
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


// [임시] 서버 API 완성 전 테스트용 더미 데이터
// API 완성 후에는 아래 주석 처리된 fetch 버전으로 교체
const loadAvailableTimes = async (date) => {
  // 전체 시간 슬롯
  const allTimes = [
    "09:00", "09:30", "10:00", "10:30",
    "11:00", "11:30", "12:00", "12:30",
    "13:00", "13:30", "14:00", "14:30",
    "15:00", "15:30", "16:00", "16:30",
  ];

  // 날짜에 따라 다른 마감 시간을 흉내내기 (테스트용)
  let reservedTimes = [];
  const todayStr = formatDate(new Date());

  if (date === todayStr) {
    // 오늘 날짜: 09:00, 13:00 마감
    reservedTimes = ["09:00", "13:00"];
  } else {
    // 다른 날짜: 10:00, 14:00 마감
    reservedTimes = ["10:00", "14:00"];
  }

  renderSlotGrid(allTimes, reservedTimes);
};

// [API 완성 후 사용] 서버에서 예약 가능 시간을 조회
// const loadAvailableTimes = async (date) => {
//   try {
//     const response = await fetch(`/reserve/available-times?date=${date}`);
//     const data = await response.json();
//     renderSlotGrid(data.allTimes, data.reservedTimes);
//   } catch (error) {
//     console.error("예약 가능 시간 조회 실패:", error);
//   }
// };


// 슬롯 그리드를 다시 그리는 함수
const renderSlotGrid = (allTimes, reservedTimes) => {
  const slotGrid = document.getElementById("slotGrid");
  if (!slotGrid) return;

  // 기존 슬롯 모두 삭제
  slotGrid.innerHTML = "";

  // 시간 하나씩 순회하며 슬롯 생성
  allTimes.forEach((time) => {
    const isReserved = reservedTimes.includes(time);

    if (isReserved) {
      // 예약된 시간은 마감 처리 (라디오 버튼 없음, 클릭 불가)
      const div = document.createElement("div");
      div.className = "slot slot--disabled";
      div.textContent = time;
      slotGrid.appendChild(div);
    } else {
      // 선택 가능한 시간은 라디오 버튼으로 생성
      const label = document.createElement("label");
      label.className = "slot-label";

      const input = document.createElement("input");
      input.type = "radio";
      input.name = "time";
      input.value = time;
      input.className = "slot-input";

      const div = document.createElement("div");
      div.className = "slot";
      div.textContent = time;

      label.appendChild(input);
      label.appendChild(div);
      slotGrid.appendChild(label);
    }
  });
};


// 예약 확정 함수
// "예약 확정하기" 버튼 클릭 시 실행됨
const goComplete = () => {
  // 1. 날짜가 선택되었는지 확인
  const reserveDate = document.getElementById("reserveDateInput").value;
  if (!reserveDate) {
    alert("방문 날짜를 먼저 선택해주세요.");
    return;
  }

  // 2. 시간이 선택되었는지 확인
  const timeInput = document.querySelector('input[name="time"]:checked');
  if (!timeInput) {
    document.getElementById("timeP").textContent = "방문 시간을 선택해주세요.";
    return;
  }

  // 3. 날짜와 시간을 합쳐서 hidden input에 저장
  //    최종 형식: "2026-09-10 10:00:00"
  const reserveDateTime = reserveDate + " " + timeInput.value + ":00";
  document.getElementById("reserveDateInput").value = reserveDateTime;

  console.log("예약 확정 데이터:", reserveDateTime);

  // 4. 폼 전송
  document.getElementById("reserve-time-form").submit();
};