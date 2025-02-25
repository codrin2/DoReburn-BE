import { MAX_TODO_ITEM_LENGTH } from './config';

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
    `${DATE_TEXT[dateType]} 할 일은 최대 ${MAX_TODO_ITEM_LENGTH}개까지 추가할 수 있어요`,
  addFavorite: '즐겨찾기에 추가되었어요',
  deleteFavorite: '즐겨찾기에서 삭제되었어요',
};

export const ERROR_MESSAGE = {
  location: '위치 정보를 가져오는데 실패했어요. 잠시 후 다시 시도해주세요',
  locationPermission: '위치 접근이 거부되었습니다. 설정에서 위치 서비스를 활성화해 주세요.',
  updatePathTodo: '할 일 이동이 실패했어요. 다시 시도해주세요!',
  check: '체크 상태가 반영되지 않았어요. 다시 시도해주세요!',
};
