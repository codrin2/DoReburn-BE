import { keepPreviousData, useInfiniteQuery } from '@tanstack/react-query';

import { getRecommendAllTodoList } from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';
import { CategoryType, DifficultyType } from '@/types/filter';
import { TodoType } from '@/types/todo';

const PAGE_SIZE = 10;

const useRecommendTodoFilterQuery = (
  todoType: TodoType,
  categoryList: CategoryType[],
  difficultyList: DifficultyType[],
  pathId?: number,
) => {
  return useInfiniteQuery({
    queryKey: [QUERY_KEY.recommendAll, todoType, ...categoryList, ...difficultyList, pathId || 0],
    queryFn: ({ pageParam }) =>
      getRecommendAllTodoList({
        modifyType: todoType,
        size: PAGE_SIZE,
        pathId,
        category: categoryList,
        difficulty: difficultyList,
        cursorCategoryId: pageParam.cursorCategoryId,
        cursorDifficulty: pageParam.cursorDifficulty,
        cursorTodoId: pageParam.cursorTodoId,
      }),
    staleTime: Infinity,
    gcTime: Infinity,
    placeholderData: keepPreviousData,
    initialPageParam: {
      cursorCategoryId: 0,
      cursorDifficulty: 'EASY' as DifficultyType,
      cursorTodoId: 0,
    },
    getNextPageParam: (lastPage) => {
      if (!lastPage.hasNext) return null;

      return lastPage.nextCursor;
    },
  });
};

export default useRecommendTodoFilterQuery;
