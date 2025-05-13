document.addEventListener("DOMContentLoaded", function () {
    const sendBtn = document.getElementById("send-code-btn");
    if (sendBtn) {
        sendBtn.addEventListener("click", sendVerificationCode);
    }

    const verifyBtn = document.getElementById("verify-code-btn");
    if (verifyBtn) {
        verifyBtn.addEventListener("click", verifyEmailCode);
    }
});

function sendVerificationCode() {
    const email = document.getElementById("email").value;
    const univName = document.getElementById("univName").value;

    if (!email || !univName) {
        alert("이메일과 학교명을 모두 입력해주세요.");
        return;
    }

    fetch(`/send-verification-code?email=${encodeURIComponent(email)}&univName=${encodeURIComponent(univName)}`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("인증 코드가 이메일로 발송되었습니다.");
            } else {
                alert("실패: " + data.message);
            }
        })
        .catch(error => {
            console.error("인증 요청 중 오류 발생:", error);
            alert("인증 요청 중 오류가 발생했습니다.");
        });
}

function verifyEmailCode() {
    const email = document.getElementById("email").value;
    const univName = document.getElementById("univName").value;
    const verificationCode = document.getElementById("verification-code").value;

    if (!email || !univName || !verificationCode) {
        alert("모든 항목을 입력해주세요.");
        return;
    }

    fetch(`/verify-email-code?email=${encodeURIComponent(email)}&univName=${encodeURIComponent(univName)}&verificationCode=${encodeURIComponent(verificationCode)}`)
        .then(response => response.json())
        .then(data => {
            const resultDiv = document.getElementById("verification-result");
            if (data.success) {
                resultDiv.style.color = "green";
                resultDiv.textContent = "인증이 완료되었습니다!";
                document.getElementById("verified").value = "true"; // hidden input 수정
            } else {
                resultDiv.style.color = "red";
                resultDiv.textContent = "인증에 실패했습니다. 코드를 다시 확인해주세요.";
                document.getElementById("verified").value = "false";
            }
        })
        .catch(error => {
            console.error("코드 검증 오류:", error);
            alert("코드 검증 중 오류가 발생했습니다.");
        });
}
