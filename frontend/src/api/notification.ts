import { getToken } from 'firebase/messaging';

import fetchClient from './fetchClient';

import { getFirebaseMessaging } from '@/config/settingFCM';
import { API_URL } from '@/constants/url';

interface SendNotificationProps {
  memberId: number;
  planId: number;
  title: string;
  body: string;
}

export const subscribePushNotification = async () => {
  const messaging = await getFirebaseMessaging();

  if (!messaging) {
    return;
  }

  const deviceToken = await getToken(messaging, {
    vapidKey: import.meta.env.VITE_PUSH_NOTIFICATION_PUBLIC_KEY,
  });

  return await fetchClient.post(API_URL.notificationFCM, {
    body: {
      deviceToken,
    },
  });
};

export const sendNotification = async ({
  memberId,
  planId,
  title,
  body,
}: SendNotificationProps) => {
  return await fetchClient.post(API_URL.notification, {
    body: { memberId, planId, title, body },
  });
};
