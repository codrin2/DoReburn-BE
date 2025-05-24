import { useMutation, useQueryClient } from '@tanstack/react-query';

import { addTodoFromArchived } from '@/api/todo';
import { QUERY_KEY } from '@/constants/queryKey';

const useAddFavoriteFromOther = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ todoId, memberId }: { todoId: number; memberId: number }) => {
      return addTodoFromArchived('FAVORITE', todoId);
    },
    onSuccess: (_, { memberId }) => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.detailTodo, memberId] });
    },
  });
};

export default useAddFavoriteFromOther;
