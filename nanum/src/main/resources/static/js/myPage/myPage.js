$(document).ready(function () {
    const dataManager = {
        data: {},
        currentPage: {},
        perPage: {
            'alarm': 7,
            'give': 10,
            'donate': 10,
            'pendingGive': 10,
            'receive': 10,
            'bookmark': 10
        },
        listType: '',

        // 이미지 토글 상태를 저장하는 변수
        currentImageIndex: 0,   //이 숫자를 변경해가며 images배열의 인덱스로 넣어 순차적으로 이미지를 변경시킨다.
        images: ["/images/trash.png", "/images/box.png"], // 두 개의 이미지 경로
    };


    // 모달 초기화 함수
    function initModal(triggerSelector, modalSelector) {
        $(triggerSelector).click(function () {
            $(modalSelector).fadeIn();
        });

        $(modalSelector).on('click', '.close', function () {
            $(modalSelector).fadeOut();
        });

        $(window).click(function (event) {
            if ($(event.target).is(modalSelector)) {
                $(modalSelector).fadeOut();
            }
        });
    }

    // 모달 초기화
    initModal('#messages', '#messagesModal');
    initModal('#alarmAdd', '#alarmAddModal');
    initModal('.alarmEdit', '#alarmEditModal');
    initModal('#deleteId', '#alertModal');
    initModal('.infoSetting', '#infoSettingModal')// 회원정보 수정 모달 보여줌 


    // 탈퇴 단계 처리
    $('#next').click(function () {
        $('#alertModal').fadeOut();
        $('#checkModal').fadeIn();
    });

    $('#reset').click(function () {
        $('#alertModal').fadeOut();
    });

    $('#checkModal').on('click', '.close', function () {
        $('#checkModal').fadeOut();
    });

    // 탈퇴 처리
    $('#delete').click(function () {
        const enteredPw = $('#pwCheck').val();

        $.ajax({
            url: "/myPage/checkPassword",
            type: "POST",
            data: {
                enteredPw: enteredPw,
            },
            success: function (response) {
                if (response.passwordMatch) {
                    if (confirm("정말 탈퇴하시겠습니까?")) {
                        $.ajax({
                            url: "/myPage/deleteMember",
                            type: "POST",
                            success: function (response) {
                                if (response.deleteMember) {
                                    $('#checkModal').fadeOut();
                                    $('#verifyModal').fadeIn();
                                } else {
                                    alert("탈퇴 처리에 실패했습니다. 다시 시도해주세요.");
                                }
                            },
                            error: function () {
                                alert("서버 오류입니다.");
                            },
                        });
                    } else {
                        $('#pwCheck').val("");
                    }
                } else {
                    alert("비밀번호가 일치하지 않습니다.");
                    $('#pwCheck').val("");
                }
            },
            error: function () {
                alert("서버 오류입니다.");
            },
        });
    });

    $('#verifyModal').on('click', '.close', function () {
        $('#verifyModal').fadeOut(function () {
            window.location.href = "/member/logout";
        });
    });

    $(window).click(function (event) {
        if ($(event.target).is('#verifyModal')) {
            $('#verifyModal').fadeOut(function () {
                window.location.href = "/member/logout";
            });
        }
    });

    // 3. 나의 알림, 나의 나눔, 북마크 버튼 hover 효과 유지 및 섹션 변경
    $(".menuArea button").on("click", function () {
        // 모든 버튼의 배경색을 원래대로
        $(".menuArea button").css("background-color", "white");

        // 클릭된 버튼의 배경색 변경
        $(this).css("background-color", "#8ABAFF");

    });

    // 알림 추가 함수
    function addAlarm(alarmDay, alarmContents) {
        $.ajax({
            url: '/myPage/alarmAdd',
            type: "POST",
            data: {
                alarmDay: alarmDay,
                alarmContents: alarmContents,
            },
            success: function (response) {
                if (response.alarmAdd) {
                    alert("성공적으로 반영되었습니다.");
                    if (dataManager.listType === 'alarm') {
                        fetchData('alarm', '/myPage/showAlarm');
                    }
                } else {
                    alert("반영되지 않았습니다.");
                }
            },
            error: function () {
                alert("알림을 추가하는 중 오류가 발생했습니다.");
            }
        });
    }

    // 알림 편집 함수 
    function editAlarm(alarmDay, alarmContents) {
        $.ajax({
            url: '/myPage/alarmEdit',
            type: "POST",
            data: {
                alarmDay: alarmDay,
                alarmContents: alarmContents,
            },
            success: function (response) {
                if (response.alarmEdit) {
                    alert("성공적으로 반영되었습니다.");

                    if (dataManager.listType === 'alarm') {
                        fetchData('alarm', '/myPage/showAlarm');
                    }
                } else {
                    alert("반영되지 않았습니다.");
                }
            },
            error: function () {
                alert("알림을 편집하는 중 오류가 발생했습니다.");
            }
        });
    }

    // 이미지 토글 함수
    function toggleImage() {
        // 이미지 인덱스 토글
        // 0이면 1로
        if(dataManager.currentImageIndex == 0){
            dataManager.currentImageIndex = 1
            return;
        }
        // 1이면 0으로 반복문에서 번갈아 가도록 설정한다. 
        if(dataManager.currentImageIndex == 1){
            dataManager.currentImageIndex = 0
            return;
        }
    }



    // 알림 추가 함수 중복 확인 
    $('#added').click(function () {
        const alarmDay = $(".addDay:checked").val();
        const alarmContents = $("#addContents").val();

        $.ajax({
            url: "/myPage/checkAlarm",  // 알림 존재 여부를 확인하는 API
            type: "POST",
            data: {
                alarmDay: alarmDay,
                alarmContents: alarmContents
            },
            success: function (response) {
                if (response.alarmExists) {
                    if (confirm("기존 알람이 있습니다. 수정하시겠습니까?")) {
                        editAlarm(alarmDay, alarmContents);
                        $('#alarmAddModal').fadeOut();
                    }
                } else {
                    addAlarm(alarmDay, alarmContents);
                    $('#alarmAddModal').fadeOut();
                    setTimeout(function () {
                        location.reload();
                    }, 1000);
                }
                $('.addDay').prop('checked', false);
                $('#addContents').val('');
            },
            error: function () {
                alert("알림 상태를 확인하는 중 오류가 발생했습니다.");
            }
        });
    })

    // 알림 편집
    $('#edited').click(function () {
        const alarmDay = $(".editDay:checked").val();
        const alarmContents = $("#editContents").val();

        if (confirm("수정하시겠습니까?")){
            editAlarm(alarmDay, alarmContents);
        }

        $('#alarmEditModal').fadeOut();
        $('#editDay').prop('checked', false);
        $('#editContents').val('');
    });

    // 알림 삭제 함수
    function deleteAlarm(alarmNum) {
        //알람 삭제 안내문문
        if(!confirm("알림을 삭제 하시겠습니까?"))
            return;

        // 알람 삭제 ajax 요청 
        $.ajax({
            url: '/myPage/alarmDelete',
            type: "POST",
            data: {
                alarmNum: alarmNum,
            },
            success: function (response) {
                if (response) {
                    alert("성공적으로 삭제되었습니다.");
                    if (dataManager.listType === 'alarm') {
                        fetchData('alarm', '/myPage/showAlarm');
                    }
                } else {
                    alert("삭제제되지 않았습니다.");
                }
            },
            error: function () {
                alert("알람을 삭제하는 중 오류가 발생했습니다.");
            }
        });
    }

    // // 알림 편집 버튼
    // $(document).on('click', '.alarmModify', function () {
    //     const day = $(this).data('day');
    //     const content = $(this).data('contents');

    //     $('.editDay').prop('disabled', true);
    //     $('.editDay[value="' + day + '"]').prop('disabled', false);

    //     $('.editDay').each(function () {
    //         $(this).prop('checked', $(this).val() === day);
    //     });

    //     $('#editContents').val(content);
    //     $('#alarmEditModal').fadeIn();
    // });

    // 데이터 목록 버튼 클릭 이벤트
    $('button[data-list]').click(function () {
        const listType = $(this).data('list');
        const url = $(this).data('url');
        dataManager.listType = listType;
        fetchData(listType, url);
    });

    // 페이지 버튼 클릭 이벤트
    $(document).on('click', '.page-btn', function () {
        const selectedPage = parseInt($(this).data('page'));
        const listType = $(this).data('list');

        if (!listType || !dataManager.data[listType]) {
            alert("유효하지 않은 데이터입니다.");
            return;
        }

        dataManager.currentPage[listType] = selectedPage;
        renderPage(listType, dataManager.currentPage[listType]);
        renderPaginationSection(listType);
    });

    // 테이블 행 클릭 이벤트
    // $(document).on('click', '.dataTable tr.data-row', function(e) {
    //     // 클릭한 요소가 링크나 버튼인 경우 이벤트 중단
    //     if ($(e.target).is('a') || $(e.target).is('button') || $(e.target).parents('a').length > 0) {
    //         return;
    //     }
    //     const url = $(this).data('url');
    //     if (url) {
    //         window.location.href = url;
    //     }
    // });

    // 데이터 가져오기 함수
    function fetchData(listType, url) {

        // 데이터를 가져올때 인덱스를 초기화 하여 시작하는 이미지를 같도록한다.
        dataManager.currentImageIndex = 0;

        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            success: function (response) {
                if (listType === 'pendingGive') {
                    if (Array.isArray(response)) {
                        dataManager.data[listType] = response.filter(item => item.shareCompleted === '나눠요');
                    } else if (typeof response === 'object') {
                        const responseArray = [response];
                        dataManager.data[listType] = responseArray.filter(item => item.shareCompleted === '나눠요');
                    } else {
                        console.error("예상치 못한 응답 형식입니다.");
                        dataManager.data[listType] = [];
                    }
                } else if (listType === 'donate') {
                    if (Array.isArray(response)) {
                        dataManager.data[listType] = response.filter(item => item.shareCompleted === '나눴어요');
                    } else if (typeof response === 'object') {
                        const responseArray = [response];
                        dataManager.data[listType] = responseArray.filter(item => item.shareCompleted === '나눴어요');
                    } else {
                        console.error("예상치 못한 응답 형식입니다.");
                        dataManager.data[listType] = [];
                    }
                } else {
                    dataManager.data[listType] = response;
                }
                dataManager.currentPage[listType] = 1;
                renderPage(listType, dataManager.currentPage[listType]);
                renderPaginationSection(listType);
            },
            error: function () {
                alert("데이터를 가져오는데 실패했습니다.");
            }
        });
    }

    // 페이지 렌더
    function renderPage(listType, page) {
        $("#showData").html("");

        const dataList = dataManager.data[listType];

        if (!dataList || !Array.isArray(dataList)) {
            $("#showData").html("<p>데이터를 불러오지 못했습니다.</p>");
            return;
        }

        const perPage = dataManager.perPage[listType];
        const start = (page - 1) * perPage;
        const end = start + perPage;
        const pagedData = dataList.slice(start, end);

        if (pagedData.length > 0) {
            if (listType === 'alarm' || listType === 'give' || listType === 'bookmark') {
                // 게시글 구조 시작
                let Html = "";

                // 반복문을 통해 html로 넣어줄 게시글들을 생성한다.
                pagedData.forEach(item => {
                    Html += generateItemHtml(listType, item);
                    // 반복문이 실행 될때마다 숫자가 변경되어 알람 이미지를 다르게 하는 함수 
                    toggleImage();
                });

                $("#showData").append(Html);
            } else {
                // 다른 목록 타입에 대한 처리
                pagedData.forEach(item => {
                    const itemHtml = generateItemHtml(listType, item);
                    $("#showData").append(itemHtml);
                });
            }
        } else {
            // 등록된 알람 또는 게시글이 없는 경우 글 출력되는 메시지 
            $("#showData").html("<p style='display: flex; justify-content: center; align-items: center; padding: 3vh; padding-bottom: 5vh; width: 93vw; height: auto; font-size: 3vh; font-weight:700; letter-spacing: 0.1vw;'>아직 등록 된 알림 또는 게시글이 없습니다!</p>");
        }
    }

    // 아이템 HTML 생성 함수
    function generateItemHtml(listType, item) {
        switch (listType) {
            case 'alarm':
                return `
            <!-- 알림 -->
            <div class="alert">
                <!-- 오른쪽 아이콘_이미지 -->
                <div class="rightIcon">
                    <img src="${dataManager.images[dataManager.currentImageIndex]}">
                </div>
                <!-- 왼쪽 하단_알림 내용 영역 -->
                <div class="alertContentArea">
                    <div>
                        <span class="settingAlert alarmModify">편집</span>
                        <span class="settingDelete alarmDelete" data-num="${item.alarmNum}">삭제</span>
                    </div>
                    <div class="contentArea">
                        <span class="date">${item.alarmDay}</span>
                        <span class="alertContent">${item.alarmContents}</span>
                    </div>
                </div>
            </div>
    `;
            case 'give':
                return `
                <!-- 나눠요 인기글 게시글 목록 -->
                <div class="sharePosting">
                    <!-- 제목 -->
                    <a href="/share/read?shareNum=${item.shareNum}">
                      <span class="sharePostTitle">${item.shareTitle}</span>
                    </a>
                    <!-- 사진 -->
                    <div class="shareImage">
                        <img src="/share/download?imageNum=${item.imageNum}" loading="lazy">
                    </div>
                    <!-- 글내용 -->
                    <div class="postContentArea">
                        <span class="postContent">${item.shareContents}</span>
                    </div>
                    <!-- 작성일자, 나눔여부 -->
                    <div class="postInfo">
                        <span class="userNickname">${item.shareDate}</span>
                        <span class="bookmarkCount" >${item.shareCompleted}</span>
                    </div>
                </div>
    `;
    
            case 'bookmark':
                return `
                <!-- 나눠요 인기글 게시글 목록 -->
                <div class="sharePosting">
                    <!-- 제목 -->
                    <a href="/share/read?shareNum=${item.shareNum}">
                      <span class="sharePostTitle">${item.shareTitle}</span>
                    </a>
                    <!-- 사진 -->
                    <div class="shareImage">
                        <img src="/share/download?imageNum=${item.imageNum}" loading="lazy">
                    </div>
                    <!-- 글내용 -->
                    <div class="postContentArea">
                        <span class="postContent">${item.shareContents}</span>
                    </div>
                    <!-- 닉네임, 나눔여부 -->
                    <div class="postInfo">
                        <span class="userNickname">${item.memberNickname}</span>
                        <span class="bookmarkCount">북마크</span>
                    </div>
                </div>
    `;
            // 다른 케이스들은 기존과 동일하게 유지
        }  
        
    }

    // 페이징 섹션 렌더링 함수
    function renderPaginationSection(listType) {

        // 알람 버튼 클릭하면 페이징 함수 실행 하지 않고 알람추가 버튼추가 
        if(listType === "alarm"){
            let alarmButton = `
                <!-- 알림 추가 영역 -->
                <div class="addAlert-container">
                    <div class="addAlert" >
                        <!-- 버튼 -->
                        <button class="addAlertButton" id="alarmAdd">+</button>
                    </div>
                </div>
            `;
            $("#showData").append(alarmButton);

            // 동적으로 추가된 알람 추가 버튼에 이벤트 바인딩
            $(document).on('click', '#alarmAdd', function () {
                $('#alarmAddModal').fadeIn();  // 알람 추가 모달을 띄움
            });

            // 동적으로 추가된 알림 편집 버튼 클릭 이벤트
            $(document).on('click', '.alarmModify', function () {
                // 클릭된 알림에서 관련 데이터(예: 날짜와 내용)를 가져옴
                const alarmDay = $(this).closest('.alertContentArea').find('.date').text();
                const alarmContents = $(this).closest('.alertContentArea').find('.alertContent').text();

                // 모달에 데이터를 채워서 보여줌
                $('.editDay').each(function () {
                    $(this).prop('checked', $(this).val() === alarmDay);
                });
                $('#editDay').html(alarmDay);
                $('#editContents').val(alarmContents);

                // 알림 편집 모달 열기
                $('#alarmEditModal').fadeIn();

            });

            // 동적으로 삭제버튼 기능 추가 
            $('.alarmDelete').click(function() {
                //삭제할 알람 번호 가져옴
                let alarmNum = $(this).data('num');
                // 알람 삭제함수 실행
                deleteAlarm(alarmNum);                
            })
            return;
        }

        // 만들어진 게시글에 글자수 제한하는 함수를 실행시킨다.
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

        const dataList = dataManager.data[listType];

        if (!dataList || !Array.isArray(dataList)) {
            return;
        }

        const totalItems = dataList.length;
        const perPage = dataManager.perPage[listType];
        const currentPage = dataManager.currentPage[listType];
        const paginationHtml = renderPagination(totalItems, perPage, currentPage, listType);
        $("#showData").append(paginationHtml);
    }

    // 페이징 HTML 생성 함수
    function renderPagination(totalItems, itemsPerPage, currentPage, listType) {
        const totalPages = Math.ceil(totalItems / itemsPerPage);
        let paginationHtml = '<div class="pagination-container"><div class="pagination">';

        // 이전 버튼
        if (currentPage > 1) {
            paginationHtml += `<button class="page-btn" data-page="${currentPage - 1}" data-list="${listType}">이전</button>`;
        }

        // 페이지 번호 버튼
        for (let p = 1; p <= totalPages; p++) {
            if (p === currentPage) {
                paginationHtml += `<span class="current-page">${p}</span>`;
            } else {
                paginationHtml += `<button class="page-btn" data-page="${p}" data-list="${listType}">${p}</button>`;
            }
        }

        // 다음 버튼
        if (currentPage < totalPages) {
            paginationHtml += `<button class="page-btn" data-page="${currentPage + 1}" data-list="${listType}">다음</button>`;
        }

        paginationHtml += '</div></div>';
        return paginationHtml;
    }

    // **************페이지가 로드되면 버튼 클릭 이벤트 발생****************
    const myShareButton = document.querySelector(".myAlert");
    if (myShareButton) {
        myShareButton.click();
    }
    
});




