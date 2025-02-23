import { useEffect, useRef, useState } from 'react';

import useCheckTodoMutation from './useCheckTodoMutation';

import { PathTodo } from '@/api/plan';
import { ERROR_MESSAGE } from '@/constants/message';
import useToast from '@/hooks/useToast';

const TODO_CHECK_DELAY = 500;

const useCheckTodo = (todo: PathTodo) => {
  const { todoId, isDone } = todo;

  const { mutate: checkTodo } = useCheckTodoMutation();
  const { toast } = useToast();

  const [isAnimatingCheck, setIsAnimatingCheck] = useState(false);
  const checkTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  const handleUncheckTodo = async () => {
    checkTodo(
      { todoId, isCompleted: !isDone },
      {
        onError: () => {
          toast({ message: ERROR_MESSAGE.check });
        },
      },
    );
  };

  const handleCheckTodo = async () => {
    setIsAnimatingCheck(true);
    checkTimeoutRef.current = setTimeout(() => {
      setIsAnimatingCheck(false);
      checkTodo(
        { todoId, isCompleted: !isDone },
        {
          onError: () => {
            toast({ message: ERROR_MESSAGE.check });
          },
        },
      );
    }, TODO_CHECK_DELAY);
  };

  useEffect(() => {
    return () => {
      if (checkTimeoutRef.current) {
        clearTimeout(checkTimeoutRef.current);
      }
    };
  }, []);

  return { handleCheckTodo, handleUncheckTodo, isAnimatingCheck };
};

export default useCheckTodo;
