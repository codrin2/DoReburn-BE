import { PropsWithChildren, useEffect, useState } from 'react';

const LOADING_DELAY = 300;

// loading fallback 을 지연시키는 wrapper 컴포넌트
const CustomSuspense = ({ children }: PropsWithChildren) => {
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

  return <>{children}</>;
};

export default CustomSuspense;
