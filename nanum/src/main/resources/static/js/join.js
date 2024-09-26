$(document).ready(function () {
    // 회원가입에 필요한 인증절차들의 값을 ""으로 초기화시킨다. 
    // 아래의 id중복확인 nickname 중복확인 email인증이 모두 시행되어야 회원가입이 되도록 한다. 
    // 비밀번호 규약통과 여부 초기화 
    $("#idDuplicationCheck").val("");
    $("#nickDuplicationCheck").val("");
    $("#emailDuplicationCheck").val("");
    $('#passwordDuplicationCheck').val("");

    // ID 입력시 keyup 이벤트로 유효성 검사 
    $('#memberId').keyup(idCheck);

    // 입력한 내용을 확인하고 DB에 저장
    $("#joinPost").submit(check);

    //비밀번호 유효성 검사 (keyup 시 )
    $("#memberPw").on("keyup", function() {
        // 입력 필드 값 가져오기
        let memberPw = $(this).val().trim();
        // 비밀번호 길이 확인 (8자 이상인지)
        if (memberPw.length < 8) {
            $(".passwordError").text("비밀번호는 8자 이상으로 입력해 주세요.").show().css("color", "red");
        }
        else {
            $(".passwordError").text("사용가능한 비밀번호 입니다.").show().css("color", "blue");
        }
    });

  //비밀번호확인 검사
  $("#pwCheck").on("keyup", function() {
        // 입력 필드 값 가져오기
        let memberPw = $("#memberPw").val().trim();
        let pwCheck = $(this).val().trim();

        // 비밀번호 일치여부 확인
        if (memberPw === pwCheck) {
            $(".pwCheckError").text("비밀번호가 일치합니다.").show().css("color", "blue");
            // 비밀번호 규약을 제대로 통과하였음으로 통과했다는 의미의 y값을 넣어줌.
            $('#passwordDuplicationCheck').val("y");
        }
        if (memberPw !== pwCheck) {
            $(".pwCheckError").text("비밀번호가 일치하지 않습니다.").show().css("color", "red");
            // 비밀번호 규약을 제대로 지키지 못했음으로 초기화
            $('#passwordDuplicationCheck').val("");
        }
  });

});
//****************************ready 함수 종료 **********************************

// 아이디 중복확인 클릭시 실행되는 함수
function idDoubleCheck() {
  let memberId = $("#memberId").val().trim();
    if (memberId === "") {
      $(".idError").text("아이디를 입력하세요").show().css("color", "red");
      return;
    }
    // 아이디 중복확인 ajax 요청 
    $.ajax({
      url: '/find/idCheck',
      type: 'GET',
      data: { memberId: memberId },
      success: function (available) {
          // 아이디가 있으면 true 없으면 false 값을 가져온다. 
          if (available) { 
              // 이미 존재하는 아이디일 때
              $(".idError").text("이미 존재하는 아이디입니다.").show().css("color", "red");
          } else {
              // 사용 가능한 아이디일 때
              $(".idError").text("중복확인 완료.").show().css("color","blue");
              // 인증절차 승인을 위해서 값을 "y"로 변경시킨다. 
              $("#idDuplicationCheck").val("y");
              // id input태그를 비활성화 
              $('#memberId').attr('readonly', true);
          }
      },
      error: function () {
        alert("아이디 중복체크에 실패하였습니다.");
      }
    })
}

/**
    * 아이디 유효성 검사 + 중복 확인 함수
*/
function idCheck() {
    
    // 사용자가 입력한 아이디
    let memberId = $("#memberId").val().trim();

    // 오류 메시지를 초기화
    $(".idError").hide();

    // 아이디 입력 확인
    if (memberId === "") {
        $(".idError").text("아이디를 입력해 주세요.").show().css("color", "red");
        return false;
    }
    
    // 아이디 길이 확인
    else if (memberId.length < 4 || memberId.length > 12) {
        $(".idError").text("아이디는 4 ~ 12자 이내로 입력해주세요.").show().css("color", "red");
        return false;
    }

    // 아이디 유효성 검사 (영문, 숫자, 언더바만 허용)
    else if (!/^[A-Za-z0-9_]+$/.test(memberId)) {
        $(".idError").text("아이디는 영문, 숫자, 언더바만 가능합니다.").show().css("color", "red");
        return false;
    }

    else {
        $(".idError").text("사용 가능한 아이디입니다.").show().css("color", "blue");
        return true;
    }

};  // idDoubleCheck()

