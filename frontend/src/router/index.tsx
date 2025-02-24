import { createBrowserRouter } from 'react-router';

import FlexPageLayout from './layout/FlexPageLayout';
import memberStatusLoader from './memberStatusLoader';

import DayStatisticsPage from '@/pages/DayStatisticsPage';
import EditPage from '@/pages/EditPage';
import FavoritePage from '@/pages/FavoritePage';
import FeedbackPage from '@/pages/FeedbackPage';
import KakaoLoginPage from '@/pages/KakaoLoginPage';
import LandingPage from '@/pages/LandingPage';
import MainPage from '@/pages/MainPage';
import MapPage from '@/pages/MapPage';
import MyPage from '@/pages/MyPage';
import OnboardingPage from '@/pages/OnboardingPage';
import PlanPage from '@/pages/PlanPage';
import RecommendTodoPage from '@/pages/RecommendTodoPage';
import RouteSelectPage from '@/pages/RouteSelectPage';
import RouteTodoEditPage from '@/pages/RouteTodoEditPage';
import WeekStatisticsPage from '@/pages/WeekStatisticsPage';

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
