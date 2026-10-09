/* ===========================================================
 * 认证状态管理：token / 用户信息存取
 * =========================================================== */
window.Auth = (function () {
  'use strict';

  function token() { return localStorage.getItem('la_token'); }

  function user() {
    try { return JSON.parse(localStorage.getItem('la_user') || 'null'); }
    catch (e) { return null; }
  }

  function save(t, u) {
    localStorage.setItem('la_token', t);
    localStorage.setItem('la_user', JSON.stringify(u));
  }

  function logout() {
    localStorage.removeItem('la_token');
    localStorage.removeItem('la_user');
    location.href = 'login.html';
  }

  function requireLogin() {
    if (!token()) { location.href = 'login.html'; return false; }
    return true;
  }

  return { token: token, user: user, save: save, logout: logout, requireLogin: requireLogin };
})();
