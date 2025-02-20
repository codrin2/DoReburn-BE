import { PropsWithChildren, useEffect } from 'react';

const Viewport = ({ children }: PropsWithChildren) => {
  useEffect(() => {
    const updateHeight = () => {
      document.documentElement.style.setProperty('--vh', `${window.innerHeight}px`);
    };

    updateHeight();

    window.addEventListener('resize', updateHeight);

    return () => window.removeEventListener('resize', updateHeight);
  }, []);

  return <>{children}</>;
};

export default Viewport;
