function openPopup(recycleNum) {
  // AJAX 요청으로 게시글 데이터를 불러온다
  fetch(`/recycle/read?recycleNum=${recycleNum}`)
    .then((response) => response.json())
    .then((data) => {
      console.log(data); // 데이터 확인
      // 팝업창의 내용을 동적으로 설정
      document.getElementById("popupTitle").textContent = data.recycleName;
      document.getElementById("popupContents").textContent =
        data.recycleContents;
      document.getElementById("popupCategory").textContent =
        data.recycleCategory;
      document.getElementById("popupPossible").textContent =
        data.recyclePossible;
      // 날짜 변환 및 표시
      const date = new Date(data.updateDate);

      const formattedDate =
        date.getFullYear() +
        "년 " +
        (date.getMonth() + 1).toString().padStart(2, "0") +
        "월 " +
        date.getDate().toString().padStart(2, "0") +
        "일 " +
        date.getHours().toString().padStart(2, "0") +
        "시 " +
        date.getMinutes().toString().padStart(2, "0") +
        "분 ";
      // date.getSeconds().toString().padStart(2, "0") +
      // "초";

      document.getElementById("popupDate").textContent = formattedDate;

      // 이미지가 제대로 표시되도록 설정
      document.getElementById("popupImage").src =
        `/images/` + data.recycleFileName;

      // 팝업 표시
      $(".popup-overlay").fadeIn();
    })
    .catch((error) => {
      console.error("데이터를 불러오는 중 오류 발생:", error);
      alert("데이터를 불러오는 데 실패했습니다.");
    });
}

function closePopup() {
  $(".popup-overlay").fadeOut();
}

// 외부 영역 클릭 시 팝업 닫기
window.addEventListener("click", function (event) {
  var popupContent = document.querySelector(".popup-content");
  var popupOverlay = document.querySelector(".popup-overlay");

  if (event.target === popupOverlay && !popupContent.contains(event.target)) {
    closePopup();
  }
});

// Esc 키로 팝업창 닫기
document.addEventListener("keydown", function (event) {
  if (event.key === "Escape") {
    closePopup();
  }
});

function pagingFormSubmit(page) {
  let form = document.createElement("form");
  form.method = "GET";
  form.action = "/recycle/list"; // 페이징을 처리할 URL

  let input = document.createElement("input");
  input.type = "hidden";
  input.name = "page";
  input.value = page;

  form.appendChild(input);
  document.body.appendChild(form);
  form.submit();
}
document.addEventListener("DOMContentLoaded", function () {
  // 검색 타입 옵션 클릭 시 처리 함수
  const optionTexts = document.querySelectorAll(".searchType .option-text");

  // hidden input 요소 가져오기
  const searchTypeInput = document.querySelector('input[name="searchType"]');

  // 페이지 로드 시, 선택된 옵션에 따라 hidden input의 값을 설정하고 애니메이션 적용
  const selectedOption = document.querySelector(
    ".searchType .option-text.selected"
  );
  if (selectedOption) {
    searchTypeInput.value = selectedOption.getAttribute("data-value");
    selectedOption.querySelector(".hb").style.backgroundSize = "100% 1vh";
  } else {
    // 기본값 설정 (예: 'titleAndContents')
    searchTypeInput.value = "titleAndContents";
    // 기본값에 따라 애니메이션 적용
    const defaultOption = document.querySelector(
      `.searchType .option-text[data-value="${searchTypeInput.value}"]`
    );
    if (defaultOption) {
      defaultOption.classList.add("selected");
      defaultOption.querySelector(".hb").style.backgroundSize = "100% 1vh";
    }
  }

  optionTexts.forEach(function (option) {
    option.addEventListener("click", function () {
      // 모든 옵션에서 selected 클래스 제거 및 배경 초기화
      optionTexts.forEach(function (opt) {
        opt.classList.remove("selected");
        opt.querySelector(".hb").style.backgroundSize = "0px 1vh";
      });

      // 클릭한 옵션에 selected 클래스 추가 및 배경 설정
      this.classList.add("selected");
      this.querySelector(".hb").style.backgroundSize = "100% 1vh";

      // hidden input의 값 변경
      searchTypeInput.value = this.getAttribute("data-value");
    });
  });

  // 검색어 저장 함수
  function saveSearchWord() {
    const searchWord = document.querySelector('input[name="searchWord"]').value;

    if (searchWord) {
      let searchWords = JSON.parse(localStorage.getItem("searchWords")) || [];

      // 중복 검색어 제거
      searchWords = searchWords.filter((word) => word !== searchWord);

      // 검색어 배열에 추가
      searchWords.unshift(searchWord); // 새로운 검색어를 앞에 추가

      // 최대 5개의 검색어만 저장
      if (searchWords.length > 5) {
        searchWords = searchWords.slice(0, 5);
      }

      localStorage.setItem("searchWords", JSON.stringify(searchWords));
    }
  }

  // 검색창 클릭 시 검색어 목록을 불러오는 함수
  function loadSearchWordList() {
    const searchWords = JSON.parse(localStorage.getItem("searchWords")) || [];

    if (searchWords.length > 0) {
      let dropdown = document.getElementById("searchWordList");

      // 이전 검색어 목록 삭제
      dropdown.innerHTML = "";

      // 검색어 목록을 동적으로 추가
      searchWords.forEach((word) => {
        const listItem = document.createElement("li");
        listItem.textContent = word;

        // 클릭한 검색어를 입력 필드에 넣기
        listItem.addEventListener("click", function () {
          document.querySelector('input[name="searchWord"]').value = word;
          dropdown.style.display = "none"; // 목록을 숨기기
        });

        dropdown.appendChild(listItem);
      });

      // 검색어 목록 표시
      dropdown.style.display = "block";
    }
  }

  // 검색창에 포커스가 갔을 때 검색어 목록 불러오기
  document
    .querySelector('input[name="searchWord"]')
    .addEventListener("focus", loadSearchWordList);

  // 검색 폼 제출 시 검색어 저장
  document
    .querySelector("#searchContainer form")
    .addEventListener("submit", saveSearchWord);

  // 검색창 외부 클릭 시 목록 닫기
  document.addEventListener("click", function (event) {
    if (!event.target.matches('input[name="searchWord"]')) {
      document.getElementById("searchWordList").style.display = "none";
    }
  });

  // Esc 키로 팝업창 닫기
  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
      closePopup();
    }
  });
});

