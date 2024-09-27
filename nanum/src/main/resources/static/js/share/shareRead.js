//*********************************************게시판 기능******************************************* */
// 삭제 버튼 클릭 시 실행될 함수
$(document).ready(function () {
  //게시글을 삭제하는 버튼 클릭이벤트 발생
  $("#deleteButton").click(del);
  // 게시글을 수정하는 버튼 클릭이벤트 발생
  $("#editButton").click(edit);
  // 북마크 버튼 클릭이벤트 실행 bookmark1 = 북 마크하지않음, bookmark
  $("#bookmark1, #bookmark2").click(bookmark);
  //북마크여부 확인 함수 실행
  checkBookmark();
  // 길찾기 이미지 클릭시 함수실행
  $("#findButton").click(findRoute);
  //신고버튼 클릭 이벤트 실행
  $("#reportButton").click(report);

  // 슬라이드 초기화
  var swiper = new Swiper(".mySwiper", {
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    loop: true, // 슬라이드를 무한 반복하려면 추가
  });
});

//삭제 함수
function del() {
  //삭제여부 확인
  if (!confirm("정말 삭제하시겠습니까?")) return;
  //삭제할 글번호 가져오기
  let shareNum = $("#deleteButton").data("num");
  //ajax를 이용해서 삭제 요청 보내기
  $.ajax({
    url: "delete",
    type: "get",
    data: { shareNum: shareNum },
    success: function (result) {
      alert("게시글을 삭제하였습니다.");
      window.location.href = "/share/list";
    },
  });
}

//수정으로 이동
function edit() {
  // 수정페이지로 이동하기 위한 게시글 번호
  let shareNum = $("#editButton").data("num");
  //수정할 경로 전송
  window.location.href = "/share/edit?shareNum=" + shareNum;
}

// 게시글 상세보기 시 작성자가 게시글을 북마크 하고 있는지 아닌지 확인하는 함수
function checkBookmark() {
  var shareNum = $("#bookmark1").data("num"); // 게시글 번호 가져오기

  // 페이지 로드 시 북마크 상태 확인
  $.ajax({
    url: "/share/bookmark/check", // 북마크 여부 확인을 위한 컨트롤러 URL
    type: "get",
    data: { shareNum: shareNum },
    success: function (response) {
      // 서버로부터 true/false 값 받음
      if (response) {
        // 북마크되어 있다면 bookmark2를 보여주고 bookmark1을 숨김
        $("#bookmark1").hide();
        $("#bookmark2").show();
      } else {
        // 북마크가 안 되어 있다면 bookmark1을 보여주고 bookmark2를 숨김
        $("#bookmark1").show();
        $("#bookmark2").hide();
      }
    },
  });
}

//북마크 버튼 클릭시 실행되는 함수
function bookmark() {
  //data에 담겨있는 게시글 번호 가져옴
  let shareNum = $(this).data("num");

  // ajax를 이용해 게시글 번호 전송
  $.ajax({
    url: "bookmark",
    type: "get",
    data: { shareNum: shareNum },
    success: function (response) {
      // 서버로부터 true/false 값 받음
      if (response) {
        // 북마크가 되어있다면 bookmark2를 보여주고 bookmark1을 숨김
        $("#bookmark1").hide();
        $("#bookmark2").show();
      } else {
        // 북마크가 안 되어있다면 bookmark1을 보여주고 bookmark2를 숨김
        $("#bookmark1").show();
        $("#bookmark2").hide();
      }
    },
    // 로그인 하지 않음 사용자는 로그인 페이지로 이동하여
    error: function () {
      alert("로그인하여 해주십시오");
      window.location.href = "/login"; // 로그인 페이지로 리다이렉트
    },
  });
}

// 신고버튼을 누르면 실행되는 함수
function report() {
  // data 에 저장된 게시글 번호를 가져온다.
  let shareNum = $(this).data("num");

  //ajax를 이용해 Contorller에 요청
  $.ajax({
    url: "/share/report",
    type: "post",
    data: { shareNum: shareNum },
    success: function (deleteResult) {
      //같은 사용자가 게시물 중복 신고시
      if (deleteResult == 0) {
        alert("이미 신고한 게시글 입니다.");
      }
      // 게시글 신고횟수가 3회 이상되어 삭제되었을 시
      else if (deleteResult == 1) {
        alert("신고되었습니다.");
        alert("게시글이 삭제되었습니다.");
        window.location.href = "/share/list";
      }
      // 게시글이 정상적으로 신고 되었을 시
      else if (deleteResult == 2) {
        alert("신고되었습니다.");
      }
    },
    // 로그인 하지 않음 사용자는 로그인 페이지로 이동하여
    error: function () {
      alert("로그인하여 해주십시오");
      window.location.href = "/login"; // 로그인 페이지로 리다이렉트
    },
  });
}

