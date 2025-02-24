import fetchClient from './fetchClient';

import { API_URL } from '@/constants/url';

interface PushSubscription {
  endpoint: string;
  keys: {
    p256dh: string;
    auth: string;
  };
}

export const subscribePushNotification = async () => {
  if (!('serviceWorker' in navigator && 'PushManager' in window)) {
    return;
  }
  const registration = await navigator.serviceWorker.ready;

  const subscription = await registration.pushManager.subscribe({
    userVisibleOnly: true,
    applicationServerKey: import.meta.env.VITE_PUSH_NOTIFICATION_PUBLIC_KEY,
  });

  const pushSubscription: PushSubscription = {
    endpoint: subscription.endpoint,
    keys: {
      p256dh: btoa(String.fromCharCode(...new Uint8Array(subscription.getKey('p256dh')!))),
      auth: btoa(String.fromCharCode(...new Uint8Array(subscription.getKey('auth')!))),
    },
  };

  return fetchClient.post(API_URL.notificationSubscribe, {
    body: {
      endpoint: pushSubscription.endpoint,
      keys: pushSubscription.keys,
    },
  });
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