//********************************회원정보 수정 ***************************** */



var file; // 선택된 파일을 저장할 변수
$(document).ready(function () {
    $("#nickDuplicationCheck").val("");
    $("#emailDuplicationCheck").val("");
    $('#passwordDuplicationCheck').val("");

    $('#modifyButton').click(function () {
        if (confirm('수정하시겠습니까?'))
            return true;
        else
            return false;
    })

    $('#modifyPost').submit(function (event) {
        console.log("폼 제출 이벤트 호출됨");

        // 폼 제출 막기
        event.preventDefault();

        // 폼 검증 로직
        if (!check()) {
            console.log("폼 제출이 막힘");
        } else {
            // 프로필 이미지 업로드
            profileImageEdit(function() {
                console.log("폼 제출 허용");
                // 이미지 업로드 후 폼을 수동으로 제출
                $('#modifyPost')[0].submit();
            });
        }
    });

    // 비밀번호 유효성 검사 (keyup 시)
    $("#memberPw").on("keyup", function () {
        validatePassword();
    });

    //비밀번호 확인 검사
    $("#pwCheck").on("keyup", function () {
        checkPasswordMatch();
    });

    // 이미지 변경 버튼 클릭 시 파일 입력 창을 열기
    $('#imageChangeBtn').on('click', function () {
        $('#fileInput').click();
    });


    // 파일 선택 시 이미지 미리보기 처리
    $('#fileInput').on('change', function (event) {
        console.log("이미지 미리보기");
        file = event.target.files[0]; // 선택한 파일 저장
        if (file) {
            // 이미지 미리보기를 위해 FileReader 객체 생성
            var reader = new FileReader();
            reader.onload = function (e) {
                // 선택한 이미지를 프로필 이미지로 설정
                $('#profileImage').attr('src', e.target.result);
            }
            reader.readAsDataURL(file); // 파일을 데이터 URL로 읽기
        }
    });

    // 파일 선택 시 이벤트 처리
    // $('#fileInput').on('change', function (event) {
    //     console.log("fileCheck");
    //     var file = event.target.files[0]; // 선택한 파일 가져오기
    //     if (file) {
    //         // 이미지 미리보기를 위해 FileReader 객체 생성
    //         var reader = new FileReader();
    //         reader.onload = function (e) {
    //             // 선택한 이미지를 프로필 이미지로 설정
    //             $('#profileImage').attr('src', e.target.result);
    //         }
    //         reader.readAsDataURL(file); // 파일을 데이터 URL로 읽기
    //
    //         // 서버로 파일 업로드를 위한 FormData 객체 생성
    //         var formData = new FormData();
    //         formData.append('file', file);
    //
    //         // AJAX 요청을 통해 서버에 이미지 업로드
    //         $.ajax({
    //             url: '/myPage/uploadProfileImage', // 서버 업로드 엔드포인트 URL
    //             type: 'POST',
    //             data: formData,
    //             processData: false, // 파일 데이터를 문자열로 변환하지 않음
    //             contentType: false, // 기본 Content-Type 헤더 설정 안 함
    //             success: function (data) {
    //                 // 업로드 성공 시 처리 로직
    //                 console.log('이미지 업로드 성공:', data);
    //             },
    //             error: function (error) {
    //                 // 업로드 실패 시 처리 로직
    //                 console.error('이미지 업로드 실패:', error);
    //             }
    //         });
    //     }
    // });
});

