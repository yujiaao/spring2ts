// @ts-ignore

import axios, {
  AxiosResponse,
  AxiosInstance,
  CreateAxiosDefaults,
  InternalAxiosRequestConfig,
} from 'axios';
import { message } from './useMessage';
import globalConfig from './global';
import { redirectSsoLogin } from './sso';

/**
 * 之家接口结构规范：响应数据类型
 */
export interface IResponse<T = any> {
  returncode: number;
  result: T;
  message: string;
}

declare module 'axios' {
  export interface AxiosRequestConfig {
    /**
     * 自定义功能配置参数
     */
    customOptions?: {
      /**
       * 是否验证 returncode 状态码
       */
      isVerifyReturncode?: boolean;

      /**
       * 是否验证 SSO 登录态
       */
      isVerifyAuth?: boolean;
    };
  }
}

// 默认的接口基础路径
// @ts-ignore
const BASE_API_URL = import.meta.env.VITE_BASE_API_URL as string;

// 保持单例，存储已创建的 Axios 实例
const requestInstances: Record<string, AxiosInstance> = {};

/**
 * 创建 Axios 实例的函数
 * @param {CreateAxiosDefaults} params - 创建 Axios 实例
 * @returns {AxiosInstance} 创建的 Axios 实例
 */
function createRequest(params: CreateAxiosDefaults) {
  const { baseURL = BASE_API_URL, timeout = 3000, ...otherParams } = params;

  // 去除 baseURL 末尾的斜杠，保证 URL 格式统一
  const normalizedBaseURL = baseURL.replace(/\/+$/, '');

  // 保持单例模式，若已有该 baseURL 对应的实例则直接返回
  if (requestInstances[normalizedBaseURL]) {
    return requestInstances[normalizedBaseURL];
  }

  // 创建新的 Axios 实例
  const instance = axios.create({
    baseURL: normalizedBaseURL,
    timeout, // 请求超时时间
    ...otherParams,
  });

  /**
   * 设置请求拦截器，用于处理每次请求前的操作
   */
  instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      // 设置鉴权 Token 到请求头
      const tokenCacheKey = globalConfig.tokenCacheKey;
      const token = localStorage.getItem(tokenCacheKey);
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error: any) => {
      console.error(error); // 调试用
      return Promise.reject(error);
    },
  );

  /**
   * 设置响应拦截器，用于处理响应后的操作
   */
  instance.interceptors.response.use(
    (response: AxiosResponse) => {
      const res = response.data;
      const customOptions = response?.config?.customOptions ?? {};

      /**
       * 业务层 - 自定义状态码异常验证
       * 当业务自定义 returncode 非 0 时，判断为接口异常
       */
      const { isVerifyReturncode = true } = customOptions;
      if (res && ![0,401,402].includes(res.returncode) && isVerifyReturncode) {
        message.error(
          res.message || `returncode 状态码异常 - ${res.returncode}`,
          5,
        );
        return Promise.reject(new Error(res.message || 'Error'));
      }

      /**
       * HTTP 状态码异常验证
       * 如果返回的 HTTP 状态码不是 200，直接判断为错误
       */
      if (response.status !== 200) {
        message.error(response.statusText || '请求出错', 5);
        return Promise.reject(new Error(response.statusText || 'Error'));
      }

      return response;
    },
    (error: any) => {
      console.error('err' + error); // 调试用
      message.error(error.message, 5);
      return Promise.reject(error);
    },
  );

  // 将新创建的实例缓存并返回，确保同一 baseURL 只创建一个实例
  requestInstances[normalizedBaseURL] = instance;
  return instance;
}

const request = createRequest({
  baseURL: BASE_API_URL,
});

export { createRequest, axios };

export default request;

const delNullValue = (data: any) => {
  for (const k in data) {
    if (data[k] === undefined || data[k] === null || data[k] === '') {
      delete data[k];
    }
  }
};

type RequestType = 'json' | 'form';

// 请求错误处理函数，用来处理响应拦截未捕获的特殊情况
const errorHandler = (error: any) => {
  throw error;
};

const requestConfig = {
  errorHandler, // 在拦截器之后执行
  requestType: 'json' as RequestType,
  crossOrigin: true,
  credentials: 'include' as RequestCredentials,
  timeout: 360000,
};

export const get = async <T = any>(
  url: any,
  data?: any,
  callback?: any,
): Promise<API.RequestResult<T>> => {
  try {
    delNullValue(data);
    const response = await request.get(url, {
      params:data?.data,
      ...requestConfig,
    });
    if (response) {
      const { returncode, message, result } = response.data;
      console.log('response from url: '+url, response);
      //  returncode 401 重定向到 sso 登录
      if (returncode === 401) {
        redirectSsoLogin();
      }
      // 402 没有权限的情况
      if (returncode === 402) {
        throw new Error('没有权限, 请联系管理人员');
      }

      const returnData = {
        success: returncode === 0,
        returncode,
        message,
        data: result,
      };

      if (callback) {
        callback(returnData);
      }
      return returnData;
    } else {
      throw new Error('网络异常');
    }
  } catch (error) {
    return { success: false, message: (error as Error).message };
  }
};

export const postJson = async <T = any>(
  url: string,
  data: any,
  callback?: any,
): Promise<API.RequestResult<T>> => {
  try {
    delNullValue(data);
    const response = await request.post(url, data, {
      ...requestConfig,
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (response) {
      const { returncode, message, result } = response.data;
      //  returncode 401 重定向到 sso 登录
      if (returncode === 401) {
        redirectSsoLogin();
      }
      // 402 没有权限的情况
      if (returncode === 402) {
        throw new Error('没有权限, 请联系管理人员');
      }

      const returnData = {
        success: returncode === 0,
        returncode,
        message,
        data: result,
      };

      if (callback) {
        callback(returnData);
      }
      return returnData;
    } else {
      throw new Error('网络异常');
    }
  } catch (error) {
    return { success: false, message: (error as Error).message };
  }
};

export const post = async <T = any>(
  url: any,
  data: any,
  callback?: any,
): Promise<API.RequestResult<T>> => {
  try {
    delNullValue(data);
    const response = await request.post(url, data, {
      ...requestConfig,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    if (response) {
      const { returncode, message, result } = response.data;
      //  returncode 401 重定向到 sso 登录
      if (returncode === 401) {
        redirectSsoLogin();
      }
      // 402 没有权限的情况
      if (returncode === 402) {
        throw new Error('没有权限, 请联系管理人员');
      }

      const returnData = {
        success: returncode === 0,
        returncode,
        message,
        data: result,
      };

      if (callback) {
        callback(returnData);
      }
      return returnData;
    } else {
      throw new Error('网络异常');
    }
  } catch (error) {
    return { success: false, message: (error as Error).message };
  }
};

export const postFile = async <T = any>(
  url: any,
  data: any,
  callback?: any,
): Promise<API.RequestResult<T>> => {
  try {
    const response = await request.post(url, data, {
      ...requestConfig,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    if (response) {
      const { returncode, message, result } = response.data;
      //  returncode 401 重定向到 sso 登录
      if (returncode === 401) {
        redirectSsoLogin();
      }
      // 402 没有权限的情况
      if (returncode === 402) {
        throw new Error('没有权限, 请联系管理人员');
      }

      const returnData = {
        success: returncode === 0,
        returncode,
        message,
        data: result,
      };

      if (callback) {
        callback(returnData);
      }
      return returnData;
    } else {
      throw new Error('网络异常');
    }
  } catch (error) {
    return { success: false, message: (error as Error).message };
  }
};
