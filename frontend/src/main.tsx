import { getToken } from 'firebase/messaging';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router';
import { ThemeProvider } from 'styled-components';

import CustomSuspense from './components/CustomSuspense/CustomSuspense';
import RootErrorBoundary from './components/ErrorBoundary/RootErrorBoundary';
import ToastProvider from './components/Toast/ToastProvider';
import Viewport from './components/Viewport/Viewport';
import { messaging } from './config/settingFCM';
import { OverlayProvider } from './providers/OverlayProvider';
import QueryProvider from './QueryProvider';
import { router } from './router';
import GlobalStyle from './styles/GlobalStyle.js';
import theme from './styles/theme';

const enableMocking = async () => {
  if (process.env.NODE_ENV !== 'development') {
    return;
  }

  // const { worker } = await import('./mocks/browser');

  // return await worker.start({ onUnhandledRequest: 'bypass' });
};

const registerServiceWorker = async () => {
  if (!('serviceWorker' in navigator)) {
    return;
  }

  await navigator.serviceWorker.register('/firebase-messaging-sw.js');
};

async function handleAllowNotification() {
  await Notification.requestPermission();
  registerServiceWorker();
  await getDeviceToken();
}

async function getDeviceToken() {
  await getToken(messaging, {
    vapidKey: import.meta.env.VITE_PUSH_NOTIFICATION_PUBLIC_KEY,
  })
    .then((currentToken) => {
      if (currentToken) {
        console.log('토큰: ', currentToken);
      } else {
        console.log('토큰을 가져오지 못했습니다. 권한을 다시 요청하세요.');
      }
    })
    .catch((err) => {
      console.error('토큰을 가져오는 중 에러가 발생했습니다.');
    });
}

// registerServiceWorker();
handleAllowNotification();
enableMocking().then(() => {
  createRoot(document.getElementById('root')!).render(
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <RootErrorBoundary>
        <ToastProvider>
          <QueryProvider>
            <OverlayProvider>
              <Viewport>
                <CustomSuspense>
                  <RouterProvider router={router} />
                </CustomSuspense>
              </Viewport>
            </OverlayProvider>
          </QueryProvider>
        </ToastProvider>
        {/* <ReactQueryDevtools initialIsOpen={false} /> */}
      </RootErrorBoundary>
    </ThemeProvider>,
  );
});
