document.addEventListener("DOMContentLoaded", function () {
    const steps = {
        email: document.getElementById("step-email"),
        password: document.getElementById("step-password"),
        nickname: document.getElementById("step-nickname"),
        confirm: document.getElementById("step-confirm"),
        complete: document.getElementById("step-complete"),
    };

    const inputs = {
        email: document.getElementById("email"),
        password: document.getElementById("password"),
        nickname: document.getElementById("nickname"),
    };

    const buttons = {
        email: document.getElementById("email-next"),
        password: document.getElementById("password-next"),
        nickname: document.getElementById("nickname-next"),
        confirm: document.getElementById("confirm-submit"),
    };

    const errors = {
        email: document.getElementById("email-error"),
        password: document.getElementById("password-error"),
        nickname: document.getElementById("nickname-error"),
    };

    const previews = {
        email: document.getElementById("email-preview"),
        emailPassword: document.getElementById("email-password-preview"),
    };

    const confirm = {
        email: document.getElementById("confirm-email"),
        password: document.getElementById("confirm-password"),
        nickname: document.getElementById("confirm-nickname"),
    };

    const final = document.getElementById("final-username");

    let saved = { email: "", password: "", nickname: "" };

    const showStep = (name) => {
        Object.values(steps).forEach(div => div.style.display = "none");
        steps[name].style.display = "block";
    };

    const enableButton = (btn) => {
        btn.disabled = false;
        btn.classList.add("active");
    };

    const disableButton = (btn) => {
        btn.disabled = true;
        btn.classList.remove("active");
    };

    const showError = (el, input) => {
        el.classList.add("visible");
        input.classList.add("error-border");
    };

    const hideError = (el, input) => {
        el.classList.remove("visible");
        input.classList.remove("error-border");
    };

    const validateEmail = (email) => /^[^\s@"\\;%=,()<>+\-*/]+@[^\s@]+\.[^\s@]+$/.test(email);
    const validatePassword = (pw) => /^(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/.test(pw) && !/['"\\;%=,()<>+\-*/]/.test(pw);
    const validateNickname = (name) => name.length > 0 && name.length <= 6;

    // 이메일 입력 이벤트
    inputs.email.addEventListener("input", () => {
        const value = inputs.email.value.trim();
        if (validateEmail(value)) enableButton(buttons.email);
        else disableButton(buttons.email);
    });

    buttons.email.addEventListener("click", () => {
        const val = inputs.email.value.trim();
        if (!validateEmail(val)) {
            showError(errors.email, inputs.email);
            return;
        }
        hideError(errors.email, inputs.email);
        saved.email = val;
        previews.email.textContent = val;
        showStep("password");
    });

    // 비밀번호 입력 이벤트
    inputs.password.addEventListener("input", () => {
        const value = inputs.password.value;
        if (validatePassword(value)) enableButton(buttons.password);
        else disableButton(buttons.password);
    });

    buttons.password.addEventListener("click", () => {
        const val = inputs.password.value;
        if (!validatePassword(val)) {
            showError(errors.password, inputs.password);
            return;
        }
        hideError(errors.password, inputs.password);
        saved.password = val;
        previews.emailPassword.textContent = `${saved.email}\n${"•".repeat(val.length)}`;
        showStep("nickname");
    });

    // 닉네임 입력 이벤트
    inputs.nickname.addEventListener("input", () => {
        const value = inputs.nickname.value.trim();
        if (validateNickname(value)) enableButton(buttons.nickname);
        else disableButton(buttons.nickname);
    });

    buttons.nickname.addEventListener("click", () => {
        const val = inputs.nickname.value.trim();
        if (!validateNickname(val)) {
            showError(errors.nickname, inputs.nickname);
            return;
        }
        hideError(errors.nickname, inputs.nickname);
        saved.nickname = val;
        confirm.email.textContent = saved.email;
        confirm.password.textContent = "•".repeat(saved.password.length);
        confirm.nickname.textContent = saved.nickname;
        showStep("confirm");
    });

    // 최종 확인 → 완료
    buttons.confirm.addEventListener("click", () => {
        fetch("https://서버주소/api/auth/signup", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: saved.email,
                password: saved.password,
                nickname: saved.nickname
            })
        })
            .then(res => {
                if (!res.ok) throw new Error("회원가입 실패");
                return res.json();
            })
            .then(() => {
                alert("회원가입이 완료되었습니다!");
                location.href = "login1.html"; // ✅ 자동 로그인
            })
            .catch(err => {
                alert("회원가입 중 오류가 발생했습니다: " + err.message);
            });
    });
});
