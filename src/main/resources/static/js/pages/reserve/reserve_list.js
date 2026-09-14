// reserve_list.js

// 탭 이름 → 예약 상태 코드 매핑
const TAB_STATUS_MAP = {
  active: ["0", "1"], // 진행중
  done: ["9"], // 완료
  cancel: ["C"], // 취소
};

// 탭 클릭 시 카드 필터링
const filterTab = (tab, el) => {
  // 탭 활성화 표시
  document.querySelectorAll("#tabList li").forEach((li) => {
    li.classList.remove("tab-active");
  });
  el.classList.add("tab-active");

  // 이 탭에 해당하는 상태 코드 목록
  const targets = TAB_STATUS_MAP[tab] || [];

  // 카드 순회하며 표시/숨김
  let visibleCount = 0;
  document.querySelectorAll(".reserve-item").forEach((item) => {
    const isVisible = targets.includes(item.dataset.status);
    item.style.display = isVisible ? "" : "none";
    if (isVisible) visibleCount++;
  });

  // 이 탭에 카드가 없으면 안내 문구 노출
  const tabEmpty = document.getElementById("tabEmpty");
  const hasAnyReserve = document.querySelectorAll(".reserve-item").length > 0;
  if (tabEmpty) {
    tabEmpty.style.display = hasAnyReserve && visibleCount === 0 ? "" : "none";
  }
};

// 상세정보 펼침/접힘 토글
const toggleSymptom = (btn) => {
  const item = btn.closest(".reserve-item");
  const panel = item.querySelector(".reserve-symptom-panel");

  // toggle() 반환값: 새로 열렸으면 true, 닫혔으면 false
  const isOpen = panel.classList.toggle("open");

  const icon = btn.querySelector("i");
  const textarea = panel.querySelector(".symptom-textarea");

  if (isOpen) {
    icon.classList.replace("bi-chevron-down", "bi-chevron-up");
    // 내용 높이에 맞춰 자동 확장 (CSS max-height가 상한선)
    textarea.style.height = "auto";
    textarea.style.height = textarea.scrollHeight + "px";
  } else {
    icon.classList.replace("bi-chevron-up", "bi-chevron-down");
    // 높이 초기화 (다음 펼침 때 재계산)
    textarea.style.height = "";
  }
};

// 페이지 진입 시 진행 중 탭 기본 적용
document.addEventListener("DOMContentLoaded", () => {
  const firstTab = document.querySelector("#tabList li.tab-active");
  if (firstTab) filterTab("active", firstTab);
});

// 예약 취소 (AJAX)
const cancelReserve = async (reserveNo) => {
  // 사용자 확인
  if (!confirm("이 예약을 취소하시겠습니까?")) return;

  try {
    const response = await axios.post("/reserve/cancel", null, {
      params: { reserveNo: reserveNo },
    });
    const data = response.data;

    if (data.success) {
      alert(data.message);
      location.reload();   // 페이지 새로고침해서 상태 반영
    } else if (data.redirect) {
      location.href = data.redirect;
    } else {
      alert(data.error);
    }
  } catch (err) {
    console.error("예약 취소 실패:", err);
    alert("예약 취소 중 오류가 발생했습니다.");
  }
};