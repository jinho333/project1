//리뷰 등록 유효성 검사
const reviewValigate = () => {
  const rate = document.querySelector('input[name="rating"]:checked');

  document.querySelector('#contentP').textContent = '';

  if(rate == null){
    document.querySelector('#contentP').textContent = '별점을 선택해주세요.';
    return false;
  }
  return true;
}

//리뷰 등록 버튼 클릭시
const goReview = () => {
  const result = reviewValigate();
  if(result){
    document.querySelector('#review-form').submit();
  }
}
