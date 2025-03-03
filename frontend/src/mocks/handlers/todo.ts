import { http, HttpResponse } from 'msw';

import PLAN_DATA from '../data/planInfo.json';
import FAVORITE_TODO from '../data/todo/favorite.json';
import PERSONALIZED_TODO from '../data/todo/personalized.json';
import RECOMMEND_TODO from '../data/todo/recommend.json';
import ROUTE_TODO_DATA from '../data/todo/routeTodo.json';
import TODAY_TODO from '../data/todo/today.json';
import TOMORROW_TODO from '../data/todo/tomorrow.json';

import { RecommendTodo } from '@/api/todo';
import { MOCK_API_URL } from '@/constants/url';
import { TodoType } from '@/types/todo';

interface TodoCreateParams {
  todoType: TodoType;
}

interface TodoDeleteParams {
  todoId: string;
}

interface TodoEditParams {
  todoId: string;
  pathId?: string;
}

const applyCheckStatus = (newTodo: RecommendTodo) => {
  FAVORITE_TODO.data = FAVORITE_TODO.data.map((todo) =>
    todo.todoId === newTodo.todoId ? { ...todo, hasChild: true } : todo,
  );

  PERSONALIZED_TODO.data = PERSONALIZED_TODO.data.map((todo) =>
    todo.todoId === newTodo.todoId ? { ...todo, hasChild: true } : todo,
  );

  RECOMMEND_TODO.data = RECOMMEND_TODO.data.map((todo) =>
    todo.todoId === newTodo.todoId ? { ...todo, hasChild: true } : todo,
  );
};

const getNewTodo = (todoId: number) => {
  const favoriteTodo = FAVORITE_TODO.data.find((todo) => todo.todoId === todoId) as RecommendTodo;
  const recommendTodo = RECOMMEND_TODO.data.find((todo) => todo.todoId === todoId) as RecommendTodo;

  return favoriteTodo ? favoriteTodo : recommendTodo;
};

const getTodayTodoHandler = () => {
  return HttpResponse.json(TODAY_TODO);
};

const getTomorrowTodoHandler = () => {
  return HttpResponse.json(TOMORROW_TODO);
};

const getFavoriteTodoHandler = () => {
  return HttpResponse.json(FAVORITE_TODO);
};

const getRecommendLimitTodoHandler = () => {
  return HttpResponse.json(PERSONALIZED_TODO);
};

const getRecommendAllTodoHandler = async ({ request }: { request: Request }) => {
  const url = new URL(request.url);

  const category = url.searchParams.get('category');
  const difficulty = url.searchParams.get('difficulty');

  if (category && difficulty) {
    // 둘 다 있는 경우
    const categoryList = category.split(',');
    const difficultyList = difficulty.split(',');

    const filteredTodo = {
      ...RECOMMEND_TODO,
      data: RECOMMEND_TODO.data.filter(
        (todo) => categoryList.includes(todo.category) && difficultyList.includes(todo.difficulty),
      ),
    };

    return HttpResponse.json(filteredTodo);
  } else if (category) {
    // 카테고리만 있는 경우
    const categoryList = category.split(',');

    const filteredTodo = {
      ...RECOMMEND_TODO,
      data: RECOMMEND_TODO.data.filter((todo) => categoryList.includes(todo.category)),
    };

    return HttpResponse.json(filteredTodo);
  } else if (difficulty) {
    // 난이도만 있는 경우
    const difficultyList = difficulty.split(',');

    const filteredTodo = {
      ...RECOMMEND_TODO,
      data: RECOMMEND_TODO.data.filter((todo) => difficultyList.includes(todo.difficulty)),
    };

    return HttpResponse.json(filteredTodo);
  }

  // 둘 다 없는 경우
  return HttpResponse.json(RECOMMEND_TODO);
};

/** 할 일 추가(바텀시트) */
const addTodoHandler = async ({
  params,
  request,
}: {
  params: TodoCreateParams;
  request: Request;
}) => {
  const requestParams = params;
  const newTodo = await request.json();

  if (requestParams.todoType === 'PATH') {
    /** 경로별 할 일 */
    ROUTE_TODO_DATA.data.push({
      ...newTodo,
      todoId: ROUTE_TODO_DATA.data.length + 1,
    });

    return HttpResponse.json(newTodo);
  } else if (requestParams.todoType === 'TODAY') {
    /** 오늘 할 일 */
    TODAY_TODO.data.push({
      ...newTodo,
      todoId: TODAY_TODO.data.length + 1,
    });

    return HttpResponse.json(newTodo);
  } else if (requestParams.todoType === 'TOMORROW') {
    /** 내일 할 일 */
    TOMORROW_TODO.data.push({
      ...newTodo,
      todoId: TOMORROW_TODO.data.length + 1,
    });

    return HttpResponse.json(newTodo);
  } else if (requestParams.todoType === 'SAVE') {
    /** 즐겨찾기 */
    FAVORITE_TODO.data.push({
      ...newTodo,
      todoId: FAVORITE_TODO.data.length + 1,
    });

    return HttpResponse.json(newTodo);
  }
};

