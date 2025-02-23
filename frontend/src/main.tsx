import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router';
import { ThemeProvider } from 'styled-components';

import ToastProvider from './components/Toast/ToastProvider';
import Viewport from './components/Viewport/Viewport';
import { OverlayProvider } from './providers/OverlayProvider';
import { router } from './router';
import GlobalStyle from './styles/GlobalStyle.js';
import theme from './styles/theme';

const enableMocking = async () => {
  if (process.env.NODE_ENV !== 'development') {
    return;
  }

  const { worker } = await import('./mocks/browser');

  // return await worker.start({ onUnhandledRequest: 'bypass' });
};

const registerServiceWorker = () => {
  if (!('serviceWorker' in navigator && 'PushManager' in window)) {
    return;
  }

  navigator.serviceWorker.register('/service-worker.js');
};

const queryClient = new QueryClient();

registerServiceWorker();
enableMocking().then(() => {
  createRoot(document.getElementById('root')!).render(
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <GlobalStyle />
        <ToastProvider>
          <OverlayProvider>
            <Viewport>
              <RouterProvider router={router} />
            </Viewport>
          </OverlayProvider>
        </ToastProvider>
        <ReactQueryDevtools initialIsOpen={false} />
      </ThemeProvider>
    </QueryClientProvider>,
  );
});
