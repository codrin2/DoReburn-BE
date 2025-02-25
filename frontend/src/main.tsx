import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
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

  // const { worker } = await import('./mocks/browser');

  return await worker.start({ onUnhandledRequest: 'bypass' });
};

const registerServiceWorker = () => {
  if (!('serviceWorker' in navigator && 'PushManager' in window)) {
    return;
  }

  navigator.serviceWorker.register('./service-worker.js', { type: 'module' });
};

registerServiceWorker();
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
            <ReactQueryDevtools initialIsOpen={false} />
          </QueryProvider>
        </ToastProvider>
      </RootErrorBoundary>
    </ThemeProvider>,
  );
});
