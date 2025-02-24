import { PropsWithChildren, Suspense, useEffect, useState } from 'react';

const LOADING_DELAY = 300;

interface CustomSuspenseProps {
  fallback?: React.ReactNode;
}

// loading fallback 을 지연시키는 wrapper 컴포넌트
const CustomSuspense = ({ fallback, children }: PropsWithChildren<CustomSuspenseProps>) => {
  const [isDeferred, setIsDeferred] = useState(false);

  useEffect(() => {
    const timeoutId = setTimeout(() => {
      setIsDeferred(true);
    }, LOADING_DELAY);

    return () => clearTimeout(timeoutId);
  }, []);

  if (!isDeferred) {
    return null;
  }

  return <Suspense fallback={fallback}>{children}</Suspense>;
};

export default CustomSuspense;