// 현재 위치에서 게시글의 위치까지의 경로를 새창으로 보여주는 함수
function findRoute() {
  // 게시글 거래위치
  let shareLat = $("#shareLat").val(); // 게시글 위도
  let shareLng = $("#shareLng").val(); // 게시글 경도

  // 현재 나의 위치
  let myLat;
  let myLng;

  // geolocation 함수를 이용해 현재 위치를 가져온다.
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      function (position) {
        // 현재 위치 위도와 경도 저장
        myLat = position.coords.latitude;
        myLng = position.coords.longitude;

        console.log("현재 위치 위도:", myLat, "경도:", myLng);
        console.log("목적지 위도:", shareLat, "경도:", shareLng);

        // Google Maps 경로 검색 URL 생성
        let url = `https://www.google.com/maps/dir/?api=1&origin=${myLat},${myLng}&destination=${shareLat},${shareLng}`;

        // 새 창으로 Kakao Map 경로 검색 페이지 열기
        window.open(url, "_blank");
      },
      function (error) {
        console.error(
          "Error Code: " + error.code + ", Error Message: " + error.message
        );
        alert("위치 정보를 가져올 수 없습니다.");
      }
    );
  } else {
    alert("Geolocation을 지원하지 않는 브라우저입니다.");
  }
}
//*********************************************게시판 기능******************************************* */

//************************************************ 받을래요 쪽지 기능 ***************************************** */
// 삭제 버튼 클릭 시 실행될 함수
$(document).ready(function () {
  // 모든 모달을 숨긴 상태로 초기화
  $(".modal").hide();

  // '받을래요' 버튼 클릭 시 모달 열기
  $("#receiveButton").click(showMessage);

  // 모달 닫기 버튼 클릭 시 모달 닫기
  $(".close").click(function () {
    if ($(this).closest("#shareReportModal").length > 0) {
      // 신고하기 모달을 닫고 쪽지 모달 열기
      $("#shareReportModal").hide();
      $("#messageModal").show();
    } else {
      $(".modal").hide();
    }
  });

  // 모달 바깥 영역 클릭 시 모달 닫기
  $(window).click(function (event) {
    if ($(event.target).is("#messageModal")) {
      $("#messageModal").hide();
    }
    if ($(event.target).is("#shareReportModal")) {
      $("#shareReportModal").hide();
    }
  });

  // 쪽지 보내기 버튼 클릭 시 처리
  $("#sendMessageButton").click(function () {
    sendMessage();
  });

  // 쪽지에서 '신고하기' 버튼 클릭 시 신고 모달 열기
  $("#userReportButton").click(function () {
    $("#shareReportModal").show();
  });

  // // 신고 모달 닫기
  // $(".close").click(function () {
  //   $("#shareReportModal").hide();
  // });

  // // 모달 창 바깥 부분을 클릭하면 모달 창 닫기
  // $(window).click(function (event) {
  //   if ($(event.target).is("#shareReportModal")) {
  //     $("#shareReportModal").hide();
  //   }
  // });

  // 신고 제출 버튼 클릭 시 신고 내역 제출
  $("#submitUserReportButton").click(function () {
    shareSubmitReport();
  });
});

// 받을래요 버튼을 누르면 실행되는 함수
function showMessage() {
  let creatorNum = $(this).data("user-num");
  let receiverNum = $(this).data("num");
  let shareNum = $(this).data("share-num");
  checkAndLoadRoom(creatorNum, receiverNum, shareNum);
}

