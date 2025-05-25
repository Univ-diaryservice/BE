let diaries = JSON.parse(localStorage.getItem('diaries')) || [];
let currentPage = 1;
let itemsPerPage = 10;
let currentFilteredDiaries = diaries;
let selectedSort = 'desc';

function addDiary(title, content, date, feeling, weather) {
    const firstLine = content.split('\n')[0] || '';
    const previewContent = firstLine.length > 15 ? firstLine.substring(0, 15) + "..." : firstLine;

    const existingDiaryIndex = diaries.findIndex(diary => diary.date === date);
    if (existingDiaryIndex !== -1) {
        diaries[existingDiaryIndex] = { title, content, previewContent, date, feeling, weather, favorite: diaries[existingDiaryIndex].favorite || false };
    } else {
        diaries.push({ title, content, previewContent, date, feeling, weather, favorite: false });
    }
    localStorage.setItem('diaries', JSON.stringify(diaries));
    console.log("일기 저장됨:", { title, content, previewContent, date, feeling, weather });
}

window.onload = function() {
    const savedDiaries = JSON.parse(localStorage.getItem('diaries'));
    if (savedDiaries) {
        diaries = savedDiaries.map(diary => ({
            ...diary,
            previewContent: diary.previewContent || (diary.content.length > 15 ? diary.content.substring(0, 15) + "..." : diary.content),
            favorite: diary.favorite || false
        }));
        console.log("로컬 스토리지에서 불러온 다이어리:", diaries);
    } else {
        console.log("로컬 스토리지에 데이터 없음");
    }

    // 쿼리 파라미터에서 date 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const dateParam = urlParams.get('date');
    let year, month;

    if (dateParam) {
        const dateObj = new Date(dateParam);
        if (!isNaN(dateObj)) {
            year = dateObj.getFullYear();
            month = dateObj.getMonth() + 1;
            console.log("쿼리 파라미터에서 날짜 파싱:", { year, month, date: dateParam });
        }
    }

    if (!year || !month) {
        const today = new Date();
        year = today.getFullYear();
        month = today.getMonth() + 1;
    }

    document.querySelector('.date').textContent = `${year}년 ${month}월`;
    currentFilteredDiaries = diaries.filter(diary => {
        const dateObj = new Date(diary.date);
        return dateObj.getFullYear() === year && (dateObj.getMonth() + 1) === month;
    });

    console.log("필터링된 다이어리:", currentFilteredDiaries);
    displayDiaries(currentFilteredDiaries);
    sortDiaries('desc');
};

function showDateModal() {
    const modal = document.getElementById('dateModal');
    if (!modal) {
        console.error("dateModal not found");
        return;
    }
    console.log("Opening dateModal");
    modal.classList.add("show");

    // 현재 표시된 연도 설정
    const currentYearElement = document.querySelector('.current-year');
    const currentYear = parseInt(document.querySelector('.date').textContent.split('년')[0]) || 2025;
    currentYearElement.textContent = currentYear;

    // 월 버튼 선택 상태 업데이트
    const currentMonth = parseInt(document.querySelector('.date').textContent.split('년 ')[1]?.split('월')[0]) || 5;
    const monthButtons = document.querySelectorAll('.month-btn');
    monthButtons.forEach(btn => {
        btn.classList.remove('selected');
        if (btn.getAttribute('data-month') === ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'][currentMonth - 1]) {
            btn.classList.add('selected');
        }
    });

    // 연도 내비게이션 버튼 이벤트
    document.querySelector('.prev-year').onclick = () => {
        currentYearElement.textContent = parseInt(currentYearElement.textContent) - 1;
    };
    document.querySelector('.next-year').onclick = () => {
        currentYearElement.textContent = parseInt(currentYearElement.textContent) + 1;
    };

    // 월 버튼 클릭 이벤트
    monthButtons.forEach(btn => {
        btn.onclick = () => {
            monthButtons.forEach(b => b.classList.remove('selected'));
            btn.classList.add('selected');
        };
    });

    // 확인 버튼 이벤트
    document.querySelector('.confirm-btn').onclick = () => {
        const selectedYear = parseInt(currentYearElement.textContent);
        const selectedMonth = document.querySelector('.month-btn.selected')?.getAttribute('data-month');
        const monthIndex = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'].indexOf(selectedMonth) + 1;
        document.querySelector('.date').textContent = `${selectedYear}년 ${monthIndex}월`;
        currentFilteredDiaries = diaries.filter(diary => {
            const dateObj = new Date(diary.date);
            return dateObj.getFullYear() === selectedYear && (dateObj.getMonth() + 1) === monthIndex;
        });
        modal.classList.remove("show");
        displayDiaries(currentFilteredDiaries);
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.classList.remove("show");
            console.log("dateModal closed by clicking outside");
        }
    };
}

