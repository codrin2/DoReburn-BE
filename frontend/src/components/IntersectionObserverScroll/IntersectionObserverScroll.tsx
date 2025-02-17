import { Fragment, PropsWithChildren, useEffect, useRef } from 'react';

interface IntersectionObserverScrollProps {
  onReachBottom: () => void;
  hasNextPage: boolean;
  threshold?: number;
}

const IntersectionObserverScroll = ({
  children,
  onReachBottom,
  hasNextPage,
  threshold = 0.1,
}: PropsWithChildren<IntersectionObserverScrollProps>) => {
  const observerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!observerRef.current) return;

    const observerElement = observerRef.current;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && hasNextPage) {
          onReachBottom();
        }
      },
      {
        threshold,
      },
    );

    observer.observe(observerRef.current);

    return () => {
      if (observerElement) observer.unobserve(observerElement);
    };
  }, [hasNextPage, onReachBottom, threshold, observerRef]);

  return (
    <Fragment>
      {children}
      <div
        ref={observerRef}
        style={{
          height: '1px',
          background: 'transparent',
        }}
      />
    </Fragment>
  );
};

export default IntersectionObserverScroll;
