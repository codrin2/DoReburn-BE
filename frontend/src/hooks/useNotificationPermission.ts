import { useEffect, useState } from 'react';

import { subscribePushNotification } from '@/api/notification';
import { isPushSupported } from '@/config/settingFCM';

const registerServiceWorker = async () => {
  if (!('serviceWorker' in navigator)) {
    return;
  }

  await navigator.serviceWorker.register('/firebase-messaging-sw.js');
};

const useNotificationPermission = (openModal: () => void) => {
  const [permission, setPermission] = useState<NotificationPermission>(
    typeof Notification !== 'undefined' ? Notification.permission : 'denied',
  );

  const requestPermission = async () => {
    if (isPushSupported() && permission === 'default') {
      const status = await Notification.requestPermission();
      await registerServiceWorker();

      setPermission(status);

      if (status === 'granted') {
        await subscribePushNotification();
      }
    }
  };

  useEffect(() => {
    if (typeof Notification === 'undefined') {
      return;
    }

    if (permission === 'default') {
      openModal();
    }
  }, [permission, openModal]);

  return { permission, requestPermission, setPermission };
};

export default useNotificationPermission;
