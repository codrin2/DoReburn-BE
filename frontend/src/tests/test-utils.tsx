import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { ThemeProvider } from 'styled-components';

import theme from '../styles/theme';

import CustomSuspense from '@/components/CustomSuspense/CustomSuspense';
import RootErrorBoundary from '@/components/ErrorBoundary/RootErrorBoundary';
import ToastProvider from '@/components/Toast/ToastProvider';
import Viewport from '@/components/Viewport/Viewport';
import { OverlayProvider } from '@/providers/OverlayProvider';
import GlobalStyle from '@/styles/GlobalStyle';

import QueryProvider from '@/QueryProvider';

const wrapper = ({ children }: { children: React.ReactNode }) => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <RootErrorBoundary>
        <ToastProvider>
          <QueryProvider>
            <OverlayProvider>
              <Viewport>
                <CustomSuspense>
                  <MemoryRouter initialEntries={['/']}>{children}</MemoryRouter>
                </CustomSuspense>
              </Viewport>
            </OverlayProvider>
          </QueryProvider>
        </ToastProvider>
      </RootErrorBoundary>
    </ThemeProvider>
  );
};

export const customRender = (ui: React.ReactElement, options = {}) =>
  render(ui, { wrapper, ...options });
