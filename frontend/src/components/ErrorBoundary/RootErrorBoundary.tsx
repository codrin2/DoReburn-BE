import { useQueryErrorResetBoundary } from '@tanstack/react-query';
import { PropsWithChildren } from 'react';
import { ErrorBoundary } from 'react-error-boundary';

import ErrorPage from '@/pages/ErrorPage';

const RootErrorBoundary = ({ children }: PropsWithChildren) => {
  const { reset } = useQueryErrorResetBoundary();

  return (
    <ErrorBoundary
      onReset={reset}
      FallbackComponent={({ resetErrorBoundary }) => <ErrorPage onClick={resetErrorBoundary} />}
    >
      {children}
    </ErrorBoundary>
  );
};

export default RootErrorBoundary;
