import { MutationCache, QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { PropsWithChildren, useMemo } from 'react';

import {
  CustomError,
  NETWORK_ERROR_STATUS,
  SERVER_ERROR_STATUS,
  UNHANDLED_ERROR_STATUS,
} from './api/error';
import useToast from './hooks/useToast';

const isServerError = (status: number) =>
  status >= SERVER_ERROR_STATUS &&
  status !== NETWORK_ERROR_STATUS &&
  status !== UNHANDLED_ERROR_STATUS;

const QueryProvider = ({ children }: PropsWithChildren) => {
  const { toast } = useToast();

  const queryClient = useMemo(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: { throwOnError: true },
          mutations: {
            throwOnError: (err) => {
              const error = err as CustomError;

              return isServerError(error.status);
            },
            networkMode: 'always',
          },
        },

        mutationCache: new MutationCache({
          onError: (error, _variables, _context, mutation) => {
            if (!mutation.options.onError) {
              toast({ message: error.message });
            }
          },
        }),
      }),
    [toast],
  );

  return (
    <QueryClientProvider client={queryClient}>
      {children}
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
};

export default QueryProvider;
