// onload
window.onload = function() {
    // 슬라이드 전체 컨테이너
    const slideList = document.querySelector('.slide');
    // 각각의 슬라이드
    const slideContents = document.querySelectorAll('.slide_content');
    // 다음 슬라이드 버튼
    const slideBtnNext = document.querySelector('.arrowRight');
    // 이전 슬라이드 버튼
    const slideBtnPrev = document.querySelector('.arrowLeft');
    // 각각의 페이징 점
    const pageDots = document.querySelectorAll('.dot');
    // 슬라이드 너비
    let sliderWidth = document.getElementById('mainContainer').offsetWidth;
    // 현재 슬라이드 인덱스
    let curIndex = 0;


    // 알림 갯수 (서버에서 받아올 값, 현재는 테스트용)
    let notifications = 3   // 알림이 있을 때 갯수 설정
    const badge = document.getElementById("badge");

    // 알림 수에 따라 괄호 안의 숫자를 변경
    badge.textContent = `(${notifications})`;


    // 메시지 타이핑 효과
    const messageElement = document.getElementById("mainMessage");
    const messageText = messageElement.textContent;     // 원래 텍스트
    let index = 0;                                      // 타이핑 될 텍스트의 인덱스

    messageElement.textContent = '';  // 초기 텍스트 비우기

    // 타이핑 하는 함수
    function typeText() {
        if (index < messageText.length) {
            messageElement.textContent += messageText[index];  // 한 글자씩 추가
            index++;
            setTimeout(typeText, 100);          // 다음 글자 타이핑까지 100ms 후에 추가
        } else {
            setTimeout(restartTyping, 10000)    // 타이핑 완료 후 10초 대기
        }
    }

    // 타이핑 다시 시작하는 함수
    function restartTyping() {
        messageElement.textContent = '';         // 텍스트를 다시 비움
        index = 0;                              // 인덱스를 초기화
        typeText();                             // 타이핑을 다시 시작
    }

    typeText();  // 타이핑 효과 시작


    function updateSliderWidth() {
        sliderWidth = document.getElementById("mainContainer").offsetWidth;
        moveSlide(curIndex);    // 너비 변경 시, 현재 슬라이드로 이동
    }


    /* 슬라이드를 이동시키는 함수_moveSlide */
    function moveSlide(index) {
        slideList.style.transform = `translateX(-${sliderWidth * index}px)`;
        document.querySelector('.dot.active').classList.remove('active');
        pageDots[index].classList.add('active');
    };  // moveSlide()


    /* 다음 슬라이드로 이동하는 이벤트 */
    slideBtnNext.addEventListener('click', () => {
        if(curIndex < slideContents.length - 1) {
            curIndex++;
        } 
        
        // 슬라이드가 마지막이라면, 첫번째로
        else {
            curIndex = 0;
        }

        moveSlide(curIndex);
    });


    /* 이전 슬라이드로 이동하는 이벤트 */
    slideBtnPrev.addEventListener('click', () => {
        if(curIndex > 0) {
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
        dot.addEventListener('click', () => {
            curIndex = index;
            moveSlide(curIndex);
        });
    });


    /* 창 크기 변경 시 슬라이드 어비 조정 */
    window.addEventListener('resize', updateSliderWidth);

};  // function