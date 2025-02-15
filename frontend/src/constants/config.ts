export const MAP_ID = 'map';
export const MAX_TODO_ITEM_LENGTH = 3;

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
  today: 'TODAY',
  tomorrow: 'TOMORROW',
  route: 'PATH',
} as const;

export const CATEGORY_LABEL_MAPPER = {
  READING: '독서',
  ENGLISH: '영어',
  LANGUAGE: '제2외국어',
  NEWS: '뉴스/시사',
  HOBBY: '취미',
  OTHERS: '기타',
} as const;
