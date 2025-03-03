import { MAX_TODO_ITEM_LENGTH } from './config';

import { ErrorCode } from '@/types/error';
import { TodoType } from '@/types/todo';

const DATE_TEXT = {
  TODAY: '오늘',
  TOMORROW: '내일',
  PATH: '',
  SAVE: '',
};

export const TODO_TOAST_MESSAGE = {
  add: (todoType: TodoType) => `${DATE_TEXT[todoType]} 할 일에 추가되었어요`,
  delete: (todoType: TodoType) => `${DATE_TEXT[todoType]} 할 일에서 삭제되었어요`,
  limit: (dateType: 'TODAY' | 'TOMORROW') =>
    `${DATE_TEXT[dateType]} 할 일은 최대 ${MAX_TODO_ITEM_LENGTH}개까지 추가할 수 있어요`,
  addFavorite: '즐겨찾기에 추가되었어요',
  deleteFavorite: '즐겨찾기에서 삭제되었어요',
  location: '현재 위치를 성공적으로 불러왔습니다!',
  move: '페이지가 이동되었어요.',
};

type clientErrorType = 'LOCATION' | 'LOCATION_PERMISSION' | 'UPDATE_PATH_TODO' | 'CHECK';

export const ERROR_MESSAGE: Record<ErrorCode | clientErrorType, string> = {
  MEMBER_NOT_FOUND: '회원 정보를 찾을 수 없어요. 다시 로그인해 주세요.',
  TODO_NOT_FOUND: '주변 사용자의 할 일을 찾을 수 없어요.',
  SAVE_TODO_NOT_FOUND_FROM_TARGET_PARENT: '해당 할 일이 즐겨찾기에 없습니다.',

  INVALID_MEMBER_STATUS: '현재 상태에서는 이 작업을 수행할 수 없어요.',
  TODO_LIMIT_EXCEEDED: '계획한 할 일의 개수가 너무 많아요!',
  METHOD_ARGUMENT_TYPE_MISMATCH: '잘못된 요청입니다. 입력 값을 확인해 주세요.',
  PATH_ID_NOT_PROVIDED: '경로 정보가 누락되었어요. 다시 시도해 주세요.',

  CATEGORY_NOT_FOUND: '해당 카테고리를 찾을 수 없어요.',
  SCHEDULE_NOT_FOUND: '해당 일정을 찾을 수 없어요.',
  PATH_NOT_FOUND: '해당 경로를 찾을 수 없어요.',

  ALREADY_ADDED_TODO: '이미 추가된 할 일이에요.',
  TODO_TYPE_MISMATCH: '할 일 타입이 올바르지 않아요.',
  MEMBER_CATEGORY_NOT_FOUND: '회원의 카테고리 정보가 없습니다.',
  MEMBER_SAVED_ADDRESS_NOT_FOUND: '저장된 주소를 찾을 수 없어요.',
  UNAUTHORIZED_PLAN_DELETION: '해당 계획을 삭제할 권한이 없어요.',
  NOT_FOUND_PLAN: '계획 정보를 찾을 수 없어요.',
  INVALID_MOOD: '잘못된 기분 형식입니다.',

  TOKEN_EXPIRED: '로그인 세션이 만료되었어요. 다시 로그인해 주세요.',
  TOKEN_INVALID: '유효하지 않은 인증 정보입니다. 다시 로그인해 주세요.',
  TOKEN_BLACKLISTED: '차단된 인증 정보입니다. 관리자에게 문의해주세요.',
  MISSING_TOKEN_IN_COOKIE: '인증 정보가 없어요. 다시 로그인해 주세요.',
  REFRESH_TOKEN_EXPIRED: '로그인 세션이 만료되었어요. 다시 로그인해 주세요.',

  NAVER_SERVICE_UNAVAILABLE: '네이버 서비스가 현재 이용 불가능합니다. 잠시 후 다시 시도해 주세요.',

  // 클라이언트 에러 코드
  LOCATION: '위치 정보를 가져오는데 실패했어요. 잠시 후 다시 시도해주세요.',
  LOCATION_PERMISSION: '위치 접근이 거부되었습니다. 설정에서 위치 서비스를 활성화해 주세요.',
  UPDATE_PATH_TODO: '할 일 이동이 실패했어요. 다시 시도해주세요!',
  CHECK: '체크 상태가 반영되지 않았어요. 다시 시도해주세요!',
};
