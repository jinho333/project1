
//------------- 모달 관련 함수 ----------------
//모달창 오픈(active 클래스 추가)
const openModal = (modal_id) => {
  const modal_tag = document.querySelector(modal_id);
  modal_tag.classList.add('active');
}

//모달창 닫기(active 클래스 제거)
const closeModal = (modal_id) => {
  const modal_tag = document.querySelector(modal_id);
  modal_tag.classList.remove('active');

  //모달창 안의 폼요소들 초기화
  const form_tag = modal_tag.querySelector('form');
  if(form_tag != null){
    form_tag.reset();
  }

  //유효성 검사 결과 p태그 초기화
  const p_tags = document.querySelectorAll('.valigate-p');
  for(const p_tag of p_tags){
    p_tag.textContent = '';
  }
}
