import { MAX_TODO_ITEM_LENGTH } from '@/constants/config';

export const TODO_TAB_TEXT = {
  TODAY: `오늘 할 일, 작은 목표로 시작해봐요\n최대 ${MAX_TODO_ITEM_LENGTH}개까지 고를 수 있어요`,
  TOMORROW: `내일 할 일, 작은 목표로 시작해봐요\n최대 ${MAX_TODO_ITEM_LENGTH}개까지 고를 수 있어요`,
  PATH: '이 구간에서 할 일을 골라보세요',
  SAVE: '즐겨찾기에 추가할 할 일을 골라보세요',
} as const;
