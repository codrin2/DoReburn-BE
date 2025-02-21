import { useEffect, useState } from 'react';

import { subscribePushNotification } from '@/api/notification';

const useNotificationPermission = () => {
  const [permission, setPermission] = useState<NotificationPermission>(Notification.permission);

  useEffect(() => {
    if (permission === 'default') {
      alert('이동 시간이 끝나기 전에 알림으로 리마인드를 받아보세요! 🚀\n 놓치지 않게 도와드려요.');

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
  }, [permission]);

  return permission;
};

export default useNotificationPermission;
