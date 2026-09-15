// reserve_list.js

// 탭 이름 → 예약 상태 코드 매핑
const TAB_STATUS_MAP = {
  active: ["0", "1"], // 진행중
  done: ["9"], // 완료
  cancel: ["C"], // 취소
};

// 탭 클릭 시 카드 필터링
// 서버(reserve/list)가 로그인 회원의 예약을 상태 상관없이 전부 한 번에 내려주기 때문에,
// 탭을 바꿀 때마다 서버에 다시 요청하지 않고 이미 화면에 있는 카드를 상태값 기준으로
// 보였다 숨겼다 하는 방식으로 처리했다. 예약 건수가 적은(수십 건 이하) 서비스라
// 전체를 한 번에 받아도 부담이 없다고 판단했다.
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
      // 취소된 카드 하나만 DOM에서 지우거나 상태를 바꿔줄 수도 있지만,
      // 취소 후에는 "진행중" 탭 카드 개수도 바뀌고 상태 배지/버튼도 같이 바뀌어야 해서
      // 부분 갱신 코드를 따로 짜는 것보다 전체 새로고침이 더 간단하고 확실하다고 판단.
      location.reload();
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