document.addEventListener("DOMContentLoaded", function() {
    // 게시글을 비동기로 로드하는 함수
    function loadPosts() {
        // ajax 또는 타임리프 방식으로 게시글 로드 > 연규상 오네가이
        let newPost = document.createElement("div");

        newPost.classList.add("sharePosting");
        newPost.innerHTML = `
            <a href="#">
                <span class="sharePostTitle">새 게시글</span>
            </a>
            <div class="shareImage">
                <img src="./img/post_new.png">
            </div>
            <div class="postContentArea">
                <span class="postContent">새로운 게시글 내용입니다...</span>
            </div>
            <div class="postInfo">
                <span class="userName">사용자명</span>
                <span class="postDate">2024.09.15</span>
            </div>`;

            // 새 게시들을 추가
            document.querySelector(".sharePostingList").appendChild(newPost);
    };
    
    // 모든 postContent 요소들을 가져옴
    let postContents = document.querySelectorAll(".postContent");

    // 각 postContent 요소들에 글자 수 제한 적용
    postContents.forEach(function(post) {
        // 게시글 내용 텍스트 가져오기
        let text = post.innerText;
        // 글자 수 제한_공백 포함
        let maxLength = 40;

        // 글자 수가 40자를 넘으면 자르고 ... 붙이기
        if (text.length > maxLength) {
            post.innerText = text.slice(0, maxLength) + "···";
        };
    });

    // 테스트_2초마다 게시글 추가
    // setInterval(loadPosts, 2000);
    
});