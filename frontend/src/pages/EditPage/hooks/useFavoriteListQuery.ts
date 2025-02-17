import { keepPreviousData, useInfiniteQuery } from '@tanstack/react-query';

import { getFavoriteTodoList } from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';
import { TodoType } from '@/types/todo';

const PAGE_SIZE = 5;

const useFavoriteTodoListQuery = (todoType: TodoType, planId?: number) => {
  return useInfiniteQuery({
    queryKey: [QUERY_KEY.favorite, todoType, planId || 0],
    queryFn: ({ pageParam }) =>
      getFavoriteTodoList({ modifyType: todoType, size: PAGE_SIZE, planId, cursor: pageParam }),
    placeholderData: keepPreviousData,
    initialPageParam: 1,
    getNextPageParam: (lastPage) => {
      // getNextPageParam에서 null 또는 undefined 반환 시 hasNextPage: false
      if (!lastPage.hasNext) return null;

      return lastPage.nextCursor;
    },
  });
};

export default useFavoriteTodoListQuery;