// 이메일 형식을 확인하는 정규 표현식 함수
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// 이메일 전송함수 
function sendEmailVerificationCode(){
      // 전송할 email 값을 가져온다. 
      const email = $("#memberEmail").val().trim();
      
      //이메일 형식에 맞지 않으면 alert 창 표시 
      if (!isValidEmail(email)) {  // 이메일 형식이 맞지 않으면
      $("#emailError").text("유효한 이메일 주소를 입력하세요.").show().css("color", "red"); // 경고 메시지
      return;  // 전송 중단
      }
      
      $("#emailError").text("이메일 전송중입니다...").show().css("color", "blue"); 

      // ajax로 받은 이메일에 이메일 전송 요청
      $.ajax({
          url: '/member/sendEmail', 
          type: 'post',
          data: { email: email },
          success: function (response) {
              // if문으로 response 값이 true 이면 중복되는 값이 있고 false 면 중복되는 값이 없다.
              if(response){
                // 전달받은 값이 true 면 중복임으로 경고 메시지 
                $("#emailError").text("이미 존재하는 이메일입니다.").show().css("color", "red"); // 경고 메시지
              }else{
                // 전달받은 값이 false 면 중복되지 않고 이메일이 전송되었음으로 아래 메시지 출력
                $("#emailError").text("이메일 전송에 성공하였습니다.").show().css("color", "blue"); 
              }

          },
          error: function () {
              // ajax 요청 실패시 메시지 보여준다.
              $("#emailError").text("이메일 전송에 요청에 실패했습니다.").show().css("color", "red"); // 경고 메시지
          }
      });        
}

// 이메일 인증번호 검증
function verifyEmailCode(){
    // 인증번호 
    const emailCode = $("#emailCode").val();
    // ajax로 인증번호 검증 요청
    $.ajax({
        url: '/member/verifyEmail',
        type: 'POST',
        data: { emailCode : emailCode },
        // ajax로 요청 성공시 "인증 성공" or "인증 실패" 둘중 하나의 메시지를 전달받는다. 
        success: function (response) {
          // 인증성공, 인증실패 결과에 따라 띄워준다.  
          if(response == "인증 성공"){
            // 인증성공 메시지 출력
            $("#emailCodeError").text(response).show().css("color", "blue");
            // 성공시 인증확인을 위한 emailDuplicationCheck를 y로 변경  
            $("#emailDuplicationCheck").val("y");
            // 인증성공시 이메일과 인증번호를 수정불가 하도록 변경
            $('#memberEmail').attr('readonly', true);
            $('#emailCode').attr('readonly', true);
          } else{
            // 리턴받은 경고메시지를 출력 
            $("#emailCodeError").text(response).show().css("color", "red"); // 경고 메시지
          }
        },
        error: function () {
          // 인증번호 요청 실패시 alert 출력 
          $("#emailCodeError").text("인증번호 확인 요청에 실패했습니다.").show().css("color", "red"); // 경고 메시지
        }
    });
}

// 닉네임 중복확인 클릭시 실행되는 함수
function nickDoubleCheck() {
  let memberNickname = $("#memberNickname").val().trim();
    if (memberNickname === "") {
      $("#nickError").text("닉네임을 입력하세요").show().css("color", "red");
      return;
    }
    // 아이디 중복확인 ajax 요청 
    $.ajax({
      url: '/find/nickCheck',
      type: 'GET',
      data: { memberNickname : memberNickname },
      success: function (available) {
          // 닉네임이 있으면 true 없으면 false 값을 가져온다. 
          if (available) { 
              // 이미 존재하는 닉네임일 때
              $(".nickError").text("이미 존재하는 닉네임입니다.").show().css("color", "red");
          } else {
              // 사용 가능한 닉네임일 때
              $(".nickError").text("중복확인 완료.").show().css("color","blue");
              // 인증절차 승인을 위해서 값을 "y"로 변경시킨다. 
              $("#nickDuplicationCheck").val("y");
              // id input태그를 비활성화 
              $('#memberNick').attr('readonly', true);
          }
      },
      error: function () {
        alert("닉네임 중복체크에 실패하였습니다.");
      }
    })
}

//제출전 체크 
function check() {
  //3가지 인증절차를 모두 통과하였는지 확인하기 위해 값을 가져온다. 값이 "y"가 아니면 인증이 모두 성공하지 못한것이다. 
  let idDuplicationCheck = $("#idDuplicationCheck").val();
  let nickDuplicationCheck = $("#nickDuplicationCheck").val();
  let emailDuplicationCheck = $("#emailDuplicationCheck").val();
  let passwordDuplicationCheck = $('#passwordDuplicationCheck').val();

  // 아이디 중복확인 절차를 통과햐였는지 확인. 
  if (idDuplicationCheck != "y") {
    $(".idError").text("아이디 중복 확인이 이루어지지 않았습니다.").show().css("color", "red");
    return false;
  }
  
  // 비밀번호 규약을 제대로 인증받았는지 확인 
  if (passwordDuplicationCheck != "y"){
    $('.passwordError').text("비밀번호를 정확히 입력해 주십시오.").show().css("color", "red");
    return false;
  }
  
  // 이메일 인증절차를 통과하였는지 확인 
  if (emailDuplicationCheck!= "y") {
    $('.emailError').text("이메일을 인증하여 주십시오.").show().css("color", "red");
    return false;
  }

  // 닉네임 인증절차를 통과하였는지 확인 
  if (nickDuplicationCheck != "y") {
    $('.nickError').text("닉네임 중복확인이 이루어지지 않았습니다.").show().css("color", "red");
    return false;
  }        

}