import { http, HttpResponse } from 'msw';

import ARCHIVED_TODO_DATA from '../data/archivedTodo.json';
import FAVORITE_TODO from '../data/favorite.json';
import PERSONALIZED_TODO from '../data/personalized.json';
import RECOMMEND_TODO from '../data/recommend.json';
import ROUTE_TODO_DATA from '../data/routeTodo.json';
import TODAY_TODO from '../data/today.json';
import TODO_DATA from '../data/todoData.json';
import TOMORROW_TODO from '../data/tomorrow.json';

import { MOCK_API_URL } from '@/constants/url';

interface TodoCreateParams {
  todoType: string;
  planId?: string;
}

interface TodoDeleteParams {
  todoId: string;
  planId?: string;
}

interface TodoEditParams {
  todoId: string;
  planId?: string;
}

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

const addTodoHandler = async ({
  params,
  request,
}: {
  params: TodoCreateParams;
  request: Request;
}) => {
  const requestParams = params;
  const newTodo = await request.json();

  if (requestParams.planId) {
    ROUTE_TODO_DATA.data.push({
      ...newTodo,
      type: requestParams.todoType,
      todoId: ROUTE_TODO_DATA.data.length + 1,
    });

    return HttpResponse.json(newTodo);
  }

  TODO_DATA.data.push({
    ...newTodo,
    type: requestParams.todoType,
    todoId: TODO_DATA.data.length + 1,
  });

  return HttpResponse.json(newTodo);
};

const deleteTodoHandler = ({ params }: { params: TodoDeleteParams }) => {
  const { todoId, planId } = params;

  if (planId) {
    ROUTE_TODO_DATA.data = ROUTE_TODO_DATA.data.filter((todo) => todo.todoId !== Number(todoId));
  } else {
    TODO_DATA.data = TODO_DATA.data.filter((todo) => todo.todoId !== Number(todoId));
  }

  return new HttpResponse(null, { status: 204 });
};

const editTodoHandler = async ({
  params,
  request,
}: {
  params: TodoEditParams;
  request: Request;
}) => {
  const { todoId, planId } = params;
  const newTodo = await request.json();

  if (planId) {
    ROUTE_TODO_DATA.data = ROUTE_TODO_DATA.data.map((todo) =>
      todo.todoId === Number(todoId) ? { ...newTodo, type: todo.type } : todo,
    );
  } else {
    TODO_DATA.data = TODO_DATA.data.map((todo) =>
      todo.todoId === Number(todoId) ? { ...newTodo, type: todo.type } : todo,
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
  const { todoType, planId } = params;
  const { todoId } = await request.json();

  const newTodo = ARCHIVED_TODO_DATA.data.find((todo) => todo.todoId === Number(todoId));

  if (newTodo === undefined) {
    return new HttpResponse(
      JSON.stringify({ message: '즐겨찾기 또는 추천에 해당하는 todo가 없습니다.' }),
      {
        status: 404,
      },
    );
  }

  if (planId) {
    ROUTE_TODO_DATA.data.push({
      ...newTodo,
      type: todoType,
      todoId: ROUTE_TODO_DATA.data.length + 1,
    });

    return HttpResponse.json(newTodo);
  }

  TODO_DATA.data.push({ ...newTodo, type: todoType, todoId: TODO_DATA.data.length + 1 });

  return HttpResponse.json(newTodo);
};

const getRouteTodoListHandler = () => {
  return HttpResponse.json(ROUTE_TODO_DATA);
};

export const handlers = [
  http.get(MOCK_API_URL.todayTodo, getTodayTodoHandler),
  http.get(MOCK_API_URL.tomorrowTodo, getTomorrowTodoHandler),
  http.get(MOCK_API_URL.favoriteTodo, getFavoriteTodoHandler),
  http.get(MOCK_API_URL.recommendLimitTodo, getRecommendLimitTodoHandler),
  http.get(MOCK_API_URL.recommendAllTodo, getRecommendAllTodoHandler),
  http.post<TodoCreateParams>(MOCK_API_URL.addTodo, addTodoHandler),
  http.delete(MOCK_API_URL.deleteTodo, deleteTodoHandler),
  http.patch<TodoEditParams>(MOCK_API_URL.editTodo, editTodoHandler),
  http.post<TodoCreateParams>(MOCK_API_URL.addTodoFromArchived, addTodoFromArchivedHandler),
  http.get(MOCK_API_URL.routeTodo, getRouteTodoListHandler),
];
