// reserve_list.js

const filterTab = (tab, el) => {

  // 1) 탭 활성화 표시 (모두 해제 후 클릭한 탭만 활성)
  document.querySelectorAll("#tabList li").forEach((li) => {
    li.classList.remove("tab-active");
  });
  el.classList.add("tab-active");

  // 2) 탭 이름 → 예약 상태 코드 배열로 매핑
  const statusMap = {
    active: ["0", "1", "2"],
    done:   ["9"],
    cancel: ["C"],
  };
  const targets = statusMap[tab] || [];

  // 3) 카드 순회하며 표시/숨김 처리
  let visibleCount = 0;
  document.querySelectorAll(".reserve-item").forEach((item) => {
    const isVisible = targets.includes(item.dataset.status);
    item.style.display = isVisible ? "" : "none";
    if (isVisible) visibleCount++;
  });

  // 4) 해당 탭에 카드가 없으면 안내 문구 노출
  //    (예약이 아예 없는 경우는 별도 empty-state가 표시됨)
  const tabEmpty = document.getElementById("tabEmpty");
  const hasAnyReserve = document.querySelectorAll(".reserve-item").length > 0;
  if (tabEmpty) {
    tabEmpty.style.display = (hasAnyReserve && visibleCount === 0) ? "" : "none";
  }
};

// 페이지 진입 시 "진행 중인 예약" 탭을 기본으로 적용
document.addEventListener("DOMContentLoaded", () => {
  const firstTab = document.querySelector("#tabList li.tab-active");
  if (firstTab) {
    filterTab("active", firstTab);
  }
});