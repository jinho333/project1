// reserve_time.js

document.addEventListener("DOMContentLoaded", function () {
  const calendarEl = document.getElementById("calendar");

  if (!calendarEl) {
    console.error("❌ #calendar 요소를 찾을 수 없습니다.");
    return;
  }

  const calendar = new FullCalendar.Calendar(calendarEl, {
    // 6.x는 plugins가 필요 없습니다!
    initialView: "dayGridMonth",
    locale: "ko",
    selectable: true,
    dateClick: function (info) {
      const selectedDate = info.dateStr;
      console.log("✅ 선택한 날짜:", selectedDate);
      // 여기에 시간 슬롯 업데이트 로직 추가
    },
  });

  calendar.render();
  console.log("✅ 캘린더 렌더링 완료!");
});