// 팝업 관련 함수
function openPopup(recycleNum) {
  // AJAX 요청으로 게시글 데이터를 불러온다
  fetch(`/recycle/read?recycleNum=${recycleNum}`)
    .then((response) => response.json())
    .then((data) => {
      console.log(data); // 데이터 확인
      // 팝업창의 내용을 동적으로 설정
      document.getElementById("popupTitle").textContent = data.recycleName;
      document.getElementById("popupContents").textContent =
        data.recycleContents;
      document.getElementById("popupCategory").textContent =
        data.recycleCategory;
      document.getElementById("popupPossible").textContent =
        data.recyclePossible;
      // 날짜 변환 및 표시
      const date = new Date(data.updateDate);

      const formattedDate =
        date.getFullYear() +
        "년 " +
        (date.getMonth() + 1).toString().padStart(2, "0") +
        "월 " +
        date.getDate().toString().padStart(2, "0") +
        "일 " +
        date.getHours().toString().padStart(2, "0") +
        "시 " +
        date.getMinutes().toString().padStart(2, "0") +
        "분 ";

      document.getElementById("popupDate").textContent = formattedDate;

      // 이미지가 제대로 표시되도록 설정
      document.getElementById("popupImage").src =
        `/images/` + data.recycleFileName;

      // 팝업 표시
      $(".popup-overlay").fadeIn(200);
    })
    .catch((error) => {
      console.error("데이터를 불러오는 중 오류 발생:", error);
      alert("데이터를 불러오는 데 실패했습니다.");
    });
}

function closePopup() {
  $(".popup-overlay").fadeOut(200);
}

// 외부 영역 클릭 시 팝업 닫기
window.addEventListener("click", function (event) {
  var popupContent = document.querySelector(".popup-content");
  var popupOverlay = document.querySelector(".popup-overlay");

  if (event.target === popupOverlay && !popupContent.contains(event.target)) {
    closePopup();
  }
});


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

  // 메시지 요소와 원래 텍스트 저장
  const messages = [
    { element: document.querySelector(".typing1") },
    { element: document.querySelector(".typing2") },
    { element: document.querySelector(".typing3") }
  ];
  
  // 각 메시지 요소의 텍스트를 변수에 저장하고 초기화
  messages.forEach((messageObj) => {
    messageObj.text = messageObj.element.textContent; // 원래 텍스트 저장
    messageObj.element.textContent = ""; // 초기 텍스트 비우기
  });
  
  // 타이핑 효과를 적용하는 함수
  function typeText(messageObj) {
    const { element, text } = messageObj;
    let index = 0;

    function typing() {
      if (index < text.length) {
        element.textContent += text[index]; // 한 글자씩 추가
        index++;
        setTimeout(typing, 100); // 다음 글자 타이핑까지 100ms 후에 추가
      } else {
        setTimeout(() => restartTyping(messageObj), 5000); // 타이핑 완료 후 10초 대기
      }
    }
    typing();
  }
  
  // 타이핑을 다시 시작하는 함수
  function restartTyping(messageObj) {
    messageObj.element.textContent = ""; // 텍스트 비움
    typeText(messageObj); // 다시 타이핑 시작
  }

  // 모든 메시지 요소에 타이핑 효과 적용
  messages.forEach((messageObj) => {
    typeText(messageObj);
  });

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
          // 로그인한 사람과 게시글을 작선한 사람이 일치하지 않으면 나눔완료버튼을 숨긴다.
          $("#shareComplete").show();
          $("#shareCompleted").hide();
          // 게시글의 나눔상태 즉 완료된 상태가 1이라면 
          if (shareCompleted) {
            // 나눔완료 버튼기능은 숨기고, 형관편 처리된 나눔완료를 의미하는 span을보여준다.
            $("#shareComplete").hide();
            $("#shareCompleted").show();
          } 
          // else {
          //   $("#shareComplete").prop("disabled", false);
          // }
        } else {
          // 로그인한 사람과 게시글을 작선한 사람이 일치하지 않으면 나눔완료버튼을 숨긴다.
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
      //  $("#shareComplete").prop("disabled", true); // 버튼 비활성화
        // 나눔완료 버튼기능은 숨기고, 형관편 처리된 나눔완료를 의미하는 span을 보여준다. 
        $("#shareComplete").hide();
        $("#shareCompleted").show();
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