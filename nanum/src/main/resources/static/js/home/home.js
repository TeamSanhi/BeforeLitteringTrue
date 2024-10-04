// onload
window.onload = function () {
  // 슬라이드 전체 컨테이너
  const slideList = document.querySelector(".slide");
  // 각각의 슬라이드
  const slideContents = document.querySelectorAll(".slide_content");
  // 다음 슬라이드 버튼
  const slideBtnNext = document.querySelector(".arrowRight");
  // 이전 슬라이드 버튼
  const slideBtnPrev = document.querySelector(".arrowLeft");
  // 각각의 페이징 점
  const pageDots = document.querySelectorAll(".dot");
  // 슬라이드 너비
  let sliderWidth = document.getElementById("mainContainer").offsetWidth;
  // 현재 슬라이드 인덱스
  let curIndex = 0;

  // 알림 갯수 (서버에서 받아올 값, 현재는 테스트용)
  // let notifications = 3   // 알림이 있을 때 갯수 설정
  // const badge = document.getElementById("badge");

  // 알림 수에 따라 괄호 안의 숫자를 변경
  // badge.textContent = `(${notifications})`;

  // 메시지 타이핑 효과
  const messageElement = document.querySelector(".typing"); // 첫 번째 매칭되는 .mainMessage 요소 선택
  const messageText = messageElement.textContent; // 원래 텍스트
  let index = 0; // 타이핑 될 텍스트의 인덱스

  messageElement.textContent = ""; // 초기 텍스트 비우기

  // 타이핑 하는 함수
  function typeText() {
    if (index < messageText.length) {
      messageElement.textContent += messageText[index]; // 한 글자씩 추가
      index++;
      setTimeout(typeText, 100); // 다음 글자 타이핑까지 100ms 후에 추가
    } else {
      setTimeout(restartTyping, 10000); // 타이핑 완료 후 10초 대기
    }
  }

  // 타이핑 다시 시작하는 함수
  function restartTyping() {
    messageElement.textContent = ""; // 텍스트를 다시 비움
    index = 0; // 인덱스를 초기화
    typeText(); // 타이핑을 다시 시작
  }

  typeText(); // 타이핑 효과 시작

  function updateSliderWidth() {
    sliderWidth = document.getElementById("mainContainer").offsetWidth;
    moveSlide(curIndex); // 너비 변경 시, 현재 슬라이드로 이동
  }

  /* 슬라이드를 이동시키는 함수_moveSlide */
  function moveSlide(index) {
    slideList.style.transform = `translateX(-${sliderWidth * index}px)`;
    document.querySelector(".dot.active").classList.remove("active");
    pageDots[index].classList.add("active");
  } // moveSlide()

  /* 다음 슬라이드로 이동하는 이벤트 */
  slideBtnNext.addEventListener("click", () => {
    if (curIndex < slideContents.length - 1) {
      curIndex++;
    }

    // 슬라이드가 마지막이라면, 첫번째로
    else {
      curIndex = 0;
    }

    moveSlide(curIndex);
  });

  /* 이전 슬라이드로 이동하는 이벤트 */
  slideBtnPrev.addEventListener("click", () => {
    if (curIndex > 0) {
      curIndex--;
    }

    // 슬라이드가 첫번째라면, 마지막으로
    else {
      curIndex = slideContents.length - 1;
    }
    moveSlide(curIndex);
  });

  /* 페이징 버튼 클릭 시 이동 이벤트 */
  pageDots.forEach((dot, index) => {
    dot.addEventListener("click", () => {
      curIndex = index;
      moveSlide(curIndex);
    });
  });

  /* 창 크기 변경 시 슬라이드 어비 조정 */
  window.addEventListener("resize", updateSliderWidth);

  // 글 제목 의 길이가 11자가 넘으면 생략하여 보여준다.
  // 모든 postContent 요소들을 가져옴
  let sharePostTitle = document.querySelectorAll(".sharePostTitle");

  // 각 postContent 요소들에 글자 수 제한 적용
  sharePostTitle.forEach(function (post) {
    // 게시글 내용 텍스트 가져오기
    let text = post.innerText;
    // 글자 수 제한_공백 포함
    let maxLength = 11;

    // 글자 수가 40자를 넘으면 자르고 ... 붙이기
    if (text.length > maxLength) {
      post.innerText = text.slice(0, maxLength) + "···";
    }
  });

  // 글 내용의 길이가 14자가 넘으면 생략하여 보여준다.
  // 모든 postContent 요소들을 가져옴
  let postContents = document.querySelectorAll(".postContent");

  // 각 postContent 요소들에 글자 수 제한 적용
  postContents.forEach(function (post) {
    // 게시글 내용 텍스트 가져오기
    let text = post.innerText;
    // 글자 수 제한_공백 포함
    let maxLength = 14;

    // 글자 수가 14자를 넘으면 자르고 ... 붙이기
    if (text.length > maxLength) {
      post.innerText = text.slice(0, maxLength) + "···";
    }
  });
}; // function

