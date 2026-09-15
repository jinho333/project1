//예약 첫 페이지 (냉방으로 카테고리 설정)
window.onload = () => {
  goProduct('냉방', document.querySelector('#coolbtn'));
}

//냉,난방 버튼 클릭 시 실행 함수
// 카테고리 목록을 페이지 로드 시 미리 다 받아두지 않고, 탭을 누를 때마다 axios로 새로 조회한다.
// 냉방/난방 카테고리를 한 번에 같이 받아서 클라이언트에서 나눠 보여줘도 되지만,
// 서버(/reserve-api/productType)가 이미 productType별로 나눠서 내려주고 있어서
// 탭 클릭 시점에 필요한 것만 요청하는 쪽이 코드가 더 단순하다고 판단했다.
const goProduct = (productType, btn) => {
  //액티브 클래스 삭제
  document.querySelector('#coolbtn').classList.remove('active');
  document.querySelector('#heatbtn').classList.remove('active');

  console.log(productType);
  //this 버튼active 활성화
  btn.classList.add('active');

  if(productType === '냉방'){
    axios
    .get('/reserve-api/productType?productType=냉방')
    .then((response) => {
      //데이터로 화면 그리기!
      iconGrid(response.data)
    })
    .catch((error) => {
      console.log(error);
      console.log('카테고리 조회 실패');
    });
  }
  else{
    axios
    .get('/reserve-api/productType?productType=난방')
    .then((response) => {
      iconGrid(response.data);
      
    })
    .catch((error) => {
      console.log(error);
      console.log('카테고리 조회 실패');
    });
  }
}

//아이콘
// CATEGORY 테이블에 아이콘 컬럼이 없어서, 카테고리명 문자열을 기준으로 아이콘을 매칭한다.
// 카테고리 종류가 6개뿐이라 이 정도 하드코딩이 DB 스키마를 바꾸는 것보다 간단하다고 판단했다.
const getIcon = (categoryName) => {
  if(categoryName === '에어컨') {return 'bi-snow2'};
  if(categoryName === '실외기') {return 'bi-fan'};
  if(categoryName === '시스템에어컨') {return 'bi-thermometer-snow'};
  if(categoryName === '보일러') {return 'bi-fire'};
  if(categoryName === '온수기') {return 'bi-droplet-fill'};
  if(categoryName === '히터') {return 'bi-thermometer-sun'};
  return 'bi bi-three-dots';
}

//화면 함수
const iconGrid = (categoryList) => {
  const product_grid = document.querySelector('#product-grid');
  product_grid.innerHTML = '';

  for(let i = 0; i < categoryList.length; i++){
    const c = categoryList[i];
    const icon = getIcon(c.categoryName);
    const checked = (i === 0) ? 'checked' : '';

    product_grid.innerHTML += `
      <label class="radio-card">
        <input type="radio" name="categoryNo" value="${c.categoryNo}" class="radio-card__input" ${checked}>
          <div class="radio-card__body product-card">
            <i class="bi ${icon}"></i>
            <span>${c.categoryName}</span>
          </div>
      </label>
    `
  }

}

//예약 1단계 유효성 검사
const reserveFormValigate = () => {
  const product = document.querySelector('input[name="categoryNo"]:checked');
  const symptom = document.querySelector('textarea[name="symptom"]').value;

  // 최소 5자: 너무 짧으면("ㅁ" 한 글자 등) 기사가 증상을 파악할 수 없어서 최소한의 정보량을 강제.
  // 최대 500자: DB 컬럼(SYMPTOM VARCHAR(500))을 넘는 값이 저장 실패로 이어지지 않도록 서버 제약과 맞춤.
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
     document.querySelector('#newAddr').value = data.roadAddress;
    
      const addrDetailInput = document.querySelector('#newAddrDetail');
      addrDetailInput.value = '상세주소를 입력하세요.';
      
      addrDetailInput.addEventListener('focus', function () {
        this.value = '';
      }, { once: true });
    }
  }).open();
}
