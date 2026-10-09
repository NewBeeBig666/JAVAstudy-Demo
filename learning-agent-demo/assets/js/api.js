/* ===========================================================
 * API 客户端：RESTful 封装 + SSE 流式解析
 * 401 自动跳登录页；非 0 code 抛错（调用方 toast）
 * =========================================================== */
window.API = (function () {
  'use strict';
  const BASE = '/api';

  function token() { return localStorage.getItem('la_token') || ''; }

  async function request(method, path, body) {
    const opt = { method: method, headers: { 'Authorization': 'Bearer ' + token() } };
    if (body !== undefined) {
      opt.headers['Content-Type'] = 'application/json;charset=utf-8';
      opt.body = JSON.stringify(body);
    }
    const res = await fetch(BASE + path, opt);
    if (res.status === 401) { window.Auth.logout(); throw new Error('登录已过期，请重新登录'); }
    let data = null;
    try { data = await res.json(); } catch (e) { /* 非 JSON 响应 */ }
    if (!res.ok || !data || data.code !== 0) {
      throw new Error((data && data.msg) || ('请求失败 (' + res.status + ')'));
    }
    return data.data;
  }

  const get = p => request('GET', p);
  const post = (p, b) => request('POST', p, b === undefined ? {} : b);
  const put = (p, b) => request('PUT', p, b === undefined ? {} : b);
  const del = p => request('DELETE', p);

  /**
   * SSE 流式请求（POST + ReadableStream 解析）
   * handlers: { meta, delta, card, replace, done, error }
   */
  async function sse(path, body, handlers) {
    const res = await fetch(BASE + path, {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer ' + token(),
        'Content-Type': 'application/json;charset=utf-8',
        'Accept': 'text/event-stream'
      },
      body: JSON.stringify(body || {})
    });
    if (res.status === 401) { window.Auth.logout(); throw new Error('登录已过期，请重新登录'); }
    if (!res.ok || !res.body) { throw new Error('流式连接失败 (' + res.status + ')'); }

    const reader = res.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let buf = '';
    handlers = handlers || {};

    while (true) {
      const r = await reader.read();
      if (r.done) break;
      buf += decoder.decode(r.value, { stream: true });
      let idx;
      while ((idx = buf.indexOf('\n\n')) >= 0) {
        const chunk = buf.slice(0, idx);
        buf = buf.slice(idx + 2);
        let evt = 'message', data = '';
        chunk.split('\n').forEach(line => {
          if (line.indexOf('event:') === 0) evt = line.slice(6).trim();
          else if (line.indexOf('data:') === 0) data += line.slice(5).trim();
        });
        if (data) {
          let payload = data;
          try { payload = JSON.parse(data); } catch (e) { /* 纯文本 */ }
          const fn = handlers[evt];
          if (fn) fn(payload, evt);
        }
      }
    }
    if (handlers.end) handlers.end();
  }

  return { get: get, post: post, put: put, del: del, sse: sse, token: token };
})();
