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
        listType: ''
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
    $(document).on('click', '.dataTable tr.data-row', function(e) {
        // 클릭한 요소가 링크나 버튼인 경우 이벤트 중단
        if ($(e.target).is('a') || $(e.target).is('button') || $(e.target).parents('a').length > 0) {
            return;
        }
        const url = $(this).data('url');
        if (url) {
            window.location.href = url;
        }
    });

    // 데이터 가져오기 함수
    function fetchData(listType, url) {
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
                // 테이블 구조 시작
                let tableHtml = '<table class="dataTable"><thead><tr>';

                // 테이블 헤더 정의
                if (listType === 'give') {
                    tableHtml += '<th>날짜</th><th>제목</th><th>내용</th><th>상태</th>';
                } else if (listType === 'bookmark') {
                    tableHtml += '<th>날짜</th><th>제목</th><th>내용</th>';
                }

                tableHtml += '</tr></thead><tbody>';

                pagedData.forEach(item => {
                    tableHtml += generateItemHtml(listType, item);
                });

                tableHtml += '</tbody></table>';

                $("#showData").append(tableHtml);
            } else {
                // 다른 목록 타입에 대한 처리
                pagedData.forEach(item => {
                    const itemHtml = generateItemHtml(listType, item);
                    $("#showData").append(itemHtml);
                });
            }
        } else {
            $("#showData").html("<p>데이터가 없습니다.</p>");
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
                    <img src="/images/trash.png">
                </div>
                <!-- 왼쪽 하단_알림 내용 영역 -->
                <div class="alertContentArea">
                    <div>
                        <span class="settingAlert alarmModify">편집</span>
                        <span class="settingDelete alarmDelete">삭제</span>
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
        <tr class="data-row" data-url="/share/read?shareNum=${item.shareNum}">
            <td><a href="/share/read?shareNum=${item.shareNum}">${item.shareDate}</a></td>
            <td><a href="/share/read?shareNum=${item.shareNum}">${item.shareTitle}</a></td>
            <td><a href="/share/read?shareNum=${item.shareNum}">${item.shareContents}</a></td>
            <td><a href="/share/read?shareNum=${item.shareNum}">${item.shareCompleted}</a></td>
        </tr>
    `;
            case 'bookmark':
                return `
        <tr class="data-row" data-url="/share/read?shareNum=${item.shareNum}">
            <td><a href="/share/read?shareNum=${item.shareNum}">${item.shareDate}</a></td>
            <td><a href="/share/read?shareNum=${item.shareNum}">${item.shareTitle}</a></td>
            <td><a href="/share/read?shareNum=${item.shareNum}">${item.shareContents}</a></td>
        </tr>
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
                <div class="addAlert" >
                    <!-- 버튼 -->
                    <button class="addAlertButton" id="alarmAdd">+</button>
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

            return;
        }

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
        let paginationHtml = '<div class="pagination">';

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

        paginationHtml += '</div>';
        return paginationHtml;
    }
});