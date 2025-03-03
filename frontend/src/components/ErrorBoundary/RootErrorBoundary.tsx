import { useQueryErrorResetBoundary } from '@tanstack/react-query';
import { PropsWithChildren } from 'react';
import { ErrorBoundary } from 'react-error-boundary';

import { CustomError } from '@/api/error';
import ErrorPage from '@/pages/ErrorPage';

const RootErrorBoundary = ({ children }: PropsWithChildren) => {
  const { reset } = useQueryErrorResetBoundary();

  return (
    <ErrorBoundary
      onReset={reset}
      FallbackComponent={({ error, resetErrorBoundary }) => {
        return (
          <ErrorPage
            message={error instanceof CustomError ? error.message : ''}
            onClick={resetErrorBoundary}
          />
        );
      }}
    >
      {children}
    </ErrorBoundary>
  );
};

export default RootErrorBoundary;