// 프로필 이미지 편집 함수
function profileImageEdit(callback) {
    if (file) {
        // 서버로 파일 업로드를 위한 FormData 객체 생성
        var formData = new FormData();
        formData.append('file', file);

        // AJAX 요청을 통해 서버에 이미지 업로드
        $.ajax({
            url: '/myPage/uploadProfileImage', // 서버 업로드 엔드포인트 URL
            type: 'POST',
            data: formData,
            processData: false, // 파일 데이터를 문자열로 변환하지 않음
            contentType: false, // 기본 Content-Type 헤더 설정 안 함
            success: function (data) {
                // 업로드 성공 시 처리 로직
                console.log('이미지 업로드 성공:', data);

                // 이미지 업로드 성공 시 콜백 실행 (폼 제출 허용)
                if (typeof callback === 'function') {
                    callback();
                }
            },
            error: function (error) {
                // 업로드 실패 시 처리 로직
                console.error('이미지 업로드 실패:', error);
            }
        });
    } else {
        console.log('파일이 선택되지 않았습니다.');

        // 파일이 선택되지 않은 경우에도 콜백을 실행
        if (typeof callback === 'function') {
            callback();
        }
    }
}

// 비밀번호 유효성 검사 함수
function validatePassword() {
    let memberPw = $("#memberPw").val().trim();
    console.log(memberPw);
    if (memberPw !== "") {
        if (memberPw.length < 8) {
            $("#passwordError").text("비밀번호는 8자 이상으로 입력해 주세요.").show().css("color", "red");
            return false;
        } else {
            $("#passwordError").text("사용가능한 비밀번호 입니다.").show().css("color", "blue");
            return true;
        }
    } else {
        $("#passwordError").text("");
        return true;
    }
}

