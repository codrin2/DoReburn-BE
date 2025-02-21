self.addEventListener('push', async (event) => {
  const data = event.data?.json();

  const title = data.notification?.title || 'Push Message';
  const body = data.notification?.body || 'This is a push notification';
  const url = data.data?.url || '/';

  const options = {
    body,
    icon: '/icons/192x192.png',
    badge: '/icons/192x192.png',
    image: '/icons/192x192.png',
    actions: data.notification?.actions || [],
    data: { url },
  };

  event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();

  const url = event.notification.data?.url || '/';

  event.waitUntil(
    clients.matchAll({ type: "window" }).then((clientList) => {
      for (const client of clientList) {
        if (client.url === url && "focus" in client) {
          return client.focus();
        }
      }
      if (clients.openWindow) {
        return clients.openWindow(url);
      }
    })
  );
});