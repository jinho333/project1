
//대시보드 초기 로드 시 KPI 카운트업 애니메이션 (필요 시 확장)
document.addEventListener('DOMContentLoaded', () => {
  console.log('AS-FAST 관리자 대시보드 로드 완료');
});

//캘린더 셀 클릭 시 상세 페이지로 이동 (예시)
const goDetail = (reserveNo) => {
  location.href = `/admin/reserve/detail/${reserveNo}`;
}