function showAllDiaries() {
    const modal = document.getElementById('dateModal');
    if (modal) modal.classList.remove("show");
    document.querySelector('.date').textContent = '전체 일기';
    currentFilteredDiaries = diaries;
    displayDiaries(currentFilteredDiaries);
}

function showSortModal() {
    const modal = document.getElementById('sortModal');
    if (!modal) {
        console.error("sortModal not found");
        return;
    }
    console.log("Opening sortModal");
    modal.classList.add("show");

    const sortOptions = document.querySelectorAll(".sort-option");
    if (sortOptions.length === 0) {
        console.error("No sort-option elements found");
        return;
    }
    sortOptions.forEach(option => {
        option.classList.remove("selected");
        if (option.getAttribute("data-sort") === selectedSort) {
            option.classList.add("selected");
        }
        option.onclick = function() {
            sortOptions.forEach(opt => opt.classList.remove("selected"));
            this.classList.add("selected");
            selectedSort = this.getAttribute("data-sort");
            console.log("Selected sort:", selectedSort);
        };
    });

    const confirmBtn = document.querySelector("#sortModal .confirm-btn");
    if (confirmBtn) {
        confirmBtn.onclick = function() {
            console.log("Confirm button clicked, applying sort:", selectedSort);
            sortDiaries(selectedSort);
            modal.classList.remove("show");
        };
    } else {
        console.error("Confirm button not found");
    }

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.classList.remove("show");
            console.log("sortModal closed by clicking outside");
        }
    };
}

function sortDiaries(order) {
    console.log("Sorting diaries with order:", order);
    selectedSort = order;
    const sortButton = document.querySelector('.sort-btn');
    if (!sortButton) {
        console.error("sort-btn not found");
        return;
    }
    sortButton.innerHTML = `${order === 'asc' ? '오래된순' : '최신순'} <img src="image/changetime.svg" alt="Change Time" style="vertical-align: middle; width: 16px; height: 16px;">`;

    diaries.sort((a, b) => {
        const dateA = new Date(a.date);
        const dateB = new Date(b.date);
        if (order === 'asc') {
            return dateA.getTime() - dateB.getTime();
        } else {
            return dateB.getTime() - dateA.getTime();
        }
    });

    console.log("Sorted diaries:", diaries.map(d => `${d.title}: ${d.date}`));
    currentFilteredDiaries = diaries.filter(diary => {
        const dateObj = new Date(diary.date);
        const [year, month] = document.querySelector('.date').textContent.split('년 ')[0] === '전체' ? [null, null] : document.querySelector('.date').textContent.split('년 ').map((v, i) => i === 0 ? parseInt(v) : parseInt(v.split('월')[0]));
        return !year || !month || (dateObj.getFullYear() === year && (dateObj.getMonth() + 1) === month);
    });
    displayDiaries(currentFilteredDiaries);
}

function displayDiaries(filteredDiaries = currentFilteredDiaries) {
    const list = document.getElementById('diaryList');
    if (!list) {
        console.error("diaryList not found");
        return;
    }
    list.innerHTML = '';
    const filter = document.getElementById('favoriteFilter')?.checked ?? false;
    let displayDiaries = filteredDiaries;

    if (filter) {
        displayDiaries = displayDiaries.filter(d => d.favorite === true);
    }

    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const paginatedDiaries = displayDiaries.slice(start, end);
    console.log("표시할 다이어리:", paginatedDiaries);

    paginatedDiaries.forEach(diary => {
        const previewContent = diary.previewContent || (diary.content.length > 15 ? diary.content.substring(0, 15) + "..." : diary.content);
        const dateObj = new Date(diary.date);
        const formattedDate = `${dateObj.getFullYear()}년 ${dateObj.getMonth() + 1}월 ${dateObj.getDate()}일`;

        const item = document.createElement('div');
        item.className = 'diary-item';
        item.innerHTML = `
            <div class="diary-card" onclick="goToWrite('${diary.date}')">
                <div class="diary-date">${formattedDate} · ${diary.feeling}</div>
                <div class="diary-title">${diary.title}</div>
                <div class="diary-content">${previewContent}</div>
                <span class="star" onclick="toggleFavorite(event, this, '${diary.date}')"><img src="${diary.favorite ? 'image/filledStar.svg' : 'image/emptyStar.svg'}" alt="Star" class="star-icon"></span>
            </div>
        `;
        list.appendChild(item);
    });

    updatePagination(displayDiaries.length);
}

