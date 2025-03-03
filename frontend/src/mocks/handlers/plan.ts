import { http, HttpResponse } from 'msw';

import MEMBER_STATUS from '../data/member/memberStatus.json';
import PLAN_DATA from '../data/planInfo.json';

import { MOCK_API_URL } from '@/constants/url';

const getPlanInfoHandler = () => {
  return HttpResponse.json(PLAN_DATA);
};

const createPlanHandler = () => {
  MEMBER_STATUS.data.status = 'MOVE';

  return HttpResponse.json({ data: { planId: 123 } });
};

const finishPlanHandler = () => {
  MEMBER_STATUS.data.status = 'FEEDBACK';

  return new HttpResponse(null, { status: 204 });
};

const cancelPlanHandler = () => {
  return new HttpResponse(null, { status: 204 });
};

const checkTodoHandler = async ({ request }: { request: Request }) => {
  const { isCompleted } = await request.json();

  const url = new URL(request.url);
  const todoId = url.searchParams.get('todoId');

  PLAN_DATA.data.paths.forEach((path) => {
    path.todos.forEach((todo) => {
      if (todo.todoId === Number(todoId)) {
        todo.isDone = isCompleted;
      }
    });
  });
};

export const handlers = [
  http.get(MOCK_API_URL.planInfo, getPlanInfoHandler),
  http.post(MOCK_API_URL.plan, createPlanHandler),
  http.delete(MOCK_API_URL.plan, cancelPlanHandler),
  http.patch(MOCK_API_URL.finishPlan, finishPlanHandler),
  http.patch(MOCK_API_URL.checkTodo, checkTodoHandler),
];
