import { TodoType } from '@/types/todo';

const BASE_URL = import.meta.env.VITE_BASE_URL;

export const API_URL = {
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
  routeTodo: (planId: number) => `${BASE_URL}/api/v1/todos/path?pathId=${planId}`,
  searchAddress: `${BASE_URL}/api/v1/places/search`,
  searchRoutes: `${BASE_URL}/api/v1/routes/search`,
  onboarding: `${BASE_URL}/api/v1/members/onboarding`,
  planInfo: `${BASE_URL}/api/v1/plans/recent`,
  memberAddress: `${BASE_URL}/api/v1/members/address`,
  loginKakao: `${BASE_URL}/api/v1/auth/KAKAO`,
  loginKakaoAuth: `${BASE_URL}/api/v1/auth/kakao-login`,
  memberStatus: `${BASE_URL}/api/v1/members/status`,
  todoDetail: (memberId: number) =>
    `${BASE_URL}/api/v1/share/members/todos?surroundingMemberId=${memberId}`,
  deleteFavoriteFromOther: (memberId: number) =>
    `${BASE_URL}/api/v1/share/todos${memberId ? `?surroundingMemberTodoId=${memberId}` : ''}`,
  getNearbyUsers: (queryParams: string) =>
    `${BASE_URL}/api/v1/share/members/surrounding${queryParams}`,
  todayAchievement: `${BASE_URL}/api/v1/plans/feedbacks`,
  saveFeedback: (planId: number) => `${BASE_URL}/api/v1/plans/${planId}/feedbacks`,
  weekStatistics: `${BASE_URL}/api/v1/statistics/week`,
  plan: (planId?: number) => `${BASE_URL}/api/v1/plans${planId ? `?planId=${planId}` : ''}`,
  memberInfo: `${BASE_URL}/api/v1/members`,
  checkTodo: (todoId: number) => `${BASE_URL}/api/v1/todos/check?todoId=${todoId}`,
  finishPlan: `${BASE_URL}/api/v1/plans/move-complete`,
  dayStatistics: `${BASE_URL}/api/v1/statistics/day`,
  notification: `${BASE_URL}/api/v1/notification`,
  notificationSubscribe: `${BASE_URL}/api/v1/notification/subscribe`,
  updateCurrentLocation: `${BASE_URL}/api/v1/members/location`,
  authReissue: `${BASE_URL}/api/v1/auth/reissue`,
  updatePathTodo: (todoId: number, newPathId: number) =>
    `${BASE_URL}/api/v1/todos/path?todoId=${todoId}&newPathId=${newPathId}`,
  notificationFCM: `${BASE_URL}/api/v1/notification/fcm/token`,
};

export const MOCK_API_URL = {
  todayTodo: `${BASE_URL}/api/v1/todos/today`,
  tomorrowTodo: `${BASE_URL}/api/v1/todos/tomorrow`,
  recommendLimitTodo: `${BASE_URL}/api/v1/todos/recommend/personalized`,
  favoriteTodo: `${BASE_URL}/api/v1/todos/save`,
  recommendAllTodo: `${BASE_URL}/api/v1/todos/recommend/all`,
  addTodo: `${BASE_URL}/api/v1/todos/:todoType/manual`,
  deleteTodo: `${BASE_URL}/api/v1/todos/:todoId`,
  editTodo: `${BASE_URL}/api/v1/todos/:todoId`,
  addTodoFromArchived: `${BASE_URL}/api/v1/todos/:todoType/from-archived`,
  routeTodo: `${BASE_URL}/api/v1/routes/:planId/todos`,
  searchAddress: `${BASE_URL}/api/v1/places/search`,
  searchRoutes: `${BASE_URL}/api/v1/routes/search`,
  onboarding: `${BASE_URL}/api/v1/members/onboarding`,
  planInfo: `${BASE_URL}/api/v1/plans/recent`,
  memberAddress: `${BASE_URL}/api/v1/members/address`,
  loginKakao: `${BASE_URL}/api/v1/auth/KAKAO`,
  loginKakaoAuth: `${BASE_URL}/api/v1/auth/kakao-login`,
  memberStatus: `${BASE_URL}/api/v1/members/status`,
  plan: `${BASE_URL}/api/v1/plans`,
  todoDetail: `${BASE_URL}/api/v1/share/members/:memberId`,
  addFavoriteFromOther: `${BASE_URL}/api/v1/share/todos`,
  deleteFavoriteFromOther: `${BASE_URL}/api/v1/share/todos`,
  getNearbyUsers: `${BASE_URL}/api/v1/share/members/surrounding`,
  todayAchievement: `${BASE_URL}/api/v1/plans/feedbacks`,
  saveFeedback: (planId: number) => `${BASE_URL}/api/v1/plans/${planId}/feedbacks`,
  weekStatistics: `${BASE_URL}/api/v1/statistics/week`,
  createPlan: `${BASE_URL}/api/v1/plans`,
  dayStatistics: `${BASE_URL}/api/v1/statistics/day`,
  updateCurrentLocation: `${BASE_URL}/api/v1/members/location`,
  memberInfo: `${BASE_URL}/api/v1/members`,
  notification: `${BASE_URL}/api/v1/notification`,
  notificationSubscribe: `${BASE_URL}/api/v1/notification/subscribe`,
};
