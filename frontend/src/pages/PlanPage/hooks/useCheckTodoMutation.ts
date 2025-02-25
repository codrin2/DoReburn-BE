import { useMutation, useQueryClient } from '@tanstack/react-query';

import { checkTodo, PlanInfoResponse } from '@/api/plan';
import { QUERY_KEY } from '@/constants/queryKey';

// 할 일 체크 상태 낙관적 업데이트 적용
const useCheckTodoMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ todoId, isCompleted }: { todoId: number; isCompleted: boolean }) =>
      checkTodo(todoId, isCompleted),

    onMutate: async ({ todoId, isCompleted }) => {
      // 진행 중인 쿼리 취소
      await queryClient.cancelQueries({ queryKey: [QUERY_KEY.planInfo] });

      // 이전 쿼리 데이터 불러오기
      const previousPlanInfo = queryClient.getQueryData<PlanInfoResponse['data']>([
        QUERY_KEY.planInfo,
      ]);

      // 응답이 오기 전 새로운 데이터로 갱신
      queryClient.setQueryData<PlanInfoResponse['data']>([QUERY_KEY.planInfo], (planInfo) => {
        if (!planInfo) return planInfo;

        return {
          ...planInfo,
          paths: planInfo.paths.map((path) => ({
            ...path,
            todos: path.todos.map((todo) =>
              todo.todoId === todoId ? { ...todo, isDone: isCompleted } : todo,
            ),
          })),
        };
      });

      return { previousPlanInfo };
    },

    onError: (_error, _variables, context) => {
      // 에러가 발생하면 미리 가져왔던 이전 데이터로 롤백
      if (context?.previousPlanInfo) {
        queryClient.setQueryData([QUERY_KEY.planInfo], context.previousPlanInfo);
      }
    },
  });
};

export default useCheckTodoMutation;
