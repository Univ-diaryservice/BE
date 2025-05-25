document.addEventListener("DOMContentLoaded", function() {
    console.log("DOM 로드 완료");

    // 현재 날짜 및 요일 표시 (영어 약어 사용, 요일 표시 제거)
    function displayToday() {
        const todayElement = document.getElementById("currentday");
        const today = new Date();
        const month = today.getMonth() + 1;
        const date = today.getDate();
        const days = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];
        const day = days[today.getDay()];
        todayElement.textContent = `${month}월 ${date}일`; // 요일 제거
        return day;
    }
    const currentDay = displayToday();

    // 완료 버튼과 입력란 요소 가져오기
    const doneButton = document.querySelector(".done");
    const titleInput = document.getElementById("titleInput");
    const bodyInput = document.getElementById("bodyInput");

    // 기분 모달 창 관련 요소
    const feelingButton = document.getElementById("feelingButton");
    const feelingModal = document.getElementById("feelingModal");
    const selectedFeelingSvg = document.getElementById("selectedFeelingSvg");
    const selectedFeelingText = document.getElementById("selectedFeelingText");
    const feelingOptions = document.querySelectorAll(".feeling-option");
    const confirmFeelingButton = document.getElementById("confirmFeelingButton");
    const selectedFeelingDisplay = document.getElementById("selectedFeelingDisplay");

    // 날씨 모달 창 관련 요소
    const weatherButton = document.getElementById("weatherButton");
    const weatherModal = document.getElementById("weatherModal");
    const selectedWeatherSvg = document.getElementById("selectedWeatherSvg");
    const weatherOptions = document.querySelectorAll(".weather-option");
    const confirmWeatherButton = document.getElementById("confirmWeatherButton");
    const selectedWeatherDisplay = document.getElementById("selectedWeatherDisplay");

    // 요소 존재 여부 확인
    console.log("feelingButton:", feelingButton);
    console.log("feelingModal:", feelingModal);
    console.log("selectedFeelingDisplay:", selectedFeelingDisplay);
    console.log("weatherButton:", weatherButton);
    console.log("weatherModal:", weatherModal);
    console.log("selectedWeatherDisplay:", selectedWeatherDisplay);

    // SVG 파일 경로 생성 함수
    function getSvgPath(day, type) {
        const basePath = "grapeimg/";
        return `${basePath}${day}_${type}.svg`; // 예: "grapeimg/MONHAPPY.svg"
    }

    function getWeatherSvgPath(weathername) {
        const basePath = "weatherimg/";
        return `${basePath}${weathername}.svg`;
    }

    // 기분에 따른 멘트 매핑
    const feelingMessages = {
        "HAPPY": "최고에요",
        "BLESSING": "좋아요",
        "SOSO": "보통이에요",
        "SAD": "슬퍼요",
        "MAD": "화나요"
    };

    // 초기 기분 SVG 설정
    const initialFeelingSvgPath = getSvgPath(currentDay, "HAPPY");
    console.log("초기 기분 SVG 경로:", initialFeelingSvgPath);
    selectedFeelingSvg.src = initialFeelingSvgPath;
    selectedFeelingText.textContent = feelingMessages["HAPPY"];

    selectedFeelingSvg.onerror = function() {
        console.error("기분 SVG 이미지 로드 실패:", initialFeelingSvgPath);
    };
    selectedFeelingSvg.onload = function() {
        console.log("기분 SVG 이미지 로드 성공:", initialFeelingSvgPath);
    };

    // 초기 날씨 SVG 설정
    const initialWeatherSvgPath = getWeatherSvgPath("SUNNY");
    console.log("초기 날씨 SVG 경로:", initialWeatherSvgPath);
    selectedWeatherSvg.src = initialWeatherSvgPath;

    selectedWeatherSvg.onerror = function() {
        console.error("날씨 SVG 이미지 로드 실패:", initialWeatherSvgPath);
    };
    selectedWeatherSvg.onload = function() {
        console.log("날씨 SVG 이미지 로드 성공:", initialWeatherSvgPath);
    };

    // 완료 버튼 상태 업데이트 함수
    function updateDoneButton() {
        const hasContent = titleInput.value.trim() !== "" || bodyInput.value.trim() !== "";
        if (hasContent) {
            doneButton.style.color = "black";
            doneButton.disabled = false;
        } else {
            doneButton.style.color = "lightgray";
            doneButton.disabled = true;
        }
    }

    // addDiary 함수 (전체 내용 저장)
    function addDiary(title, content, date, feeling, weather) {
        let diaries = JSON.parse(localStorage.getItem('diaries')) || [];
        const previewContent = content.split('\n')[0]?.substring(0, 15) + (content.split('\n')[0]?.length > 15 ? "..." : "") || "";

        const existingDiaryIndex = diaries.findIndex(diary => diary.date === date);
        if (existingDiaryIndex !== -1) {
            diaries[existingDiaryIndex] = { title, content, previewContent, date, feeling, weather, favorite: diaries[existingDiaryIndex].favorite || false };
        } else {
            diaries.push({ title, content, previewContent, date, feeling, weather, favorite: false });
        }
        localStorage.setItem('diaries', JSON.stringify(diaries));
        console.log("일기 저장됨:", { title, content, previewContent, date, feeling, weather });
    }

    // 쿼리 파라미터에서 날짜 읽고 데이터 로드
    function loadDiaryFromDate() {
        const urlParams = new URLSearchParams(window.location.search);
        const date = urlParams.get('date');
        if (date) {
            console.log("쿼리 파라미터에서 날짜:", date);
            const diaries = JSON.parse(localStorage.getItem('diaries')) || [];
            const diary = diaries.find(d => d.date === date);
            if (diary) {
                console.log("불러온 일기:", diary);
                titleInput.value = diary.title || "";
                bodyInput.value = diary.content || "";

                // 기분 설정
                const feeling = diary.feeling || "HAPPY";
                const svgPath = getSvgPath(currentDay, feeling);
                selectedFeelingSvg.src = svgPath;
                selectedFeelingText.textContent = feelingMessages[feeling] || "기분 선택...";
                feelingOptions.forEach(opt => opt.classList.remove("selected"));
                const selectedOption = Array.from(feelingOptions).find(opt => opt.getAttribute("data-feeling") === feeling);
                if (selectedOption) {
                    selectedOption.classList.add("selected");
                    selectedFeelingDisplay.innerHTML = `
                        <img src="${svgPath}" alt="선택된 기분" style="width: 20px; height: 20px; vertical-align: middle;">
                        <span>${feelingMessages[feeling]}</span>
                    `;
                }

                // 날씨 설정
                const weather = diary.weather || "SUNNY";
                const weatherSvgPath = getWeatherSvgPath(weather);
                selectedWeatherSvg.src = weatherSvgPath;
                weatherOptions.forEach(opt => opt.classList.remove("selected"));
                const selectedWeatherOption = Array.from(weatherOptions).find(opt => opt.getAttribute("data-weather") === weather);
                if (selectedWeatherOption) {
                    selectedWeatherOption.classList.add("selected");
                    selectedWeatherDisplay.innerHTML = `
                        <img src="${weatherSvgPath}" alt="선택된 날씨" style="width: 20px; height: 20px; vertical-align: middle;">
                    `;
                }

                // 완료 버튼 상태 업데이트
                updateDoneButton();
            } else {
                console.log("해당 날짜의 일기 없음:", date);
            }
        }
    }

    // 초기 상태 설정
    doneButton.disabled = true;
    updateDoneButton();
    loadDiaryFromDate(); // 페이지 로드 시 데이터 로드

    // 입력 이벤트 리스너
    titleInput.addEventListener("input", updateDoneButton);
    bodyInput.addEventListener("input", updateDoneButton);

    // 기분 선택 버튼 클릭 시 모달 창 열기
    if (feelingButton && feelingModal) {
        feelingButton.addEventListener("click", function() {
            console.log("기분 모달 버튼 클릭됨");
            feelingModal.classList.add("show");
            console.log("feelingModal 클래스:", feelingModal.className);
            console.log("feelingModal 스타일 (display):", window.getComputedStyle(feelingModal).display);
        });
    } else {
        console.error("feelingButton 또는 feelingModal이 없습니다.");
    }

    // 기분(SVG) 선택 로직
    feelingOptions.forEach(option => {
        const feeling = option.getAttribute("data-feeling");
        const svgPath = getSvgPath(currentDay, feeling);
        console.log("기분 옵션 SVG 경로:", svgPath);
        option.innerHTML = `<img src="${svgPath}" alt="${feeling}">`;

        const img = option.querySelector("img");
        img.onerror = function() {
            console.error("기분 옵션 SVG 이미지 로드 실패:", svgPath);
        };
        img.onload = function() {
            console.log("기분 옵션 SVG 이미지 로드 성공:", svgPath);
        };

        option.addEventListener("click", function() {
            console.log("기분 클릭됨");
            feelingOptions.forEach(opt => opt.classList.remove("selected"));
            this.classList.add("selected");
            const feeling = this.getAttribute("data-feeling");
            const text = feelingMessages[feeling];
            const svgPath = getSvgPath(currentDay, feeling);
            selectedFeelingSvg.src = svgPath;
            selectedFeelingText.textContent = text;
            console.log("선택된 기분 SVG 경로:", svgPath);
        });
    });

    // 기분 입력 버튼 클릭 시 모달 창 닫고 선택한 기분과 멘트 표시
    if (confirmFeelingButton) {
        confirmFeelingButton.addEventListener("click", function() {
            const selectedSvg = selectedFeelingSvg.src.split("/").pop();
            const selectedFeeling = document.querySelector(".feeling-option.selected")?.getAttribute("data-feeling");
            const selectedText = selectedFeeling ? feelingMessages[selectedFeeling] : "기분 선택...";
            if (selectedFeelingDisplay) {
                selectedFeelingDisplay.innerHTML = `
                    <img src="grapeimg/${selectedSvg}" alt="선택된 기분" style="width: 20px; height: 20px; vertical-align: middle;">
                    <span>${selectedText}</span>
                `;
            } else {
                console.error("selectedFeelingDisplay 요소를 찾을 수 없습니다.");
            }
            feelingModal.classList.remove("show");
        });
    } else {
        console.error("confirmFeelingButton 요소를 찾을 수 없습니다.");
    }

    // 날씨 선택 버튼 클릭 시 모달 창 열기
    if (weatherButton && weatherModal) {
        weatherButton.addEventListener("click", function() {
            console.log("날씨 모달 버튼 클릭됨");
            weatherModal.classList.add("show");
            console.log("weatherModal 클래스:", weatherModal.className);
            console.log("weatherModal 스타일 (display):", window.getComputedStyle(weatherModal).display);
        });
    } else {
        console.error("weatherButton 또는 weatherModal이 없습니다.");
    }

    // 날씨(SVG) 선택 로직
    weatherOptions.forEach(option => {
        const weather = option.getAttribute("data-weather");
        const svgPath = getWeatherSvgPath(weather);
        console.log("날씨 옵션 SVG 경로:", svgPath);
        option.innerHTML = `<img src="${svgPath}" alt="${weather}">`;

        const img = option.querySelector("img");
        img.onerror = function() {
            console.error("날씨 옵션 SVG 이미지 로드 실패:", svgPath);
        };
        img.onload = function() {
            console.log("날씨 SVG 이미지 로드 성공:", svgPath);
        };

        option.addEventListener("click", function() {
            console.log("날씨 클릭됨");
            weatherOptions.forEach(opt => opt.classList.remove("selected"));
            this.classList.add("selected");
            const weather = this.getAttribute("data-weather");
            const svgPath = getWeatherSvgPath(weather);
            selectedWeatherSvg.src = svgPath;
            console.log("선택된 날씨 SVG 경로:", svgPath);
        });
    });

    // 날씨 입력 버튼 클릭 시 모달 창 닫고 선택한 날씨 표시
    if (confirmWeatherButton) {
        confirmWeatherButton.addEventListener("click", function() {
            const selectedSvg = selectedWeatherSvg.src.split("/").pop();
            if (selectedWeatherDisplay) {
                selectedWeatherDisplay.innerHTML = `
                    <img src="weatherimg/${selectedSvg}" alt="선택된 날씨" style="width: 20px; height: 20px; vertical-align: middle;">
                `;
            } else {
                console.error("selectedWeatherDisplay 요소를 찾을 수 없습니다.");
            }
            weatherModal.classList.remove("show");
        });
    } else {
        console.error("confirmWeatherButton 요소를 찾을 수 없습니다.");
    }

    // 완료 버튼 클릭 시 데이터 저장, URL 업데이트, ltest.html로 리디렉션
    if (doneButton && feelingModal && weatherModal) {
        doneButton.addEventListener("click", function(event) {
            event.preventDefault(); // 기본 동작 방지
            console.log("완료 버튼 클릭됨");

            // 모달 창 닫기
            if (feelingModal.classList.contains("show")) {
                feelingModal.classList.remove("show");
                console.log("기분 모달 창 닫힘");
            }
            if (weatherModal.classList.contains("show")) {
                weatherModal.classList.remove("show");
                console.log("날씨 모달 창 닫힘");
            }

            // 현재 날짜를 YYYY-MM-DD 형식으로 생성
            const today = new Date();
            const year = today.getFullYear();
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const day = String(today.getDate()).padStart(2, '0');
            const dateString = `${year}-${month}-${day}`;

            // 입력 데이터 가져오기
            const title = titleInput.value.trim();
            const content = bodyInput.value.trim();
            const selectedFeeling = document.querySelector(".feeling-option.selected")?.getAttribute("data-feeling") || "HAPPY";
            const selectedWeather = document.querySelector(".weather-option.selected")?.getAttribute("data-weather") || "SUNNY";

            // 데이터 저장
            if (title || content) {
                addDiary(title, content, dateString, selectedFeeling, selectedWeather);
            } else {
                console.warn("제목 또는 내용이 비어 있어 일기를 저장하지 않음");
            }

            // write.html의 URL을 write.html?date=YYYY-MM-DD로 업데이트
            history.pushState({}, "", `write.html?date=${dateString}`);
            console.log("write.html URL 업데이트:", `write.html?date=${dateString}`);

            // ltest.html?date=YYYY-MM-DD로 리디렉션
            window.location.href = `ltest.html?date=${dateString}`;
            console.log("리디렉션 URL:", `ltest.html?date=${dateString}`);
        });
    } else {
        console.error("doneButton, feelingModal 또는 weatherModal이 없습니다.");
    }
});