$(document).ready(function() {
    // 입력한 내용을 확인하고 아이디를 보여줌 
    $("#findButton").click(check);
});

//제출전 체크
function check() {
    //3가지 인증절차를 모두 통과하였는지 확인하기 위해 값을 가져온다. 값이 "y"가 아니면 인증이 모두 성공하지 못한것이다.
    let emailDuplicationCheck = $("#emailDuplicationCheck").val();

    // 이메일 인증절차를 통과하였는지 확인
    if (emailDuplicationCheck != "y") {
      $("#emailCodeError")
        .text("이메일을 인증하여 주십시오.")
        .show()
        .css("color", "red");
    }else{
        console.log("모달 띄우는 작업 시작");
        console.log(emailDuplicationCheck);

        // 모달 요소 가져오기
        var modal = document.getElementById("myModal");  
        // 바로 보여주기 
        modal.style.display = "block";

          // 모달의 '닫기' 버튼 클릭 시 모달창 닫기
        var span = document.getElementsByClassName("close")[0];
        span.onclick = function() {
          modal.style.display = "none";
        }  
          // 모달의 '확인' 버튼 클릭 시 login 페이지로 이동
        var confirmButton = document.getElementById("confirmButton");
        confirmButton.onclick = function() {
          window.location.href = "/member/login"; // login 페이지로 리디렉션
        }  
          // 모달 외부 클릭 시 모달 닫기
        window.onclick = function(event) {
          if (event.target == modal) {
            modal.style.display = "none";
          }
        }
    }
  }


// 이메일 형식을 확인하는 정규 표현식 함수
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }
  
  // 아이디 찾기 이메일 전송함수
  function sendEmailVerificationCode() {
    // 전송할 email 값을 가져온다.
    const email = $("#memberEmail").val().trim();
  
    //이메일 형식에 맞지 않으면 alert 창 표시
    if (!isValidEmail(email)) {
      // 이메일 형식이 맞지 않으면
      $("#emailError")
        .text("유효한 이메일 주소를 입력하세요.")
        .show()
        .css("color", "red"); // 경고 메시지
      return; // 전송 중단
    }
  
    // 이메일 전송시 이메일 전송중 메시지 출력
    $("#emailError").text("이메일 전송중입니다...").show().css("color", "blue");
  
    // ajax로 받은 이메일에 이메일 전송 요청
    $.ajax({
      url: "/member/FindSendEmail",
      type: "post",
      data: { email: email },
      success: function (response) {
        // if문으로 response 값이 true 이면 중복되는 값이 있고 false 면 중복되는 값이 없다.
        if (response) {
          // 전달받은 값이 true 면 존재하지 않는 email임으로 경고 메시지
          $("#emailError")
            .text("존재하지 않는 이메일 입니다.")
            .show()
            .css("color", "red"); // 경고 메시지
        } else {
          // 전달받은 값이 false 면 중복되지 않고 이메일이 전송되었음으로 아래 메시지 출력
          $("#emailError")
            .text("이메일 전송에 성공하였습니다.")
            .show()
            .css("color", "blue");
          // memberEmail input태그를 비활성화
          $("#memberEmail").attr("readonly", true);
        }
      },
      error: function () {
        // ajax 요청 실패시 메시지 보여준다.
        $("#emailError")
          .text("이메일 전송에 요청에 실패했습니다.")
          .show()
          .css("color", "red"); // 경고 메시지
      },
    });
  }
  
  // 이메일 인증번호 검증
  function verifyEmailCode() {
    // 인증번호
    const emailCode = $("#emailCode").val();
    // 전송할 email 값을 가져온다.
    const email = $("#memberEmail").val();
    // ajax로 인증번호 검증 요청
    $.ajax({
      url: "/member/idFindverifyEmail",
      type: "POST",
      data: { emailCode: emailCode , email: email },
      // ajax로 요청 성공시 "인증 성공" or "인증 실패" 둘중 하나의 메시지를 전달받는다.
      success: function (response) {
        console.log(response.memberId);
        // 인증성공, 인증실패 결과에 따라 띄워준다.
        if (response != null) {
          // 인증성공 메시지 출력
          $("#emailCodeError").text("인증 성공").show().css("color", "blue");
          // 성공시 인증확인을 위한 emailDuplicationCheck를 y로 변경
          $("#emailDuplicationCheck").val("y");
          // 인증번호를 수정불가 하도록 변경
          $("#emailCode").attr("readonly", true);
          // 모달창에 아이디 추가
          $("#idCheck").text(response.memberId);
        } else {
          // 리턴받은 경고메시지를 출력
          $("#emailCodeError").text("인증 실패").show().css("color", "red"); // 경고 메시지
        }
      },
      error: function () {
        // 인증번호 요청 실패시 alert 출력
        $("#emailCodeError")
          .text("인증번호 확인 요청에 실패했습니다.")
          .show()
          .css("color", "red"); // 경고 메시지
      },
    });
  }