// 쪽지방이 존재하는지 체크하고, 존재할 경우 쪽지 내용 로드
function checkAndLoadRoom(creatorNum, receiverNum, shareNum) {
  // ajax를 이용해 쪽지방 존재 여부 체크
  $.ajax({
    url: "/message/check",
    type: "get",
    data: {
      creatorNum: creatorNum,
      receiverNum: receiverNum,
      shareNum: shareNum,
    },
    success: function (response) {
      // 모달 제목 업데이트
      $("#sReceiverNickname").text(response.receiverNickname); // 쪽지 상대의 닉네임
      $("#sReceiverProfileImage").attr("src", response.receiverProfileImage); // 프로필 사진 URL

      //쪽지내역 모달 창 출력
      $("#messageModal").show();
      let userNum = $("#messages").data("num");
      let messages = response.existingMessages;
      let messageList = $("#existingMessages");
      messageList.empty(); // 기존 내용을 지우고
      if (response.roomExists) {
        // 기존 쪽지방이 있으면 쪽지 내용 로드
        messages.forEach(function (message) {
          if (userNum == message.senderNum) {
            messageList.append(
              `<div class="myMessageArea">
                <div class="myMessage">보낸 이야기</div>
                <div class="myMessageContent">${message.messageContents}</div>
                <div class="myMessageDate">${message.deliverDate}</div>
              </div>`
            );
          } else {
            messageList.append(
              `<div class="getMessageArea">
                <div class="getMessage">받은 이야기</div>
                <div class="getMessageContent">${message.messageContents}</div>
                <div class="getMessageDate">${message.deliverDate}</div>
              </div>`
            );
          }
        });
      } else {
        // 쪽지방이 없으면 빈 상태로 유지
        $("#existingMessages").html("");
      }
    },
    error: function () {
      //로그인하지 않았을시 ajax실패! 로그인 화면으로 이동
      alert("로그인하여 주십시오");
      window.location.href = "/login";
    },
  });
}

// 작성한 쪽지를 보내서 DB에 저장
function sendMessage() {
  let messageContents = $("#messageContents").val();
  let receiverNum = $("#receiveButton").data("num");
  let creatorNum = $("#receiveButton").data("user-num");
  let shareNum = $("#receiveButton").data("share-num");

  console.log(creatorNum);
  // ajax를 이용해서 DB에 쪽지 내용을 저장
  $.ajax({
    url: "/message/send",
    type: "post",
    data: {
      creatorNum: creatorNum,
      receiverNum: receiverNum,
      messageContents: messageContents,
      shareNum: shareNum,
    },
    success: function (response) {
      if (response.success) {
        // 쪽지 전송 후 모달 내역 갱신
        $.ajax({
          url: "/message/check",
          type: "GET",
          data: {
            creatorNum: creatorNum,
            receiverNum: receiverNum,
            shareNum: shareNum,
          },
          success: function (response) {
            // 모달 제목 업데이트
            $("#receiverNickname").text(response.receiverNickname); // 쪽지 상대의 닉네임
            $("#receiverProfileImage").attr(
              "src",
              response.receiverProfileImage
            ); // 프로필 사진 URL

            let messages = response.existingMessages;
            let messageList = $("#existingMessages");
            messageList.empty(); // 기존 내용을 지우고
            // 주고받은 전체 쪽지를 영역에 집어넣음
            messages.forEach(function (message) {
              if (userNum == message.senderNum) {
                messageList.append(
                  `<div class="myMessageArea">
                    <div class="myMessage">보낸 이야기</div>
                    <div class="myMessageContent">${message.messageContents}</div>
                    <div class="myMessageDate">${message.deliverDate}</div>
                  </div>`
                );
              } else {
                messageList.append(
                  `<div class="getMessageArea">
                    <div class="getMessage">받은 이야기</div>
                    <div class="getMessageContent">${message.messageContents}</div>
                    <div class="getMessageDate">${message.deliverDate}</div>
                  </div>`
                );
              }
            });
            $("#messageContents").val(""); // 쪽지 입력하는 필드 비우기
          },
        });
      } else {
        alert("쪽지 전송에 실패했습니다.");
      }
    },
  });
}

// 신고 데이터 전송 함수
function shareSubmitReport() {
  let reportReasons = [];
  $("input[name='reportReason']:checked").each(function () {
    reportReasons.push($(this).val());
  });
  let additionalReason = $("#shareAdditionalReason").val();
  let shareNum = $("#receiveButton").data("share-num");
  let reporterNum = $("#receiveButton").data("user-num"); // 사용자의 ID
  let memberNum = $("#receiveButton").data("num"); // 게시글 작성자의 ID

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
      $("#shareAdditionalReason").val("");
      $("#shareReportModal").hide();
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
//************************************************ 받을래요 쪽지 기능 ***************************************** */

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
