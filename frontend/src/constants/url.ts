import { TodoType } from '@/types/todo';

const BASE_URL = import.meta.env.VITE_BASE_URL;

export const API_URL = {
  // 할 일
  todayTodo: `${BASE_URL}/api/v1/todos/today`,
  tomorrowTodo: `${BASE_URL}/api/v1/todos/tomorrow`,
  favoriteTodo: (queryParams: string) => `${BASE_URL}/api/v1/todos/save${queryParams}`,
  recommendLimitTodo: (modifyType: TodoType, planId?: number) =>
    `${BASE_URL}/api/v1/todos/recommend/personalized?modifyType=${modifyType}${planId ? `&pathId=${planId}` : ''}`,
  recommendAllTodo: (queryParams: string) => `${BASE_URL}/api/v1/todos/recommend/all${queryParams}`,
  addTodo: (todoType: TodoType, planId?: number) =>
    `${BASE_URL}/api/v1/todos/${todoType}/manual${planId ? `?pathId=${planId}` : ''}`,
  deleteTodo: (todoId: number, todoType: TodoType) =>
    `${BASE_URL}/api/v1/todos/${todoId}?type=${todoType}`,
  editTodo: (todoId: number, todoType: TodoType) =>
    `${BASE_URL}/api/v1/todos/${todoId}?type=${todoType}`,
  addTodoFromArchived: (todoType: TodoType, planId?: number) =>
    `${BASE_URL}/api/v1/todos/${todoType}/from-archived${planId ? `?pathId=${planId}` : ''}`,

  // 로그인
  loginKakao: `${BASE_URL}/api/v1/auth/KAKAO`,
  loginKakaoAuth: `${BASE_URL}/api/v1/auth/kakao-login`,
  authReissue: `${BASE_URL}/api/v1/auth/reissue`,

  // 온보딩
  onboarding: `${BASE_URL}/api/v1/members/onboarding`,

  // 주소 검색
  searchAddress: `${BASE_URL}/api/v1/places/search`,

  // 경로 선택
  searchRoutes: `${BASE_URL}/api/v1/routes/search`,

  // 경로별 할 일
  planInfo: `${BASE_URL}/api/v1/plans/recent`,
  plan: (planId?: number) => `${BASE_URL}/api/v1/plans${planId ? `?planId=${planId}` : ''}`,
  routeTodo: (planId: number) => `${BASE_URL}/api/v1/todos/path?pathId=${planId}`,
  updatePathTodo: (todoId: number, newPathId: number) =>
    `${BASE_URL}/api/v1/todos/path?todoId=${todoId}&newPathId=${newPathId}`,
  checkTodo: (todoId: number) => `${BASE_URL}/api/v1/todos/check?todoId=${todoId}`,
  finishPlan: `${BASE_URL}/api/v1/plans/move-complete`,

  // 피드백
  todayAchievement: `${BASE_URL}/api/v1/plans/feedbacks`,
  saveFeedback: (planId: number) => `${BASE_URL}/api/v1/plans/${planId}/feedbacks`,

  // 지도
  getNearbyUsers: (queryParams: string) =>
    `${BASE_URL}/api/v1/share/members/surrounding${queryParams}`,
  updateCurrentLocation: `${BASE_URL}/api/v1/members/location`,
  todoDetail: (memberId: number) =>
    `${BASE_URL}/api/v1/share/members/todos?surroundingMemberId=${memberId}`,
  deleteFavoriteFromOther: (memberId: number) =>
    `${BASE_URL}/api/v1/share/todos${memberId ? `?surroundingMemberTodoId=${memberId}` : ''}`,

  // 통계
  dayStatistics: `${BASE_URL}/api/v1/statistics/day`,
  weekStatistics: `${BASE_URL}/api/v1/statistics/week`,

  // 멤버
  memberAddress: `${BASE_URL}/api/v1/members/address`,
  memberStatus: `${BASE_URL}/api/v1/members/status`,
  memberInfo: `${BASE_URL}/api/v1/members`,

  // 알림
  notification: `${BASE_URL}/api/v1/notification`,
  notificationSubscribe: `${BASE_URL}/api/v1/notification/subscribe`,
  notificationFCM: `${BASE_URL}/api/v1/notification/fcm/token`,
};

export const MOCK_API_URL = {
  // 할 일
  todayTodo: `${BASE_URL}/api/v1/todos/today`,
  tomorrowTodo: `${BASE_URL}/api/v1/todos/tomorrow`,
  favoriteTodo: `${BASE_URL}/api/v1/todos/save`,
  recommendLimitTodo: `${BASE_URL}/api/v1/todos/recommend/personalized`,
  recommendAllTodo: `${BASE_URL}/api/v1/todos/recommend/all`,
  addTodo: `${BASE_URL}/api/v1/todos/:todoType/manual`,
  deleteTodo: `${BASE_URL}/api/v1/todos/:todoId`,
  editTodo: `${BASE_URL}/api/v1/todos/:todoId`,
  addTodoFromArchived: `${BASE_URL}/api/v1/todos/:todoType/from-archived`,

  // 로그인
  loginKakao: `${BASE_URL}/api/v1/auth/KAKAO`,
  loginKakaoAuth: `${BASE_URL}/api/v1/auth/kakao-login`,

  // 온보딩
  onboarding: `${BASE_URL}/api/v1/members/onboarding`,

  // 주소 검색
  searchAddress: `${BASE_URL}/api/v1/places/search`,

  // 경로 선택
  searchRoutes: `${BASE_URL}/api/v1/routes/search`,

  // 경로별 할 일
  planInfo: `${BASE_URL}/api/v1/plans/recent`,
  plan: `${BASE_URL}/api/v1/plans`,
  routeTodo: `${BASE_URL}/api/v1/routes/:planId/todos`,
  finishPlan: `${BASE_URL}/api/v1/plans/move-complete`,

  // 피드백
  todayAchievement: `${BASE_URL}/api/v1/plans/feedbacks`,
  saveFeedback: `${BASE_URL}/api/v1/plans/:planId/feedbacks`,

  // 지도
  getNearbyUsers: `${BASE_URL}/api/v1/share/members/surrounding`,
  updateCurrentLocation: `${BASE_URL}/api/v1/members/location`,
  todoDetail: `${BASE_URL}/api/v1/share/members/:memberId`,
  addFavoriteFromOther: `${BASE_URL}/api/v1/share/todos`,
  deleteFavoriteFromOther: `${BASE_URL}/api/v1/share/todos`,

  // 통계
  dayStatistics: `${BASE_URL}/api/v1/statistics/day`,
  weekStatistics: `${BASE_URL}/api/v1/statistics/week`,

  // 멤버
  memberInfo: `${BASE_URL}/api/v1/members`,
  memberStatus: `${BASE_URL}/api/v1/members/status`,
  memberAddress: `${BASE_URL}/api/v1/members/address`,

  // 알림
  notification: `${BASE_URL}/api/v1/notification`,
  notificationSubscribe: `${BASE_URL}/api/v1/notification/subscribe`,
};