// 비밀번호 확인 검사 함수
function checkPasswordMatch() {
    let memberPw = $("#memberPw").val().trim();
    let pwCheck = $("#pwCheck").val().trim();
    console.log(memberPw);
    console.log(pwCheck);
    if (!(memberPw === "" && pwCheck === "")) {
        if (memberPw === pwCheck) {
            $("#pwCheckError").text("비밀번호가 일치합니다.").show().css("color", "blue");
            $('#passwordDuplicationCheck').val("y");
        } else {
            $("#pwCheckError").text("비밀번호가 일치하지 않습니다.").show().css("color", "red");
            $('#passwordDuplicationCheck').val("n");
        }
    } else if (memberPw === "" && pwCheck === "") {
        $("#pwCheckError").text("");
        $('#passwordDuplicationCheck').val("");
    } else {
        $('#passwordDuplicationCheck').val("n");
    }
}

// 이메일 형식을 확인하는 정규 표현식 함수
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// 로그인한 이메일과 동일한지 여부
function isLoggedEmail() {
    var email = $("#memberEmail").val().trim();
    var loggedEmail = $("#loggedEmail").val();

    console.log("email :", email);
    console.log("loggedEmail :", loggedEmail);

    if (loggedEmail === email)
        return true;

    return false;
}

// 이메일 전송함수
function sendEmailVerificationCode() {
    // 전송할 email 값을 가져온다.
    const email = $("#memberEmail").val().trim();

    // 이메일 형식에 맞지 않으면 alert 창 표시
    if (!isValidEmail(email)) {  // 이메일 형식이 맞지 않으면
        $("#emailError").text("유효한 이메일 주소를 입력하세요.").show().css("color", "red"); // 경고 메시지
        return;  // 전송 중단
    }

    $("#emailError").text("잠시만 기다려주세요...").show().css("color", "blue");

    // ajax로 받은 이메일에 이메일 전송 요청
    $.ajax({
        url: '/member/reSendEmail',
        type: 'post',
        data: {email: email},
        success: function (response) {
            // if문으로 response 값이 true 이면 중복되는 값이 있고 false 면 중복되는 값이 없다.
            if (response.duplication) {
                // 전달받은 값이 true 면 중복임으로 경고 메시지
                if (!response.loginUser) {
                    $("#emailError").text("이미 존재하는 이메일입니다.").show().css("color", "red"); // 경고 메시지
                } else { // 이메일 체크 안 하도록 설정
                    $("#emailError").text("인증 완료된 이메일입니다.").show().css("color", "blue"); // 경고 메시지
                }
            } else {
                // 전달받은 값이 false 면 중복되지 않고 이메일이 전송되었음으로 아래 메시지 출력
                $("#emailError").text("이메일 전송에 성공하였습니다.").show().css("color", "blue");
            }
        },
        error: function () {
            // ajax 요청 실패시 메시지 보여준다.
            $("#emailError").text("이메일 전송 요청에 실패했습니다.").show().css("color", "red"); // 경고 메시지
        }
    });
}

