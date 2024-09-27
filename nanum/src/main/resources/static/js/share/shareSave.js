
$(document).ready(function () {

    // 폼 제출 이벤트 제어 
    $("#Save").submit(function (event) {
        // 제목과 내용 입력 여부 확인
        var title = $("#writeTitle").val();
        var content = $("#postContent").val();
        var lat = $("#lat").val();
        var lng = $("#lng").val();
        var file = $("#file").val();
        if (title.trim() === "" || content.trim() === "" || lat.trim() === "" || lng.trim() === "" || file.trim() === ""){
            alert("제목, 내용, 사진, 위치를 모두 입력해주세요.");
            event.preventDefault(); // 폼 제출 방지
            return;
        }
    })
    // + 버튼을 누르면 사진이 업로드되는 기능 추가 .
    $('#addPhoto').click(function() {
        $('#file').click();
    })
    // // submitButton 클릭시 Save form submit 버튼만으로도 submit 가능함으로 중복이된다.
    // $('#submitButton').click(function() {
    //     $('#Save').submit();
    // });

    // 제목 글자수 제한
    const titleInput = document.getElementById('writeTitle');
    const maxTitleLength = 25; // 제목 글자수 제한

    titleInput.addEventListener('input', function () {
    if (titleInput.value.length > maxTitleLength) {
        alert("제목은 최대 " + maxTitleLength + "자까지 입력 가능합니다.");
        titleInput.value = titleInput.value.slice(0, maxTitleLength); // 글자수 제한
    }
    });

    // 본문 글자수 제한
    const contentInput = document.getElementById('postContent');
    const maxContentLength = 150; // 본문 글자수 제한

    contentInput.addEventListener('input', function () {
    if (contentInput.value.length > maxContentLength) {
        alert("본문은 최대 " + maxContentLength + "자까지 입력 가능합니다.");
        contentInput.value = contentInput.value.slice(0, maxContentLength); // 글자수 제한
    }
    });
    
})

// 올라오는 파일의 갯수를 제한하는 함수 
function checkFileLimit(event) {
    // 사용자가 새로 선택한 파일들
    const files = event.target.files;
    // 최대 업로드 갯수 제한
    const maxFiles = 5;
    // 새로 선택한 파일들의 수 확인
    const totalFiles = files.length;
    // 만약 새로 선택한 파일 수가 최대 갯수를 초과하면 경고하고 막음
    if (totalFiles > maxFiles) {
        alert(`사진은 최대 ${maxFiles}장까지만 업로드할 수 있습니다.`);
        // 파일 선택 초기화 (선택된 파일을 지움)
        event.target.value = '';  // 파일 선택 초기화
        // 미리보기 초기화 
        resetPreview();  
        return;
    }
    // 미리보기 초기화 - 새로 파일을 선택할 때마다 기존 미리보기를 초기화
    resetPreview();
    
    // 선택된 파일들이 5개를 초과하지 않았을 경우 이미지 미리보기 함수 실행
    previewImages(files);
}
// 사진 미리보기 함수 
function previewImages(files) {
    
    // 미리보기 이미지를 넣을 태그를 배열로 선택 요소 선택
    let previewContainer = document.querySelectorAll('.preview'); 
    //각각의 태그에 미리보기 사진을 넣어준다. 
    for (let i = 0; i < files.length; i++) {
        const reader = new FileReader();
        // 파일을 읽는 작업을 완료한 후 이벤트 호출 
        reader.onload = function(e) {
            // img 태그 생성 
            let img = document.createElement('img');
            // img 태그의 src 경로 정의 
            img.src = e.target.result;
            img.style.maxWidth = "100px";
            img.style.margin = "10px";
            // 이미지를 미리 볼 div에 추가
            previewContainer[i].appendChild(img);
        }
        // 사용자가 선택한 파일을 미리 볼 수 있도록 변환 작업 시행
        reader.readAsDataURL(files[i]); 
    }
}
// 미리보기 초기화 함수     
function resetPreview() {
    // 미리보기 이미지를 넣을 태그를 배열로 선택 요소 선택
    let previewContainer = document.querySelectorAll('.preview'); 
    // 반복문을 이용하여 img를 넣기전 태그를 초기화 해준다.
    for (let i = 0; i < 5; i++) {
        previewContainer[i].innerHTML = "";
    }
}


