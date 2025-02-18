import { TodoType } from '@/types/todo';

const DATE_TEXT = {
  TODAY: '오늘',
  TOMORROW: '내일',
  PATH: '',
  SAVE: '',
};

export const TODO_TOAST_MESSAGE = {
  add: (todoType: TodoType) => `${DATE_TEXT[todoType]} 할 일에 추가되었어요`,
  delete: (dateType: 'TODAY' | 'TOMORROW') => `${DATE_TEXT[dateType]} 할 일에서 삭제되었어요`,
  limit: (dateType: 'TODAY' | 'TOMORROW') =>
    `${DATE_TEXT[dateType]} 할 일은 최대 3개까지 추가할 수 있어요`,
  addFavorite: '즐겨찾기에 추가되었어요',
  deleteFavorite: '즐겨찾기에서 삭제되었어요',
};
