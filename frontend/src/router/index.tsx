import { createBrowserRouter } from 'react-router';

import FlexPageLayout from './layout/FlexPageLayout';
import MainLayout from './layout/MainLayout';
import memberStatusLoader from './memberStatusLoader';

import CustomSuspense from '@/components/CustomSuspense/CustomSuspense';
import RouteErrorPage from '@/pages/ErrorPage/RouteErrorPage';
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
    path: '/',
    element: <MainLayout />,
    children: [
      {
        path: '/landing',
        element: (
          <CustomSuspense>
            <LandingPage />
          </CustomSuspense>
        ),
      },
      {
        path: '/login/kakao',
        element: (
          <CustomSuspense>
            <KakaoLoginPage />
          </CustomSuspense>
        ),
      },
      {
        path: '/onboarding',
        element: (
          <CustomSuspense>
            <OnboardingPage />
          </CustomSuspense>
        ),
        loader: memberStatusLoader,
      },
      {
        path: '/',
        element: (
          <CustomSuspense>
            <MainPage />
          </CustomSuspense>
        ),
        loader: memberStatusLoader,
      },
      {
        path: '/edit',
        element: <FlexPageLayout />,
        children: [
          {
            index: true,
            element: (
              <CustomSuspense>
                <EditPage />
              </CustomSuspense>
            ),
          },
        ],
      },
      {
        path: '/route-select',
        element: (
          <CustomSuspense>
            <RouteSelectPage />
          </CustomSuspense>
        ),
      },
      {
        path: '/plan',
        element: <FlexPageLayout />,
        children: [
          {
            index: true,
            element: (
              <CustomSuspense>
                <PlanPage />
              </CustomSuspense>
            ),
            loader: memberStatusLoader,
          },
          {
            path: ':planId/todos/edit',
            element: <FlexPageLayout />,
            children: [
              {
                index: true,
                element: (
                  <CustomSuspense>
                    <RouteTodoEditPage />
                  </CustomSuspense>
                ),
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
            element: (
              <CustomSuspense>
                <RecommendTodoPage />
              </CustomSuspense>
            ),
          },
        ],
      },
      {
        path: '/feedback',
        element: (
          <CustomSuspense>
            <FeedbackPage />
          </CustomSuspense>
        ),
        loader: memberStatusLoader,
      },
      {
        path: '/map',
        element: (
          <CustomSuspense>
            <MapPage />
          </CustomSuspense>
        ),
      },
      {
        path: '/statistics',
        children: [
          {
            path: 'day',
            element: (
              <CustomSuspense>
                <DayStatisticsPage />
              </CustomSuspense>
            ),
          },
          {
            path: 'week',
            element: (
              <CustomSuspense>
                <WeekStatisticsPage />
              </CustomSuspense>
            ),
          },
        ],
      },
      {
        path: '/my-page',
        element: (
          <CustomSuspense>
            <MyPage />
          </CustomSuspense>
        ),
      },
      {
        path: '/favorite',
        element: <FlexPageLayout />,
        children: [
          {
            index: true,
            element: (
              <CustomSuspense>
                <FavoritePage />
              </CustomSuspense>
            ),
          },
        ],
      },
      {
        path: '*',
        element: <RouteErrorPage />,
      },
    ],
  },
]);
