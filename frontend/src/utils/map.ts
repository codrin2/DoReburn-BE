import EnglishMarker from '@/assets/images/marker-english.svg';
import HobbyMarker from '@/assets/images/marker-hobby.svg';
import LanguageMarker from '@/assets/images/marker-language.svg';
import NewsMarker from '@/assets/images/marker-news.svg';
import OthersMarker from '@/assets/images/marker-others.svg';
import ReadingMarker from '@/assets/images/marker-reading.svg';
import { CategoryType } from '@/types/filter';

const categoryMarker = {
  HOBBY: HobbyMarker,
  ENGLISH: EnglishMarker,
  READING: ReadingMarker,
  NEWS: NewsMarker,
  LANGUAGE: LanguageMarker,
  OTHERS: OthersMarker,
};
export const createMarkerImage = (
  src: string,
  width: number,
  height: number,
  category: CategoryType | null,
) => {
  const markerSrc = category ? categoryMarker[category] : src;
  const markerSize = new window.kakao.maps.Size(width, height);
  const markerOption = { offset: new window.kakao.maps.Point(0, 0) };

  return new window.kakao.maps.MarkerImage(markerSrc, markerSize, markerOption);
};
