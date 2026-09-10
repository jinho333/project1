
//로그인 유효성 검사 -> 성공 시 return true
const loginValigate = (memId, memPw) => {
  let result = true;

  const p_tag = document.querySelector('.login .valigate-p');

  if(memId === '' || memPw === ''){
    p_tag.textContent = '아이디 또는 비번을 입력하지 않았습니다.';
    result = false;
  }

  return result;
}

//로그인 버튼 클릭시
const login = () => {
  const memId = document.querySelector('input[name="memId"]').value;
  const memPw = document.querySelector('input[name="memPw"]').value;

  const result = loginValigate(memId, memPw);

  if(result){
    axios
    .get(`/member-api/login?memId=${memId}&memPw=${memPw}`)
    .then(response => {
      if(response.data === ''){
        document.querySelector('.login .valigate-p').textContent='아이디 또는 비밀번호를 잘못 입력했습니다.'
      }
      else{
        //일반회원 로그인 -> 홈으로
        if(response.data.memRole === 'USER'){
          location.href='/';
        }
        //관리자 로그인 -> 대시보드
        else{
          location.href='/admin';
        }
      }
    })
    .catch(error => {
      console.log('로그인 중 오류 발생')
      console.log(error);
    });
  }
}