//*********************************************마이페이지 옆 쪽지 ******************************************** */
$(document).ready(function () {
    // 읽지 않은 쪽지 개수 가져오기
    updateUnreadCount();
  
    // 쪽지 목록 열기 모달
    $("#messages").click(function () {
      $("#messagesModal").fadeIn();
      updateUnreadCount(); // 쪽지 모달 열릴 때마다 개수 업데이트
  
      let memberNum = $("#messages").data("num");
  
      // 회원 번호를 통해 속한 쪽지방을 모두 가져옴
      $.ajax({
        url: "/message/rooms",
        method: "get",
        data: { memberNum: memberNum },
        success: function (data) {
          console.log("서버에서 반환된 데이터:", data);
          let currentUserNum = $("#messages").data("num"); // 현재 로그인한 사용자의 번호
          let messageList = "";
          data.forEach(function (room) {
            console.log("발신자 번호 (senderNum):", room.senderNum);
            console.log("현재 사용자 번호 (currentUserNum):", currentUserNum);
  
            // 쪽지를 보낸 사람이 현재 로그인한 사용자가 아니고, 읽지 않은 쪽지가 있을 때 'New!' 표시
            let newBadge =
              room.hasUnreadMessages && room.senderNum !== currentUserNum
                ? "<span style='color: red;'>New!</span>"
                : "";
  
            messageList += `
                <div class="message-room" data-room="${
                  room.roomNum
                }" data-share-num="${room.shareNum}">
                  <div class="shareBoard" data-write="${
                    room.shareWriteNum
                  }" data-complete="${room.shareCompleted}">
                    <strong>게시글:</strong> ${room.shareTitle} ${newBadge} 
                  </div>
                  <div class="sender" data-num="${room.receiverNum}">
                    <strong>상대방:</strong> ${room.senderNickname}
                  </div>
                  <div>
                    <strong>메시지:</strong> ${
                      room.messageContents || "메시지가 없습니다."
                    }
                  </div>
                  <div>
                    <strong>시간:</strong> ${new Date(
                      room.deliverDate
                    ).toLocaleString()}
                  </div>
                </div>`;
          });
          $("#messageListContainer").html(messageList);
        },
        error: function (xhr, status, error) {
          console.error("AJAX 요청 오류:", status, error);
        },
      });
    });
  
    // 쪽지방 클릭 시 상세보기 및 쪽지 전송 모달 표시
    $(document).on("click", ".message-room", function () {
      // 다른 방에서 active 클래스 제거
      $(".message-room").removeClass("active");
      // 현재 선택한 방에 active 클래스 추가
      $(this).addClass("active");
  
      let roomNum = $(this).data("room");
      let userNum = $("#messages").data("num");
      let shareNum = $(this).data("share-num");
      console.log(
        "방번호:",
        roomNum,
        "로그인한 유저번호:",
        userNum,
        "게시글 번호:",
        shareNum
      );
      $("#detailsModal").fadeIn();
  
      // 쪽지 상세 목록에서 게시글로 이동하는 클릭 이벤트
      $("#goToShareBoard").click(function () {
        let shareBoardUrl = `/share/read?shareNum=${shareNum}`; // 게시글 상세 페이지로 이동하는 URL
        window.open(shareBoardUrl, "_blank"); // 새 창에서 열기
      });
  
      // 쪽지 상세 내역 가져오기
      $.ajax({
        url: "/message/details",
        method: "GET",
        data: { roomNum: roomNum, userNum: userNum },
        success: function (data) {
          // 상대방의 닉네임과 프로필 사진을 모달에 업데이트
          $("#receiverNickname").text(data.receiverNickname);
          $("#receiverProfileImage").attr("src", data.receiverProfileImage); // 이미지 URL 업데이트
          let detailsList = "";
          data.messages.forEach(function (message) {
            detailsList += `
                <div class="message-detail">
                  <strong>${message.senderNickname}:</strong> ${
              message.messageContents
            }
                  <br/>
                  <small>${new Date(message.deliverDate).toLocaleString()}</small>
                </div>
                <hr />`;
          });
          $("#messageDetailsContainer").html(detailsList);
  
          // 현재 선택한 방의 정보를 가져옴
          let selectedRoom = $(".message-room[data-room='" + roomNum + "']");
          let thisShareNum = parseInt(
            selectedRoom.find(".message-room").data("share-num")
          );
          let shareWriteNum = parseInt(
            selectedRoom.find(".shareBoard").data("write")
          );
          let shareCompletedData = selectedRoom
            .find(".shareBoard")
            .data("complete");
          let shareCompleted =
            shareCompletedData === true || shareCompletedData === "true";
          console.log(
            "게시글 번호:",
            thisShareNum,
            "게시글주인:",
            shareWriteNum,
            "완료여부:",
            shareCompleted
          );
          // 나눔 완료 버튼 표시 및 비활성화 설정
          if (userNum === shareWriteNum) {
            $("#shareComplete").show();
  
            if (shareCompleted) {
              $("#shareComplete").prop("disabled", true);
            } else {
              $("#shareComplete").prop("disabled", false);
            }
          } else {
            $("#shareComplete").hide();
          }
        },
        error: function (xhr, status, error) {
          console.error("AJAX 요청 오류:", status, error);
        },
      });
    });
  
    // 메시지 보내기
    $("#sendMessageBtn").click(function () {
      let roomNum = $(".message-room.active").data("room");
      let messageContents = $("#messageInput").val();
      console.log("방번호:", roomNum, "내용:", messageContents);
  
      $.ajax({
        url: "/message/detailSend",
        method: "POST",
        data: { roomNum: roomNum, messageContents: messageContents },
        success: function (response) {
          $("#messageInput").val(""); // 입력 필드 초기화
  
          // 메시지 전송 후 상세 내역을 다시 가져오기
          $.ajax({
            url: "/message/details",
            method: "GET",
            data: { roomNum: roomNum, userNum: $("#messages").data("num") },
            success: function (data) {
              // 상대방의 프로필과 닉네임 업데이트
              $("#receiverNickname").text(data.receiverNickname);
              $("#receiverProfileImage").attr("src", data.receiverProfileImage); // 프로필 사진 URL 업데이트
  
              let detailsList = "";
              data.messages.forEach(function (message) {
                detailsList += `
            <div class="message-detail">
              <strong>${message.senderNickname}:</strong> ${
                  message.messageContents
                }
              <br/>
              <small>${new Date(message.deliverDate).toLocaleString()}</small>
            </div>
            <hr />`;
              });
              $("#messageDetailsContainer").html(detailsList); // 상세 메시지 갱신
            },
            error: function (xhr, status, error) {
              console.error("상세 메시지 가져오기 오류:", status, error);
            },
          });
        },
        error: function (xhr, status, error) {
          console.error("메시지 전송 오류:", status, error);
        },
      });
    });
  
    // 나눔 이야기 닫기
    $("#messagesClose").click(function () {
      $("#messagesModal").fadeOut();
      updateUnreadCount(); // 모달 닫을 때 안 읽은 쪽지 개수 업데이트
    });
  
    // 쪽지 상세내역 닫기
    $("#detailsClose").click(function () {
      $("#detailsModal").fadeOut();
      // 쪽지 목록 갱신을 위해 다시 목록 불러오기
      updateMessageRooms();
    });
  
    $(window).click(function (event) {
      if (
        $(event.target).is("#messagesModal") ||
        $(event.target).is("#detailsModal")
      ) {
        $("#messagesModal").fadeOut();
        $("#detailsModal").fadeOut();
        updateUnreadCount(); // 모달 닫을 때 안 읽은 쪽지 개수 업데이트
        // 쪽지 목록 갱신을 위해 다시 목록 불러오기
        updateMessageRooms();
      }
    });
  
    // 쪽지에서 '신고하기' 버튼 클릭 시 신고 모달 열기
    $("#userReportBtn").click(function () {
      $("#reportModal").show();
    });
  
    // 신고 모달 닫기
    $(".close").click(function () {
      $("#reportModal").hide();
    });
  
    // 모달 창 바깥 부분을 클릭하면 모달 창 닫기
    $(window).click(function (event) {
      if ($(event.target).is("#reportModal")) {
        $("#reportModal").hide();
      }
    });
  
    // 신고 제출 버튼 클릭 시 신고 내역 제출
    $("#submitUserReportBtn").click(function () {
      submitReport();
    });
  
    // 나눔 완료 버튼 클릭 시
    $("#shareComplete").click(function () {
      let shareNum = $(".message-room.active").data("share-num");
      let receiverNum = $(".message-room.active").find(".sender").data("num");
      console.log("게시글:", shareNum, "받는사람:", receiverNum);
  
      $.ajax({
        url: "/share/complete",
        method: "POST",
        data: { shareNum: shareNum, receiverNum: receiverNum },
        success: function (response) {
          alert("나눔이 완료되었습니다.");
          $("#shareComplete").prop("disabled", true); // 버튼 비활성화
          // 나눔 완료 후 쪽지 목록 갱신
          let memberNum = $("#messages").data("num");
          $.ajax({
            url: "/message/rooms",
            method: "get",
            data: { memberNum: memberNum },
            success: function (data) {
              let messageList = "";
              data.forEach(function (room) {
                messageList += `
            <div class="message-room" data-room="${
              room.roomNum
            }" data-share-num="${room.shareNum}">
              <div class="shareBoard" data-write="${
                room.shareWriteNum
              }" data-complete="${room.shareCompleted}">
                <strong>게시글:</strong> ${room.shareTitle}
              </div>
              <div class="sender" data-num="${room.receiverNum}">
                <strong>상대방:</strong> ${room.senderNickname}
              </div>
              <div>
                <strong>메시지:</strong> ${
                  room.messageContents || "메시지가 없습니다."
                }
              </div>
              <div>
                <strong>시간:</strong> ${new Date(
                  room.deliverDate
                ).toLocaleString()}
              </div>
            </div>`;
              });
              $("#messageListContainer").html(messageList);
            },
            error: function (xhr, status, error) {
              console.error("AJAX 요청 오류:", status, error);
            },
          });
        },
        error: function () {
          alert("나눔 완료 처리 중 오류가 발생했습니다.");
        },
      });
    });
  });
  
  // 신고 데이터 전송 함수
  function submitReport() {
    let reportReasons = [];
    $("input[name='reportReason']:checked").each(function () {
      reportReasons.push($(this).val());
    });
    let additionalReason = $("#additionalReason").val();
    let shareNum = $(".message-room.active").data("share-num");
    let reporterNum = $("#messages").data("num"); // 사용자의 ID
    let memberNum = $(".message-room.active").find(".sender").data("num"); // 상대 ID
  
    console.log(
      "사유:",
      additionalReason,
      "게시글번호:",
      shareNum,
      "나:",
      reporterNum,
      "신고당한 사람:",
      memberNum
    );
    // Ajax 요청으로 신고 데이터 전송
    $.ajax({
      url: "/report/submit",
      type: "post",
      data: {
        memberNum: memberNum,
        reporterNum: reporterNum,
        reportReason: JSON.stringify(reportReasons),
        additionalReason: additionalReason,
        shareNum: shareNum,
      },
      success: function (response) {
        alert("신고가 접수되었습니다.");
        $("#reportModal").hide();
      },
      error: function (xhr) {
        if (xhr.status === 409) {
          // Conflict 상태일 때
          alert(xhr.responseText); // 이미 신고한 유저에 대한 메시지 출력
        } else {
          alert("신고 처리 중 오류가 발생했습니다.");
        }
      },
    });
  }
  
  function updateUnreadCount() {
    let memberNum = $("#messages").data("num");
    console.log("로그인한 번호:", memberNum);
    $.ajax({
      url: "/message/rooms/unreadCount/" + memberNum, // 현재 로그인한 사용자의 쪽지 개수 API 호출
      method: "get",
      success: function (count) {
        $("#badge").text(`(${count})`); // 읽지 않은 쪽지 개수를 badge에 업데이트
      },
      error: function (xhr, status, error) {
        console.error("읽지 않은 쪽지 개수 가져오기 오류:", status, error);
      },
    });
  }
  
  function updateMessageRooms() {
    let memberNum = $("#messages").data("num");
  
    $.ajax({
      url: "/message/rooms",
      method: "get",
      data: { memberNum: memberNum },
      success: function (data) {
        console.log("갱신된 쪽지방 데이터:", data);
        let currentUserNum = $("#messages").data("num"); // 현재 로그인한 사용자의 번호
        let messageList = "";
        data.forEach(function (room) {
          // 쪽지를 보낸 사람이 현재 로그인한 사용자가 아니고, 읽지 않은 쪽지가 있을 때 'New!' 표시
          let newBadge =
            room.hasUnreadMessages && room.senderNum !== currentUserNum
              ? "<span style='color: red;'>New!</span>"
              : "";
  
          messageList += `
                <div class="message-room" data-room="${
                  room.roomNum
                }" data-share-num="${room.shareNum}">
                  <div class="shareBoard" data-write="${
                    room.shareWriteNum
                  }" data-complete="${room.shareCompleted}">
                    <strong>게시글:</strong> ${room.shareTitle} ${newBadge}
                  </div>
                  <div class="sender" data-num="${room.receiverNum}">
                    <strong>상대방:</strong> ${room.senderNickname}
                  </div>
                  <div>
                    <strong>메시지:</strong> ${
                      room.messageContents || "메시지가 없습니다."
                    }
                  </div>
                  <div>
                    <strong>시간:</strong> ${new Date(
                      room.deliverDate
                    ).toLocaleString()}
                  </div>
                </div>`;
        });
        $("#messageListContainer").html(messageList);
      },
      error: function (xhr, status, error) {
        console.error("AJAX 요청 오류:", status, error);
      },
    });
  }
  //************************************쪽지*************************************************** */
  


