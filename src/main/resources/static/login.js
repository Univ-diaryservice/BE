// login.js
window.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('.login-form');
    const userIdInput = document.getElementById('user-id');
    const userPwInput = document.getElementById('user-pw');
    const errorDiv = document.getElementById('login-error');
    const togglePwBtn = document.querySelector('.toggle-pw');

    // 입력 시작하면 에러 숨기기
    [userIdInput, userPwInput].forEach(input => {
        input.addEventListener('input', () => {
            errorDiv.style.display = 'none';
        });
    });

    // 로그인 검증 함수 (실제 API 연동)
    window.validateLogin = function(event) {
        event.preventDefault();

        const id = userIdInput.value.trim();
        const pw = userPwInput.value;

        fetch("https://localhost:8080/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: id,
                password: pw
            })
        })
            .then(res => {
                if (!res.ok) throw new Error("로그인 실패");
                return res.json();
            })
            .then(data => {
                // 토큰 저장
                localStorage.setItem("accessToken", data.accessToken);
                localStorage.setItem("refreshToken", data.refreshToken);

                alert("로그인 성공!");
                location.href = "today.html"; // 로그인 성공 시 페이지 이동
            })
            .catch(err => {
                errorDiv.style.display = 'block';
            });
    };

    // 비밀번호 보기/숨기기 토글
    togglePwBtn.addEventListener('click', () => {
        const currentType = userPwInput.getAttribute('type');
        userPwInput.setAttribute('type', currentType === 'password' ? 'text' : 'password');
    });
});

async function fetchWithAuth(url, options = {}) {
    const accessToken = localStorage.getItem("accessToken");
    const refreshToken = localStorage.getItem("refreshToken");

    options.headers = {
        ...(options.headers || {}),
        "Authorization": `Bearer ${accessToken}`,
        "Content-Type": "application/json"
    };

    let res = await fetch(url, options);

    if (res.status === 401 || res.status === 403) {
        // ✅ 여기서는 fetch() 써야함 (자기 자신 부르면 안 됨!)
        const refreshRes = await fetch("https://서버주소/api/auth/refresh", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ refreshToken })
        });

        if (refreshRes.ok) {
            const data = await refreshRes.json();
            localStorage.setItem("accessToken", data.accessToken);

            // 새 accessToken으로 다시 원래 요청
            options.headers["Authorization"] = `Bearer ${data.accessToken}`;
            res = await fetch(url, options);
        } else {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            localStorage.clear();
            location.href = "login1.html";
            return;
        }
    }

    return res;
}
