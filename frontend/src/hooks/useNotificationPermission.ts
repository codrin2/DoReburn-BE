import { useEffect, useState } from 'react';

import { subscribePushNotification } from '@/api/notification';

const useNotificationPermission = (openModal: () => void) => {
  const [permission, setPermission] = useState<NotificationPermission>(
    typeof Notification !== 'undefined' ? Notification.permission : 'denied',
  );

  useEffect(() => {
    if (typeof Notification === 'undefined') {
      return;
    }

    if (permission === 'default') {
      openModal();
    }
  }, [permission, openModal]);

  const requestPermission = () => {
    if (typeof Notification !== 'undefined' && permission === 'default') {
      const isPWA = window.matchMedia('(display-mode: standalone)').matches;
      const isIOS = /iPad|iPhone|iPod/.test(navigator.userAgent);

      if ('Notification' in window && (!isIOS || isPWA)) {
        Notification.requestPermission().then((newPermission) => {
          setPermission(newPermission);

          if (newPermission === 'granted') {
            subscribePushNotification();
          }
        });
      }
    }
  };

  return { permission, requestPermission };
};

export default useNotificationPermission;
