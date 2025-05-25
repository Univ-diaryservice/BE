document.addEventListener('DOMContentLoaded', function() {
    const calendarDates = document.getElementById("calendarDates");
    const currentMonthElement = document.getElementById("currentMonth");
    const prevMonth = document.getElementById("prevMonth");
    const nextMonth = document.getElementById("nextMonth");
    const nowWeek = document.getElementById("nowWeek");
    const prevWeekBtn = document.getElementById("prevWeek");
    const nextWeekBtn = document.getElementById("nextWeek");
    const cluster = document.getElementById("grapeCluster");
    const today = new Date();
    let currentMonth = today.getMonth();
    let currentYear = today.getFullYear();
    let selectedDate = new Date();

    // localStorage에서 다이어리 데이터 로드
    let diaries = JSON.parse(localStorage.getItem('diaries')) || [];
    console.log("로컬 스토리지에서 불러온 다이어리:", diaries);

    const days = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];

    // 포도줄기 이미지 추가
    const stemImg = document.createElement("img");
    stemImg.src = "image/grape_stem.png";
    stemImg.alt = "포도꽁다리";
    stemImg.className = "grape-stem";
    cluster.appendChild(stemImg);

    // 초기 포도알 이미지 생성
    days.forEach(day => {
        const img = document.createElement("img");
        img.src = `grapeimg/${day}_NULL.svg`;
        img.alt = `${day} - NULL`;
        img.className = `grape ${day}`;
        cluster.appendChild(img);
    });

    function displayToday() {
        const todayElement = document.getElementById("currentday");
        const daysOfWeek = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
        const year = today.getFullYear();
        const month = today.getMonth() + 1;
        const date = today.getDate();
        const day = daysOfWeek[today.getDay()];
        todayElement.textContent = `${year}년 ${month}월 ${date}일 ${day}`;
    }

    displayToday();

    function renderCalendar() {
        const firstDayOfMonth = new Date(currentYear, currentMonth, 1);
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
        const startDayOfWeek = firstDayOfMonth.getDay();
        currentMonthElement.textContent = `${currentYear}년 ${currentMonth + 1}월`;

        calendarDates.innerHTML = "";

        for (let i = 0; i < startDayOfWeek; i++) {
            const emptyDate = document.createElement("div");
            emptyDate.classList.add("date", "empty");
            calendarDates.appendChild(emptyDate);
        }

        for (let i = 1; i <= daysInMonth; i++) {
            const dateElement = document.createElement("div");
            dateElement.classList.add("date");
            const dateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
            const diary = diaries.find(d => d.date === dateStr);
            const hasDiary = diary ? "has-diary" : "";
            // 요일 계산 (0: 일요일, 6: 토요일)
            const dateObj = new Date(currentYear, currentMonth, i);
            const day = days[dateObj.getDay()];
            // 기분 이미지 설정
            const imgSrc = diary ? `grapeimg/${day}_${diary.feeling}.svg` : "image/circle.png";
            const imgAlt = diary ? `${day} - ${diary.feeling}` : "circle";
            dateElement.innerHTML = `
                <div class="date-number ${hasDiary}">${i}</div>
                <img class="date-circle" src="${imgSrc}" alt="${imgAlt}">
            `;
            // 날짜 클릭 시 write.html로 이동
            dateElement.addEventListener("click", () => {
                window.location.href = `write.html?date=${dateStr}`;
            });
            calendarDates.appendChild(dateElement);
            console.log(`날짜 ${dateStr} 이미지: ${imgSrc}`);
        }
    }

    renderCalendar();

    prevMonth.addEventListener("click", () => {
        currentMonth--;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }
        renderCalendar();
        updateMonthMood();
    });

    nextMonth.addEventListener("click", () => {
        currentMonth++;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }
        renderCalendar();
        updateMonthMood();
    });

    function getWeekKey(date) {
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const firstDay = new Date(date.getFullYear(), date.getMonth(), 1).getDay();
        const weekNumber = Math.ceil((date.getDate() + firstDay) / 7);
        return `${year}-${month}-W${weekNumber}`;
    }

    function getWeekRange(date) {
        const start = new Date(date);
        const day = start.getDay();
        start.setDate(start.getDate() - day); // 주의 시작 (일요일)
        const startDate = new Date(start);
        const endDate = new Date(startDate);
        endDate.setDate(startDate.getDate() + 6); // 주의 끝 (토요일)
        return {
            start: `${startDate.getMonth() + 1}.${startDate.getDate().toString().padStart(2, '0')}`,
            end: `${endDate.getMonth() + 1}.${endDate.getDate().toString().padStart(2, '0')}`,
            dates: Array.from({ length: 7 }, (_, i) => {
                const d = new Date(startDate);
                d.setDate(startDate.getDate() + i);
                return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
            })
        };
    }

    function updateWeekDisplay() {
        if (!nowWeek) {
            console.error("nowWeek 요소를 찾을 수 없습니다.");
            return;
        }
        const key = getWeekKey(selectedDate);
        const range = getWeekRange(selectedDate);
        nowWeek.innerHTML = `${selectedDate.getMonth() + 1}월 ${Math.ceil((selectedDate.getDate() + new Date(selectedDate.getFullYear(), selectedDate.getMonth(), 1).getDay()) / 7)}째주 포도송이<br>(${range.start} - ${range.end})`;

        // 주간 날짜와 다이어리 데이터 매핑
        range.dates.forEach((date, index) => {
            const day = days[index];
            const diary = diaries.find(d => d.date === date);
            const emotion = diary ? diary.feeling : "NULL";
            const img = cluster.querySelector(`.grape.${day}`);
            if (img) {
                img.src = `grapeimg/${day}_${emotion}.svg`;
                img.alt = `${day} - ${emotion}`;
            } else {
                console.warn(`.grape.${day} 이미지를 찾을 수 없습니다.`);
            }
        });
    }

    prevWeekBtn.addEventListener("click", () => {
        selectedDate.setDate(selectedDate.getDate() - 7);
        updateWeekDisplay();
    });

    nextWeekBtn.addEventListener("click", () => {
        selectedDate.setDate(selectedDate.getDate() + 7);
        updateWeekDisplay();
    });

    // 이번 달 기분 통계 업데이트
    function updateMonthMood() {
        const countSpans = document.querySelectorAll('.month-mood-total .count');
        const feelingMap = {
            'HAPPY': 0,
            'BLESSING': 1,
            'SOSO': 2,
            'SAD': 3,
            'MAD': 4
        };
        const feelingCounts = [0, 0, 0, 0, 0]; // HAPPY, BLESSING, SOSO, SAD, MAD

        // 현재 달의 일기 필터링
        const monthDiaries = diaries.filter(diary => {
            const date = new Date(diary.date);
            return date.getFullYear() === currentYear && date.getMonth() === currentMonth;
        });

        // 기분별 개수 집계
        monthDiaries.forEach(diary => {
            const index = feelingMap[diary.feeling];
            if (index !== undefined) {
                feelingCounts[index]++;
            }
        });

        // UI 업데이트
        feelingCounts.forEach((count, index) => {
            countSpans[index].textContent = `× ${count}`;
        });

        console.log("이번 달 기분 통계:", {
            HAPPY: feelingCounts[0],
            BLESSING: feelingCounts[1],
            SOSO: feelingCounts[2],
            SAD: feelingCounts[3],
            MAD: feelingCounts[4]
        });
    }

    // FAB 버튼 클릭 핸들러
    window.handleFabClick = function() {
        const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
        window.location.href = `write.html?date=${todayStr}`;
    };

    // 초기 렌더링
    updateWeekDisplay();
    updateMonthMood();

    // 1년 전 일기 (임시로 비워둠, 필요 시 구현)
    const diaryFromLastYear = {
        title: "",
        feeling: "",
        weather: ""
    };
    document.querySelector(".last-title").textContent = "제목: " + (diaryFromLastYear.title || "없음");
    document.querySelector(".last-feeling").textContent = "기분: " + (diaryFromLastYear.feeling || "없음");
    document.querySelector(".last-weather").textContent = "날씨: " + (diaryFromLastYear.weather || "없음");

    document.getElementById("gotodiarybtn").addEventListener("click", function () {
        const lastYear = new Date(today);
        lastYear.setFullYear(today.getFullYear() - 1);
        const dateStr = `${lastYear.getFullYear()}-${String(lastYear.getMonth() + 1).padStart(2, '0')}-${String(lastYear.getDate()).padStart(2, '0')}`;
        window.location.href = `write.html?date=${dateStr}`;
    });
});