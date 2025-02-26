import { getToken } from 'firebase/messaging';

import fetchClient from './fetchClient';

import { messaging } from '@/config/settingFCM';
import { API_URL } from '@/constants/url';

// interface PushSubscription {
//   endpoint: string;
//   keys: {
//     p256dh: string;
//     auth: string;
//   };
// }

// export const subscribePushNotification = async () => {
//   if (!('serviceWorker' in navigator && 'PushManager' in window)) {
//     return;
//   }
//   const registration = await navigator.serviceWorker.ready;

//   const subscription = await registration.pushManager.subscribe({
//     userVisibleOnly: true,
//     applicationServerKey: import.meta.env.VITE_PUSH_NOTIFICATION_PUBLIC_KEY,
//   });

//   const pushSubscription: PushSubscription = {
//     endpoint: subscription.endpoint,
//     keys: {
//       p256dh: btoa(String.fromCharCode(...new Uint8Array(subscription.getKey('p256dh')!))),
//       auth: btoa(String.fromCharCode(...new Uint8Array(subscription.getKey('auth')!))),
//     },
//   };

//   return fetchClient.post(API_URL.notificationSubscribe, {
//     body: {
//       endpoint: pushSubscription.endpoint,
//       keys: pushSubscription.keys,
//     },
//   });
// };

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
      console.log('토큰을 가져오지 못했습니다. 권한을 다시 요청하세요.');

      return;
    }

    console.log('FCM 토큰:', deviceToken);

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
