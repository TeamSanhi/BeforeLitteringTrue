
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

