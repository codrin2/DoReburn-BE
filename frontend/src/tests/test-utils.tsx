import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { ThemeProvider } from 'styled-components';

import theme from '../styles/theme';

import ToastProvider from '@/components/Toast/ToastProvider';
import { OverlayProvider } from '@/providers/OverlayProvider';
import GlobalStyle from '@/styles/GlobalStyle';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 0,
    },
  },
});

const wrapper = ({ children }: { children: React.ReactNode }) => {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <GlobalStyle />
        <ToastProvider>
          <OverlayProvider>
            <MemoryRouter initialEntries={['/']}>{children}</MemoryRouter>
          </OverlayProvider>
        </ToastProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
};

export const customRender = (ui: React.ReactElement, options = {}) =>
  render(ui, { wrapper, ...options });
