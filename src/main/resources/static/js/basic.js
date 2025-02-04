$(document).ready(function () {
    const auth = getToken();
    if(auth === '') {
        $('#login-true').hide();
        $('#login-false').show();
    } else {
        $('#login-true').show();
        $('#login-false').hide();
    }
});

let host = 'http://' + window.location.host;

function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', { path: '/' });
    window.location.href = host + "/api/member/login-page";
}

function getToken() {
    let auth = Cookies.get('auth');

    if(auth === undefined) {
        return '';
    }

    return auth;
}