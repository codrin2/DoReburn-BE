import backgroundEnglish from '@/assets/images/backgroundEnglish.webp';
import backgroundHobby from '@/assets/images/backgroundHobby.webp';
import backgroundLanguage from '@/assets/images/backgroundLanguage.webp';
import backgroundNews from '@/assets/images/backgroundNews.webp';
import backgroundReading from '@/assets/images/backgroundReading.webp';

export const CAGETORY_BACKGROUNDS_IMG = {
  READING: backgroundReading,
  ENGLISH: backgroundEnglish,
  HOBBY: backgroundHobby,
  LANGUAGE: backgroundLanguage,
  NEWS: backgroundNews,
};

export const ONBOARDING_CATEGORY_MIN_NUM = 1;
export const ONBOARDING_CATEGORY_MAX_NUM = 3;
export const ONBOARDING_CATEGORY_DIVISION_NUM = 3;

export const ONBOARDING_CATEGORIES = [
  { label: 'READING', value: '독서' },
  { label: 'ENGLISH', value: '영어' },
  { label: 'HOBBY', value: '취미' },
  { label: 'LANGUAGE', value: '제2외국어' },
  { label: 'NEWS', value: '뉴스/시사' },
];
