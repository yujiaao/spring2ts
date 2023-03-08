import { request } from 'umi';
import { redirectSsoLogin } from './sso';
import type { RequestOptionsInit } from 'umi-request';

interface myRequestOptionsInit extends RequestOptionsInit {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
}

const myRequest = async <T = any>(
  url,
  options: myRequestOptionsInit,
): Promise<API.RequestResult<T>> => {
  let newOptions: RequestOptionsInit = { ...options };

  const { method, data = {} } = options;
  if (method === 'GET') {
    newOptions = {
      ...options,
      params: data,
    };
    delete newOptions.data;
  }
  try {
    const response = await request(url, newOptions);

    if (response) {
      const { returncode, message, result } = response;
      //  returncode 401 重定向到 sso 登录

      if (returncode === 401) {
        redirectSsoLogin();
      }

      // 402 没有权限的情况
      if (returncode === 402) {
        message.error('没有权限, 请联系管理人员')
        throw new Error('没有权限, 请联系管理人员');
      }

      return {
        success: returncode === 0,
        returncode,
        message,
        data: result,
      };
    } else {
      throw new Error('网络异常');
    }
  } catch (error) {
    return { success: false, message: (error as Error).message };
  }
};

export default myRequest;



export const get = async <T = any>(
  url: any,
  data?: any,
  callback?: any
): Promise<API.RequestResult<T>> => {
  try {
    const response = await request(url, {
      method: 'GET',
      params: data
    });
    if (response) {
      const { returncode, message, result } = response;
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
      }

      if (callback) {
        callback(returnData)
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
  callback?: any
): Promise<API.RequestResult<T>> => {
  try {
    const response = await request(url, {
      method: 'POST',
      data: data,
      requestType: 'json'
    });
    if (response) {
      const { returncode, message, result } = response;
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
      }

      if (callback) {
        callback(returnData)
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
  callback?: any
): Promise<API.RequestResult<T>> => {
  try {
    const response = await request(url, {
      method: 'POST',
      data: data,
      requestType: 'form'
    });
    if (response) {
      const { returncode, message, result } = response;
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
      }

      if (callback) {
        callback(returnData)
      }
      return returnData;
    } else {
      throw new Error('网络异常');
    }
  } catch (error) {
    return { success: false, message: (error as Error).message };
  }
}


export const postFile = async <T = any>(
  url: any,
  data: any,
  callback?: any
): Promise<API.RequestResult<T>> => {
  try {
    const response = await request(url, {
      method: 'POST',
      data: data,
      contentType: "multipart/form-data",
    });
    if (response) {
      const { returncode, message, result } = response;
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
      }

      if (callback) {
        callback(returnData)
      }
      return returnData;
    } else {
      throw new Error('网络异常');
    }
  } catch (error) {
    return { success: false, message: (error as Error).message };
  }
}
