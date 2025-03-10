import globalConfig from './global';

/**
 * 获取 SSO 登录的开启状态
 * @returns {boolean} 是否开启了 SSO 登录
 */
export const getSSOEnabled = () =>
  globalConfig.loginEnabled && globalConfig.loginType === 'SSO';

/**
 * 跳转到 SSO 进行登录
 * @returns {void}
 * @example
 */
export const redirectToSSOLogin = (
  callbackUrl = window.location.href,
): void => {
  const url = globalConfig.getSSOLoginUrl(callbackUrl);

  // 安全校验，确保返回的 SSO 登录地址为字符串类型
  if (typeof url !== 'string') {
    throw new Error(
      'SSO 登录地址返回异常，请检查 /config/global 中的 SSO 配置',
    );
  }

  // 重定向到 SSO 登录页面
  window.location.href = url;
};

/**
 * 跳转到 SSO 进行登出
 * @returns {void}
 * @example
 */
export const redirectToSSOLogout = (): void => {
  const url = globalConfig.getSSOLogoutUrl();
  // 安全校验，确保返回的 SSO 登出地址为字符串类型
  if (typeof url !== 'string') {
    throw new Error(
      'SSO 登出地址返回异常，请检查 /config/global 中的 SSO 配置',
    );
  }

  // 移除本地存储的 Token
  localStorage.removeItem(globalConfig.tokenCacheKey);

  // 进行退出登录
  window.location.href = url;
};

/**
 * 拼装后端解析 sso ticket 的地址
 * @returns
 */
const getCheckSsoUrl = () => {
  // 当前前端地址，用于 sso 登录和解析完成后，重定向回来（服务端需要进行 base64 解码）
  const currentUrl = Base64.encode(window.location.href);

  // dev 环境使用 test 环境的域名做 sso（因为 mock 环境，没法做 sso 解析）
  let domain =
    import.meta.env.VITE_ENV === 'dev'
      ? 'http://localhost/api'
      : import.meta.env.VITE_API_DOMAIN;

  // 防止 API_DOMAIN 配置时，使用 // 开头
  if (!/^(http|https)/.test(domain)) {
    domain = window.location.protocol + domain;
  }

  return `${domain}/api/sso/login/cookie?sso_redirect=${currentUrl}`;
};

// 拼装 SSO 登录页面地址
const getSsoLoginUrl = () => {
  const homePage = '';
  console.log('env:', import.meta.env.VITE_ENV );
  if (import.meta.env.VITE_ENV === 'dev') {
    return `http://test.com/console?backurl=${homePage}`;
  } else if (import.meta.env.VITE_ENV === 'test') {
    return `http://test.com/console?backurl=${homePage}`;
  } else {
    return `http://product.com/console?backurl=${homePage}`;
  }
};

/**
 * 跳往服务端提供的 sso 校验地址登录（前端请求接口，返回鉴权失败时调用）
 */
const redirectSsoLogin = () => {
   window.location.href = getSsoLoginUrl();
};

/**
 * 重定向到登出页面（用户点击退出登录时调用）
 */
const redirectSsoLogout = () => {
  window.location.href = `https://localhost/sso/logout?service=${encodeURIComponent(
    getSsoLoginUrl(),
  )}`;
};

export { redirectSsoLogin, redirectSsoLogout };
