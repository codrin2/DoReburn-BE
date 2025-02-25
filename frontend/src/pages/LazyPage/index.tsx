import { lazy } from 'react';

const MainPage = lazy(() => import('../MainPage'));
const EditPage = lazy(() => import('../EditPage'));
const FavoritePage = lazy(() => import('../FavoritePage'));
const MapPage = lazy(() => import('../MapPage'));
const MyPage = lazy(() => import('../MyPage'));
const OnboardingPage = lazy(() => import('../OnboardingPage'));
const PlanPage = lazy(() => import('../PlanPage'));
const RecommendTodoPage = lazy(() => import('../RecommendTodoPage'));
const RouteSelectPage = lazy(() => import('../RouteSelectPage'));
const FeedbackPage = lazy(() => import('../FeedbackPage'));
const KakaoLoginPage = lazy(() => import('../KakaoLoginPage'));
const LandingPage = lazy(() => import('../LandingPage'));
const RouteTodoEditPage = lazy(() => import('../RouteTodoEditPage'));
const DayStatisticsPage = lazy(() => import('../DayStatisticsPage'));
const WeekStatisticsPage = lazy(() => import('../WeekStatisticsPage'));

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
