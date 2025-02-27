import { getToken } from 'firebase/messaging';

import fetchClient from './fetchClient';

import { messaging } from '@/config/settingFCM';
import { API_URL } from '@/constants/url';

export const subscribePushNotification = async () => {
  if (!('serviceWorker' in navigator)) {
    return;
  }

  await Notification.requestPermission();

  try {
    const deviceToken = await getToken(messaging, {
      vapidKey: import.meta.env.VITE_PUSH_NOTIFICATION_PUBLIC_KEY,
    });

    if (!deviceToken) {
      return;
    }

    return fetchClient.post(API_URL.notificationFCM, {
      body: {
        deviceToken,
      },
    });
  } catch (error) {
    console.error('FCM 토큰 요청 중 오류 발생:', error);
  }
};

interface SendNotificationProps {
  memberId: number;
  planId: number;
  title: string;
  body: string;
}

export const sendNotification = ({ memberId, planId, title, body }: SendNotificationProps) => {
  return fetchClient.post(API_URL.notification, {
    body: { memberId, planId, title, body },
  });
};
