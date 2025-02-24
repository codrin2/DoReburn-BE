import { lazy } from 'react';

const MainPage = lazy(() => import('@/pages/MainPage'));
const EditPage = lazy(() => import('@/pages/EditPage'));
const FavoritePage = lazy(() => import('@/pages/FavoritePage'));
const MapPage = lazy(() => import('@/pages/MapPage'));
const MyPage = lazy(() => import('@/pages/MyPage'));
const OnboardingPage = lazy(() => import('@/pages/OnboardingPage'));
const PlanPage = lazy(() => import('@/pages/PlanPage'));
const RecommendTodoPage = lazy(() => import('@/pages/RecommendTodoPage'));
const RouteSelectPage = lazy(() => import('@/pages/RouteSelectPage'));
const FeedbackPage = lazy(() => import('@/pages/FeedbackPage'));
const KakaoLoginPage = lazy(() => import('@/pages/KakaoLoginPage'));
const LandingPage = lazy(() => import('@/pages/LandingPage'));
const RouteTodoEditPage = lazy(() => import('@/pages/RouteTodoEditPage'));
const DayStatisticsPage = lazy(() => import('@/pages/DayStatisticsPage'));
const WeekStatisticsPage = lazy(() => import('@/pages/WeekStatisticsPage'));

export {
  MainPage,
  EditPage,
  FavoritePage,
  MapPage,
  MyPage,
  OnboardingPage,
  PlanPage,
  RecommendTodoPage,
  RouteSelectPage,
  FeedbackPage,
  KakaoLoginPage,
  LandingPage,
  RouteTodoEditPage,
  DayStatisticsPage,
  WeekStatisticsPage,
};