//*********************************************쪽지 ********************************************
$(document).ready(function () {
  // 모든 모달을 숨긴 상태로 초기화
  $(".modal").hide();

  // 읽지 않은 쪽지 개수 가져오기
  updateUnreadCount();

  // 쪽지 목록 열기 모달
  $("#messages").click(function () {
    $("#messagesModal").fadeIn().css("display", "flex");
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
    $("#detailsModal").fadeIn().css("display", "flex");
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
        // 상대방의 프로필과 닉네임 업데이트
        $("#receiverNickname").text(data.receiverNickname);
        $("#receiverProfileImage").attr("src", data.receiverProfileImage); // 프로필 사진 URL 업데이트

        // 신고 모달에 닉네임 설정
        $("#reportUser").text(`${data.receiverNickname} 님`);

        let currentUserNum = $("#messages").data("num");
        let detailsList = "";
        data.messages.forEach(function (message) {
          if (currentUserNum == message.senderNum) {
            detailsList += `
              <div class="myMessageArea">
                <div class="myMessage">보낸 이야기</div>
                <div class="myMessageContent">${message.messageContents}</div>
                <div class="myMessageDate">${new Date(
                  message.deliverDate
                ).toLocaleString()}</div>
              </div>
              <br>
              `;
          } else {
            detailsList += `
              <div class="getMessageArea">
                <div class="getMessage">받은 이야기</div>
                <div class="getMessageContent">${message.messageContents}</div>
                <div class="getMessageDate">${new Date(
                  message.deliverDate
                ).toLocaleString()}</div>
              </div>
              <br>
            `;
          }
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

        $("#messageDetailsContainer").scrollTop(
          $("#messageDetailsContainer")[0].scrollHeight
        ); // 상세 목록 이동 시 자동 스크롤
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

            // 신고 모달에 닉네임 설정
            $("#reportUser").text(`${data.receiverNickname} 님`);

            let currentUserNum = $("#messages").data("num");
            let detailsList = "";
            data.messages.forEach(function (message) {
              if (currentUserNum == message.senderNum) {
                detailsList += `
                  <div class="myMessageArea">
                    <div class="myMessage">보낸 이야기</div>
                    <div class="myMessageContent">${
                      message.messageContents
                    }</div>
                    <div class="myMessageDate">${new Date(
                      message.deliverDate
                    ).toLocaleString()}</div>
                  </div>
                  <br>
                  `;
              } else {
                detailsList += `
                  <div class="getMessageArea">
                    <div class="getMessage">받은 이야기</div>
                    <div class="getMessageContent">${
                      message.messageContents
                    }</div>
                    <div class="getMessageDate">${new Date(
                      message.deliverDate
                    ).toLocaleString()}</div>
                  </div>
                  <br>
                `;
              }
            });
            $("#messageDetailsContainer").html(detailsList); // 상세 메시지 갱신
            $("#messageDetailsContainer").scrollTop(
              $("#messageDetailsContainer")[0].scrollHeight
            ); // 상세목록 이동 시 맨 아래로 자동 스크롤
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

  // 모달 닫기 버튼 클릭 시 모달 닫기
  $(".close").click(function () {
    if ($(this).closest("#reportModal").length > 0) {
      // 신고하기 모달을 닫고 쪽지 모달 열기
      $("#reportModal").hide();
      $("#messageModal").show();
      updateUnreadCount(); // 모달 닫을 때 안 읽은 쪽지 개수 업데이트
      updateMessageRooms(); // 쪽지 목록 갱신을 위해 다시 목록 불러오기
    } else {
      $(".modal").hide();
      updateUnreadCount(); // 모달 닫을 때 안 읽은 쪽지 개수 업데이트
      updateMessageRooms(); // 쪽지 목록 갱신을 위해 다시 목록 불러오기
    }
  });

  // // 나눔 이야기 닫기
  // $("#messagesClose").click(function () {
  //   $("#messagesModal").fadeOut();
  //   updateUnreadCount(); // 모달 닫을 때 안 읽은 쪽지 개수 업데이트
  // });

  // // 쪽지 상세내역 닫기
  // $("#detailsClose").click(function () {
  //   $("#detailsModal").fadeOut();
  //   // 쪽지 목록 갱신을 위해 다시 목록 불러오기
  //   updateMessageRooms();
  // });

  $(window).click(function (event) {
    if (
      $(event.target).is("#messagesModal") ||
      $(event.target).is("#detailsModal")
    ) {
      $("#messagesModal").fadeOut().css("display", "none");
      $("#detailsModal").fadeOut().css("display", "none");
      updateUnreadCount(); // 모달 닫을 때 안 읽은 쪽지 개수 업데이트
      // 쪽지 목록 갱신을 위해 다시 목록 불러오기
      updateMessageRooms();
    }
  });

  // 쪽지에서 '신고하기' 버튼 클릭 시 신고 모달 열기
  $("#userReportBtn").click(function () {
    $("#reportModal").show().css("display", "flex");
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

//************************************쪽지 *******************************************************/
