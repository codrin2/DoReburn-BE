export const MAP_ID = 'map';
export const MAX_TODO_ITEM_LENGTH = 5;

export const ICON_MAPPER = {
  READING: 'Reading',
  ENGLISH: 'English',
  LANGUAGE: 'Language',
  NEWS: 'News',
  HOBBY: 'Hobby',
  OTHERS: 'Others',
} as const;

export const USER_STATUS = {
  stop: 'STOP',
  move: 'MOVE',
  feedback: 'FEEDBACK',
} as const;

export const TODO_TYPE = {
  TODAY: 'TODAY',
  TOMORROW: 'TOMORROW',
  FAVORITE: 'FAVORITE',
  PATH: 'PATH',
} as const;

export const DATE_TYPE = {
  TODAY: 'TODAY',
  TOMORROW: 'TOMORROW',
} as const;

export const CATEGORY_LABEL_MAPPER = {
  READING: '독서',
  ENGLISH: '영어',
  LANGUAGE: '제2외국어',
  NEWS: '뉴스/시사',
  HOBBY: '취미',
  OTHERS: '기타',
} as const;

export const CATEGORY_OPTIONS = [
  {
    label: '독서',
    value: 'READING',
  },
  {
    label: '영어',
    value: 'ENGLISH',
  },
  {
    label: '제2외국어',
    value: 'LANGUAGE',
  },
  {
    label: '뉴스/시사',
    value: 'NEWS',
  },
  {
    label: '취미',
    value: 'HOBBY',
  },
  {
    label: '기타',
    value: 'OTHERS',
  },
] as const;

export const DIFFICULTY_OPTIONS = [
  {
    label: '쉬움',
    value: 'EASY',
  },
  {
    label: '보통',
    value: 'NORMAL',
  },
  {
    label: '어려움',
    value: 'HARD',
  },
] as const;

export const CATEGORY_MAPPER = {
  READING: '독서',
  ENGLISH: '영어',
  LANGUAGE: '제2외국어',
  NEWS: '뉴스/시사',
  HOBBY: '취미',
  OTHERS: '기타',
} as const;

export const TABS = [
  {
    label: '할 일',
    value: 'todo',
  },
  {
    label: '즐겨찾기',
    value: 'favorite',
  },
  {
    label: '추천',
    value: 'recommend',
  },
] as const;
