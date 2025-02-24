import { createBrowserRouter } from 'react-router';

import FlexPageLayout from './layout/FlexPageLayout';
import memberStatusLoader from './memberStatusLoader';

import {
  MainPage,
  EditPage,
  FavoritePage,
  MapPage,
  MyPage,
  OnboardingPage,
  PlanPage,
  RecommendTodoPage,
  FeedbackPage,
  RouteTodoEditPage,
  DayStatisticsPage,
  WeekStatisticsPage,
  RouteSelectPage,
  KakaoLoginPage,
  LandingPage,
} from '@/pages/LazyPage';

export const router = createBrowserRouter([
  {
    path: '/landing',
    element: <LandingPage />,
  },
  {
    path: '/login/kakao',
    element: <KakaoLoginPage />,
  },
  {
    path: '/onboarding',
    element: <OnboardingPage />,
    loader: memberStatusLoader,
  },
  {
    path: '/',
    element: <MainPage />,
    loader: memberStatusLoader,
  },
  {
    path: '/edit',
    element: <FlexPageLayout />,
    children: [
      {
        index: true,
        element: <EditPage />,
      },
    ],
  },
  {
    path: '/route-select',
    element: <RouteSelectPage />,
  },
  {
    path: '/plan',
    element: <FlexPageLayout />,
    children: [
      {
        index: true,
        element: <PlanPage />,
        loader: memberStatusLoader,
      },
      {
        path: ':planId/todos/edit',
        element: <FlexPageLayout />,
        children: [
          {
            index: true,
            element: <RouteTodoEditPage />,
          },
        ],
      },
    ],
  },
  {
    path: '/recommend/:planId?',
    element: <FlexPageLayout />,
    children: [
      {
        index: true,
        element: <RecommendTodoPage />,
      },
    ],
  },
  {
    path: '/feedback',
    element: <FeedbackPage />,
    loader: memberStatusLoader,
  },
  {
    path: '/map',
    element: <MapPage />,
  },
  {
    path: '/statistics',
    children: [
      {
        path: 'day',
        element: <DayStatisticsPage />,
      },
      {
        path: 'week',
        element: <WeekStatisticsPage />,
      },
    ],
  },
  {
    path: '/my-page',
    element: <MyPage />,
  },
  {
    path: '/favorite',
    element: <FlexPageLayout />,
    children: [
      {
        index: true,
        element: <FavoritePage />,
      },
    ],
  },
]);
