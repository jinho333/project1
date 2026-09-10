// reserve_time.js

document.addEventListener("DOMContentLoaded", function () {
  const calendarEl = document.getElementById("calendar");

  if (!calendarEl) {
    console.error("❌ #calendar 요소를 찾을 수 없습니다.");
    return;
  }

  // 오늘 날짜를 기본 선택값으로 설정
  const today = new Date();
  let selectedDate = formatDate(today);   // "2026-09-10"

  const calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: "dayGridMonth",
    locale: "ko",
    selectable: true,
    dateClick: function (info) {
      selectedDate = info.dateStr;
      console.log("✅ 선택한 날짜:", selectedDate);

      // hidden input에 선택한 날짜 저장
      document.getElementById("reserveDateInput").value = selectedDate;

      // 슬롯 헤더 날짜 갱신
      updateSlotHeader(selectedDate);

      // 슬롯 그리드 갱신
      loadAvailableTimes(selectedDate);
    },
  });

  calendar.render();
  console.log("✅ 캘린더 렌더링 완료!");

  // 페이지 진입 시 오늘 날짜로 초기화
  document.getElementById("reserveDateInput").value = selectedDate;
  updateSlotHeader(selectedDate);
  loadAvailableTimes(selectedDate);
});


// ✅ Date → "yyyy-MM-dd" 문자열 변환
function formatDate(date) {
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
}


// ✅ 슬롯 헤더의 날짜를 "9월 10일 (목)" 형태로 갱신
function updateSlotHeader(dateStr) {
  const headerEl = document.getElementById("slotDateTitle");
  if (!headerEl) return;

  const date = new Date(dateStr);
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
  const weekday = weekdays[date.getDay()];

  headerEl.innerHTML = `${month}월 ${day}일 (${weekday}) <span class="badge badge-primary">오늘</span>`;
}


// ✅ [임시] 서버 API 대신 더미 데이터로 슬롯 그림
async function loadAvailableTimes(date) {
  // 전체 시간 슬롯
  const allTimes = [
    "09:00", "09:30", "10:00", "10:30",
    "11:00", "11:30", "12:00", "12:30",
    "13:00", "13:30", "14:00", "14:30",
    "15:00", "15:30", "16:00", "16:30"
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
}


// ✅ 슬롯 그리드를 다시 그리는 함수
function renderSlotGrid(allTimes, reservedTimes) {
  const slotGrid = document.getElementById("slotGrid");
  if (!slotGrid) return;

  // 기존 슬롯 모두 삭제
  slotGrid.innerHTML = "";

  // 시간 하나씩 순회하며 슬롯 생성
  allTimes.forEach(time => {
    const isReserved = reservedTimes.includes(time);

    if (isReserved) {
      // 예약된 시간 → 마감 처리 (라디오 버튼 없음 → 클릭 불가)
      const div = document.createElement("div");
      div.className = "slot slot--disabled";
      div.textContent = time;
      slotGrid.appendChild(div);
    } else {
      // 선택 가능 → 라디오 버튼으로 생성
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
}


// ✅ 예약 확정 함수
function goComplete() {
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

  // 3. 날짜 + 시간을 합쳐서 hidden input에 저장
  const reserveDateTime = reserveDate + " " + timeInput.value + ":00";
  document.getElementById("reserveDateInput").value = reserveDateTime;

  console.log("✅ 예약 확정 데이터:", reserveDateTime);

  // 4. 폼 전송
  document.getElementById("reserve-time-form").submit();
}
//예약 확정 유효성 검사
// const reserveTimeValigate = () => {
//   const time = document.querySelector('input[name="time"]:checked');

//   document.querySelector('#timeP').textContent = '';

//   if(time == null){
//     document.querySelector('#timeP').textContent = '방문 시간을 선택해주세요.';
//     return false;
//   }
//   return true;
// }

// //예약 확정하기 버튼 클릭시
// const goComplete = () => {
//   const result = reserveTimeValigate();
//   if(result){
//     document.querySelector('#reserve-time-form').submit();
//   }
// }
