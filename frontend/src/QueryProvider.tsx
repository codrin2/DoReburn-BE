import { MutationCache, QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { PropsWithChildren, useMemo } from 'react';

import useToast from './hooks/useToast';

const QueryProvider = ({ children }: PropsWithChildren) => {
  const { toast } = useToast();

  const queryClient = useMemo(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: { throwOnError: true },
        },
        mutationCache: new MutationCache({
          onError: (error) => {
            toast({ message: error.message });
          },
        }),
      }),
    [toast],
  );

  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
};

export default QueryProvider;
