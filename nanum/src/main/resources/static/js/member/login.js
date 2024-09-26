$(document).ready(function () {

    // 로그인 버튼 클릭 이벤트
    $('.loginButton').on('click', function(event) {
            
            // 기본 폼 전송 방지
            event.preventDefault();

            // 입력 값 가져오기
            let userId = $('#id').val().trim();
            let userPassword = $('#password').val().trim();

            // 아이디 입력 확인
            if (userId === "") {
                // 아이디가 비어 있으면 오류 메시지 표시
                $('.idError').html("아이디를 입력해주십시오.");
                return;
            }else{
                // 입력했으면 초기화 시켜줌
                $('.idError').html("");
            }

            // 비밀번호 입력 확인
            if (userPassword === "") {
                // 비밀번호가 비어 있으면 오류 메시지 표시
                $('.passwordError').html("비밀번호를 입력해주십시오.");
                return;
            }else{
                // 비밀번호가 입력되어 있었으면 공백처리
                $('.passwordError').html("");
            }
            //로그인 진행
            $("#loginPost").submit();

    });

}); // ******************** ready 끝 *********************************