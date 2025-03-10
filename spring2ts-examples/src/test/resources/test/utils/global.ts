import { Base64 } from 'js-base64';

// SSO 登录地址
const SSO_DEFAULT_LOGIN_URL = 'https://localhost/login/sso';
// 项目二级路径
const PROJECT_BASE_PATH = import.meta.env.VITE_BASE_API_URL as string;

/**
 * 全局配置对象，包含 SSO 登录配置、无权限路径、token 缓存等信息
 */
const globalConfig: IGlobalConfig = {
  /**
   * [必填配置] 登录是否启用
   * 可选值为 true 或 false，决定是否启用登录功能
   */
  loginEnabled: true,

  /**
   * [必填配置] 登录方式
   * 可选值为 'SSO' 或 'password'，表示使用单点登录 (SSO) 或密码登录
   */
  loginType: 'password',

  /**
   * [必填配置] 启用 SSO 登录时，获取 SSO 登录 URL
   * 生成 SSO 登录页面的 URL，根据当前环境生成跳转链接
   *
   * @returns {string} 返回 SSO 登录地址
   */
  getSSOLoginUrl: (callbackUrl = window.location.href): string => {
    // 服务端 SSO 登录地址
    const loginAPI = 'https://localhost/api/sso';

    /**
     * 方式一：由前端跳转到 SSO 地址
     * 生成前端跳转的 SSO 登录地址，包含回调地址（Base64 编码后的当前页面 URL）
     */
    const url = `${SSO_DEFAULT_LOGIN_URL}?service=${loginAPI}?callback=${Base64.encode(callbackUrl)}`;

    // 方式二：由后端跳转到 SSO 地址
    // const url = `${loginAPI}?callback=${callbackUrl}`;

    return url;
  },

  /**
   * [必填配置] 启用 SSO 登录时，获取 SSO 登出 URL
   * 生成 SSO 登出地址，包含回调地址，用户登出后会跳转回当前页面
   *
   * @returns {string} 返回 SSO 登出地址
   */
  getSSOLogoutUrl: (): string => {
    // 服务端 SSO 登出地址
    const logoutAPI = 'https://localhost/api/logout';

    /**
     * 返回登出地址，包含回调参数（登出后返回当前页面）
     */
    return `${logoutAPI}?callback=${window.location.href}`;
  },

  /**
   * [可选配置] token 缓存的 key
   * 通过 Base64 编码项目路径生成的 token 缓存 key，确保不同项目有不同的 token 缓存
   */
  tokenCacheKey: `token-${Base64.encode(
    /^\//.test(PROJECT_BASE_PATH) ? PROJECT_BASE_PATH : 'basic',
  )}`,

  /**
   * [可选配置] 无需授权的路径列表
   * 该列表中的路径不会进行授权验证，适用于公共页面，如登录页等
   */
  noAuthPaths: ['/login'],
};

export default globalConfig;
