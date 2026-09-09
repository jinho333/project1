//예약 확정 유효성 검사
const reserveTimeValigate = () => {
  const time = document.querySelector('input[name="time"]:checked');

  document.querySelector('#timeP').textContent = '';

  if(time == null){
    document.querySelector('#timeP').textContent = '방문 시간을 선택해주세요.';
    return false;
  }
  return true;
}

//예약 확정하기 버튼 클릭시
const goComplete = () => {
  const result = reserveTimeValigate();
  if(result){
    document.querySelector('#reserve-time-form').submit();
  }
}