// 이메일 인증번호 검증
function verifyEmailCode() {
    // 인증번호
    const emailCode = $("#emailCode").val();
    // ajax로 인증번호 검증 요청
    $.ajax({
        url: '/member/verifyEmail',
        type: 'POST',
        data: {emailCode: emailCode},
        // ajax로 요청 성공시 "인증 성공" or "인증 실패" 둘중 하나의 메시지를 전달받는다.
        success: function (response) {
            // 인증성공, 인증실패 결과에 따라 띄워준다.
            if (response === "인증 성공") {
                // 인증성공 메시지 출력
                $("#emailCodeError").text(response).show().css("color", "blue");
                // 성공시 인증확인을 위한 emailDuplicationCheck를 y로 변경
                $("#emailDuplicationCheck").val("y");
                // 인증성공시 이메일과 인증번호를 수정불가 하도록 변경
                $('#memberEmail').attr('readonly', true);
                $('#emailCode').attr('readonly', true);
            } else {
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

// 로그인한 닉네임과 동일한지 여부
function isLoggedNickname() {
    var nickname = $("#memberNickname").val().trim();
    var loggedNickname = $("#loggedNickname").val();

    console.log("")

    if (nickname === loggedNickname)
        return true;
    return false;
}

// 닉네임 중복확인 클릭시 실행되는 함수
function nickDoubleCheck() {
    const memberNickname = $("#memberNickname").val().trim();

    if (memberNickname === "") {
        $("#nickError").text("닉네임을 입력하세요.").show().css("color", "red");
        return;
    }
    // 아이디 중복확인 ajax 요청
    $.ajax({
        url: '/find/reNickCheck',
        type: 'POST',
        data: {memberNickname: memberNickname},
        success: function (response) {
            // 닉네임이 있으면 true 없으면 false 값을 가져온다.
            if (response.duplication) {
                if (!response.loginUser) {
                    $("#nickError").text("이미 존재하는 닉네임입니다.").show().css("color", "red");
                    $("#nickDuplicationCheck").val("y");
                } else { // 이메일 체크 안 하도록 설정
                    $("#nickError").text("확인 완료된 닉네임입니다.").show().css("color", "blue");
                    $("#nickDuplicationCheck").val("y");
                }
            } else {
                // 사용 가능한 닉네임일 때
                $("#nickError").text("중복 확인 완료.").show().css("color", "blue");
                // 인증절차 승인을 위해서 값을 "y"로 변경시킨다.
                $('#memberNickname').attr('readonly', true);
                $("#nickDuplicationCheck").val("y");
            }
        },
        error: function () {
            alert("닉네임 중복 체크에 실패하였습니다.");
        }
    })
}

//제출 전 체크
function check() {
    // event prevent
    console.log("체크 함수 실행됨");
    //3가지 인증절차를 모두 통과하였는지 확인하기 위해 값을 가져온다. 값이 "y"가 아니면 인증이 모두 성공하지 못한것이다.
    let passwordDuplicationCheck = $('#passwordDuplicationCheck').val();
    let emailDuplicationCheck = $("#emailDuplicationCheck").val();
    let nickDuplicationCheck = $('#nickDuplicationCheck').val();

    // 비밀번호 유효성 검사 통과했는지 확인
    if (!validatePassword()) {
        alert("비밀번호를 규칙에 맞게 입력해 주십시오.");
        return false;
    }

    // 비밀번호 규약을 제대로 인증받았는지 확인
    if (passwordDuplicationCheck === "n") {
        alert("비밀번호를 정확히 입력해 주십시오.");
        return false;
    }


    // 이메일 인증절차를 통과하였는지 확인
    if (!(isLoggedEmail() || emailDuplicationCheck === "y")) {
        console.log("isLoggedEmail : ", isLoggedEmail());
        console.log("emailDuplicationCheck : ", emailDuplicationCheck);
        alert("이메일을 인증하여 주십시오.");
        return false;
    }

    // 닉네임 인증절차를 통과하였는지 확인
    if (!(isLoggedNickname() || nickDuplicationCheck === "y")) {
        console.log("isLoggedNickname : ", isLoggedNickname());
        console.log("nickDuplicationCheck : ", nickDuplicationCheck);

        alert("닉네임 중복확인이 이루어지지 않았습니다.");
        return false;
    }

    return true;

}


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


