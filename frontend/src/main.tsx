import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router';
import { ThemeProvider } from 'styled-components';

import CustomSuspense from './components/CustomSuspense/CustomSuspense';
import RootErrorBoundary from './components/ErrorBoundary/RootErrorBoundary';
import ToastProvider from './components/Toast/ToastProvider';
import Viewport from './components/Viewport/Viewport';
import { OverlayProvider } from './providers/OverlayProvider';
import QueryProvider from './QueryProvider';
import { router } from './router';
import GlobalStyle from './styles/GlobalStyle.js';
import theme from './styles/theme';

const enableMocking = async () => {
  if (process.env.NODE_ENV !== 'development') {
    return;
  }

  const { worker } = await import('./mocks/browser');

  return await worker.start({ onUnhandledRequest: 'bypass' });
};

const registerServiceWorker = async () => {
  if (!('serviceWorker' in navigator)) {
    return;
  }

  await navigator.serviceWorker.register('/firebase-messaging-sw.js');
};

enableMocking().then(() => {
  registerServiceWorker().then(() => {
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
        </RootErrorBoundary>
      </ThemeProvider>,
    );
  });
});
