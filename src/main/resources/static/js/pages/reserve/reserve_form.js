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

//주소변경 버튼 클릭 시 변경 주소api
const searchPostCode = () => {
   new kakao.Postcode({
    oncomplete: function(data) {
     document.querySelector('#newAddr').textContent = data.roadAddress;
     //상세주소 입력 태그로 변경?이 필요하다
     const addr_tag = document.querySelector('#newAddr');
     document.querySelector('#newAddrDetail').innerHTML='';
     let str = ''
     str = `
     <input type="text" name="addrDetail" id="newAddrDetail">
     `
      addr_tag.insertAdjacentHTML("afterend", str);
    }
  }).open();
}