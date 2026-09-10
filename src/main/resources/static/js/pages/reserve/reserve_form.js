//예약 1단계 유효성 검사
const reserveFormValigate = () => {
  const product = document.querySelector('input[name="product"]:checked');
  const symptom = document.querySelector('textarea[name="symptom"]').value;

  const symptom_regex = /^[a-zA-Z0-9가-힣\s\p{P}]{5,500}$/u;

  const p_tags = document.querySelectorAll('.valigate-p');
  for(const p_tag of p_tags)
    p_tag.textContent = '';

  let result = true;

  if(product == null){
    document.querySelector('#productP').textContent = '기기를 선택해주세요.';
    result = false;
  }
  if(!symptom_regex.test(symptom)){
    document.querySelector('#symptomP').textContent = '고장 증상을 5글자 이상 상세히 입력해주세요.';
    result = false;
  }

  return result;
}

//시간 선택하기 버튼 클릭시
const goReserveStep2 = () => {
  const result = reserveFormValigate();

  if(result){
    document.querySelector('#reserve-form').submit();
  }
}
