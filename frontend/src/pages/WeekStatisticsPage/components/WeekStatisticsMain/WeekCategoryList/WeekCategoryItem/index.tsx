import * as S from './WeekCategoryItem.styled';

import CategoryBadge from '@/components/CategoryBadge';
import { CategoryType } from '@/types/filter';

interface WeekTodoItemProps {
  index: number;
  usageTime: number;
  category: CategoryType;
  count: number;
}

const WeekCategoryItem = ({ index, usageTime, category, count }: WeekTodoItemProps) => {
  return (
    <S.WeekCategoryItemContainer>
      <S.CategoryLeftContainer>
        <S.CategoryIndex>{index}</S.CategoryIndex>
        <S.CategoryTimeContainer>
          <S.CategoryTime>{usageTime}분</S.CategoryTime>
          <CategoryBadge category={category} />
        </S.CategoryTimeContainer>
      </S.CategoryLeftContainer>
      <S.CategoryCount>{count}개</S.CategoryCount>
    </S.WeekCategoryItemContainer>
  );
};

export default WeekCategoryItem;
