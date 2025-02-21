import { useEffect, useState } from 'react';

import { subscribePushNotification } from '@/api/notification';

const useNotificationPermission = () => {
  const [permission, setPermission] = useState<NotificationPermission>(Notification.permission);

  useEffect(() => {
    if (permission === 'default') {
      alert('이동 시간이 끝나기 전에 알림으로 리마인드를 받아보세요! 🚀\n 놓치지 않게 도와드려요.');

      if ('Notification' in window || typeof Notification !== 'undefined') {
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
