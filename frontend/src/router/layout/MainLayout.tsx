import React from 'react';
import { Outlet } from 'react-router';

import RootErrorBoundary from '@/components/ErrorBoundary/RootErrorBoundary';

const MainLayout = () => {
  return (
    <RootErrorBoundary>
      <Outlet />
    </RootErrorBoundary>
  );
};

export default MainLayout;