/** 할 일 삭제 */
const deleteTodoHandler = ({ params, request }: { params: TodoDeleteParams; request: Request }) => {
  const { todoId } = params;

  const url = new URL(request.url);
  const todoType = url.searchParams.get('type');

  if (todoType === 'PATH') {
    /** 경로별 할 일 */
    ROUTE_TODO_DATA.data = ROUTE_TODO_DATA.data.filter((todo) => todo.todoId !== Number(todoId));
  } else if (todoType === 'TODAY') {
    /** 오늘 할 일 */
    TODAY_TODO.data = TODAY_TODO.data.filter((todo) => todo.todoId !== Number(todoId));
  } else if (todoType === 'TOMORROW') {
    /** 내일 할 일 */
    TOMORROW_TODO.data = TOMORROW_TODO.data.filter((todo) => todo.todoId !== Number(todoId));
  } else if (todoType === 'SAVE') {
    /** 즐겨찾기 */
    FAVORITE_TODO.data = FAVORITE_TODO.data.filter((todo) => todo.todoId !== Number(todoId));
  }

  return new HttpResponse(null, { status: 204 });
};

/** 할 일 수정 */
const editTodoHandler = async ({
  params,
  request,
}: {
  params: TodoEditParams;
  request: Request;
}) => {
  const { todoId } = params;
  const newTodo = await request.json();

  const url = new URL(request.url);
  const todoType = url.searchParams.get('type');

  if (todoType === 'PATH') {
    /** 경로별 할 일 */
    ROUTE_TODO_DATA.data = ROUTE_TODO_DATA.data.map((todo) =>
      todo.todoId === Number(todoId) ? { ...newTodo } : todo,
    );
  } else if (todoType === 'TODAY') {
    /** 오늘 할 일 */
    TODAY_TODO.data = TODAY_TODO.data.map((todo) =>
      todo.todoId === Number(todoId) ? { ...newTodo } : todo,
    );
  } else if (todoType === 'TOMORROW') {
    /** 내일 할 일 */
    TOMORROW_TODO.data = TOMORROW_TODO.data.map((todo) =>
      todo.todoId === Number(todoId) ? { ...newTodo } : todo,
    );
  } else if (todoType === 'SAVE') {
    /** 즐겨찾기 */
    FAVORITE_TODO.data = FAVORITE_TODO.data.map((todo) =>
      todo.todoId === Number(todoId) ? { ...newTodo } : todo,
    );
  }

  return new HttpResponse(null, { status: 204 });
};

const addTodoFromArchivedHandler = async ({
  params,
  request,
}: {
  params: TodoCreateParams;
  request: Request;
}) => {
  const requestParams = params;
  const { todoId } = await request.json();

  const url = new URL(request.url);
  const pathId = url.searchParams.get('pathId');

  if (requestParams.todoType === 'PATH' && pathId) {
    /** 경로별 할 일 */
    const newTodo = getNewTodo(todoId);

    if (!newTodo) return;

    PLAN_DATA.data.paths.forEach((path) => {
      if (path.pathId === Number(pathId)) {
        (path.todos as RecommendTodo[]).push(newTodo);
      }
    });

    applyCheckStatus(newTodo);

    return HttpResponse.json(newTodo);
  } else if (requestParams.todoType === 'TODAY') {
    /** 오늘 할 일 */
    const newTodo = getNewTodo(todoId);

    if (!newTodo) return;

    TODAY_TODO.data.push({
      ...newTodo,
      todoId: TODAY_TODO.data.length + 1,
    });

    applyCheckStatus(newTodo);

    return HttpResponse.json(newTodo);
  } else if (requestParams.todoType === 'TOMORROW') {
    /** 내일 할 일 */
    const newTodo = getNewTodo(todoId);

    if (!newTodo) return;

    TOMORROW_TODO.data.push({
      ...newTodo,
      todoId: TOMORROW_TODO.data.length + 1,
    });

    applyCheckStatus(newTodo);

    return HttpResponse.json(newTodo);
  } else if (requestParams.todoType === 'SAVE') {
    /** 즐겨찾기 */
    const newTodo = getNewTodo(todoId);

    if (!newTodo) return;

    FAVORITE_TODO.data.push({
      ...newTodo,
      todoId: FAVORITE_TODO.data.length + 1,
    });

    applyCheckStatus(newTodo);

    return HttpResponse.json(newTodo);
  }
};

const getRouteTodoListHandler = ({ request }: { request: Request }) => {
  const url = new URL(request.url);
  const pathId = url.searchParams.get('pathId');

  const routeTodo = {
    data: PLAN_DATA.data.paths.find((path) => path.pathId === Number(pathId))?.todos,
  };

  if (!routeTodo.data) {
    return HttpResponse.json({ error: 'pathId not found' }, { status: 404 });
  }

  return HttpResponse.json(routeTodo);
};

export const handlers = [
  http.get(MOCK_API_URL.todayTodo, getTodayTodoHandler),
  http.get(MOCK_API_URL.tomorrowTodo, getTomorrowTodoHandler),
  http.get(MOCK_API_URL.favoriteTodo, getFavoriteTodoHandler),
  http.get(MOCK_API_URL.recommendLimitTodo, getRecommendLimitTodoHandler),
  http.get(MOCK_API_URL.recommendAllTodo, getRecommendAllTodoHandler),
  http.post<TodoCreateParams>(MOCK_API_URL.addTodo, addTodoHandler),
  http.delete<TodoDeleteParams>(MOCK_API_URL.deleteTodo, deleteTodoHandler),
  http.patch<TodoEditParams>(MOCK_API_URL.editTodo, editTodoHandler),
  http.post<TodoCreateParams>(MOCK_API_URL.addTodoFromArchived, addTodoFromArchivedHandler),
  http.get(MOCK_API_URL.routeTodo, getRouteTodoListHandler),
];
