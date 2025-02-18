import TodoShowForm from '../components/TimeBlockContent/TimeBlockList/TimeBlockItem/TodoShowForm';

import useOverlay from '@/hooks/useOverlay';
import { Todo } from '@/types/todo';

const useShowTodoBottomSheet = () => {
  const overlay = useOverlay();

  const showTodoBottomSheet = (todo: Todo) => {
    overlay.open(() => <TodoShowForm todo={todo} />, { title: '할 일 보기' });
  };

  return showTodoBottomSheet;
};

export default useShowTodoBottomSheet;
