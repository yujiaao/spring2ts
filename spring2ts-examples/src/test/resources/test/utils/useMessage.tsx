import { useEffect } from 'react';
import { message as antMessage } from 'antd';
import { JointContent } from 'antd/es/message/interface';

// 消息事件名称
export const MESSAGE_EVENT_NAME = 'pivot_message';

// 消息类型枚举
export enum MESSAGE_TYPES {
  SUCCESS = 'success',
  ERROR = 'error',
  INFO = 'info',
  WARNING = 'warning',
  LOADING = 'loading',
}

// 派发消息事件
const dispatch = (
  type: MESSAGE_TYPES,
  content: JointContent,
  duration?: number | VoidFunction,
  onClose?: VoidFunction,
) => {
  window.dispatchEvent(
    new CustomEvent(MESSAGE_EVENT_NAME, {
      detail: {
        params: {
          content,
          duration,
          onClose,
        },
        type,
      },
    }),
  );
};

// 定义具体的消息类型方法
export const message = {
  success(
    content: JointContent,
    duration?: number | VoidFunction,
    onClose?: VoidFunction,
  ) {
    dispatch(MESSAGE_TYPES.SUCCESS, content, duration, onClose);
  },
  error(
    content: JointContent,
    duration?: number | VoidFunction,
    onClose?: VoidFunction,
  ) {
    dispatch(MESSAGE_TYPES.ERROR, content, duration, onClose);
  },
  info(
    content: JointContent,
    duration?: number | VoidFunction,
    onClose?: VoidFunction,
  ) {
    dispatch(MESSAGE_TYPES.INFO, content, duration, onClose);
  },
  warning(
    content: JointContent,
    duration?: number | VoidFunction,
    onClose?: VoidFunction,
  ) {
    dispatch(MESSAGE_TYPES.WARNING, content, duration, onClose);
  },
  loading(
    content: JointContent,
    duration?: number | VoidFunction,
    onClose?: VoidFunction,
  ) {
    dispatch(MESSAGE_TYPES.LOADING, content, duration, onClose);
  },
};

// 自定义 Hook，用于绑定消息事件
export const useMessageEvent = () => {
  const [api, contextHolder] = antMessage.useMessage();

  useEffect(() => {
    const bindEvent = (e: CustomEvent | any) => {
      if (e?.detail && e?.detail.params) {
        const func = e?.detail?.type || 'info';
        const { content, duration, onClose } = e.detail.params;
        if (func in api) {
          (api as any)[func](content, duration, onClose);
        }
      }
    };

    window.addEventListener(MESSAGE_EVENT_NAME, bindEvent);

    return () => {
      window.removeEventListener(MESSAGE_EVENT_NAME, bindEvent);
    };
  }, [api]);

  return contextHolder;
};
