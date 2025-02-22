import fetchClient from './fetchClient';

import { API_URL } from '@/constants/url';
import { Todo } from '@/types/todo';

export interface PlanInfoResponse {
  data: {
    planId: number;
    totalSectionTime: number;
    createdAt: string;
    paths: Path[];
  };
}

export interface Path {
  pathId: number;
  trafficType: 'SUBWAY' | 'BUS';
  sectionTime: number;
  subwayCode: number | null;
  busNumber: string | null;
  busType: number | null;
  startName: string;
  endName: string;
  todos: PathTodo[];
}

export interface PathTodo extends Todo {
  isDone: boolean;
}

interface CreatePlanPath {
  trafficType: 'SUBWAY' | 'BUS';
  sectionTime: number;
  subwayCode: number | null;
  busType: number | null;
  busNumber: string | null;
  busType: number | null;
  startName: string;
  endName: string;
}

export interface CreatePlanRequest {
  totalTime: number;
  totalSectionTime: number;
  paths: CreatePlanPath[];
}

export const getPlanInfo = async () => {
  const result = await fetchClient.get<PlanInfoResponse>(API_URL.planInfo);

  return result.data;
};

export const cancelPlan = async (planId: number) => {
  return await fetchClient.delete(API_URL.plan(planId));
};

export const createPlan = async (
  request: CreatePlanRequest,
  coordinates: { startX: string; startY: string; endX: string; endY: string },
) => {
  const queryParams = new URLSearchParams(coordinates).toString();

  return await fetchClient.post(`${API_URL.plan()}?${queryParams}`, { body: { ...request } });
};

export const checkTodo = async (todoId: number, isCompleted: boolean) => {
  return await fetchClient.patch(API_URL.checkTodo(todoId), { body: { isCompleted } });
};

export const finishPlan = async () => {
  return await fetchClient.patch(API_URL.finishPlan);
};