function goToWrite(date) {
    console.log("카드 클릭 - 이동할 날짜:", date);
    window.location.href = `write.html?date=${encodeURIComponent(date)}`;
}

function toggleFavorite(event, star, date) {
    event.stopPropagation();
    const diary = diaries.find(d => d.date === date);
    if (diary) {
        diary.favorite = !diary.favorite;
        const starIcon = star.querySelector('.star-icon');
        starIcon.src = diary.favorite ? 'image/filledStar.svg' : 'image/emptyStar.svg';
        localStorage.setItem('diaries', JSON.stringify(diaries));
        displayDiaries(currentFilteredDiaries);
    }
}

function updatePagination(totalItems) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const pagination = document.getElementById('pagination');
    if (!pagination) {
        console.error("pagination not found");
        return;
    }
    pagination.innerHTML = '';
    if (totalPages > 0) {
        const first = document.createElement('button');
        first.disabled = currentPage === 1;
        first.className = first.disabled ? 'disabled' : 'enabled';
        first.innerHTML = `<object type="image/svg+xml" data="image/Firstpage_${first.disabled ? 'disabled' : 'enabled'}.svg" class="pagination-icon"></object>`;
        first.onclick = () => { if (!first.disabled) { currentPage = 1; displayDiaries(currentFilteredDiaries); } };
        const firstObject = first.querySelector('object');
        if (firstObject) {
            firstObject.onerror = () => console.error('Failed to load Firstpage SVG');
        }
        pagination.appendChild(first);

        const prev = document.createElement('button');
        prev.disabled = currentPage === 1;
        prev.className = prev.disabled ? 'disabled' : 'enabled';
        prev.innerHTML = `<object type="image/svg+xml" data="image/prevpage_${prev.disabled ? 'disabled' : 'enabled'}.svg" class="pagination-icon"></object>`;
        prev.onclick = () => { if (!prev.disabled) { currentPage--; displayDiaries(currentFilteredDiaries); } };
        const prevObject = prev.querySelector('object');
        if (prevObject) {
            prevObject.onerror = () => console.error('Failed to load prevpage SVG');
        }
        pagination.appendChild(prev);

        for (let i = 1; i <= totalPages; i++) {
            const page = document.createElement('button');
            page.textContent = i;
            if (i === currentPage) {
                page.style.color = '#000';
                page.style.fontWeight = 'bold';
            }
            page.onclick = () => { currentPage = i; displayDiaries(currentFilteredDiaries); };
            pagination.appendChild(page);
        }

        const next = document.createElement('button');
        next.disabled = currentPage === totalPages;
        next.className = next.disabled ? 'disabled' : 'enabled';
        next.innerHTML = `<object type="image/svg+xml" data="image/nextpage_${next.disabled ? 'disabled' : 'enabled'}.svg" class="pagination-icon"></object>`;
        next.onclick = () => { if (!next.disabled) { currentPage++; displayDiaries(currentFilteredDiaries); } };
        const nextObject = next.querySelector('object');
        if (nextObject) {
            nextObject.onerror = () => console.error('Failed to load nextpage SVG');
        }
        pagination.appendChild(next);

        const last = document.createElement('button');
        last.disabled = currentPage === totalPages;
        last.className = last.disabled ? 'disabled' : 'enabled';
        last.innerHTML = `<object type="image/svg+xml" data="image/Lastpage_${last.disabled ? 'disabled' : 'enabled'}.svg" class="pagination-icon"></object>`;
        last.onclick = () => { if (!last.disabled) { currentPage = totalPages; displayDiaries(currentFilteredDiaries); } };
        const lastObject = last.querySelector('object');
        if (lastObject) {
            lastObject.onerror = () => console.error('Failed to load Lastpage SVG');
        }
        pagination.appendChild(last);
    }
}

document.getElementById('favoriteFilter').onchange = () => displayDiaries(currentFilteredDiaries);