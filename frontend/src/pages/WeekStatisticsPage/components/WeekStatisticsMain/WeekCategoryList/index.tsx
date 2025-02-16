import WeekCategoryItem from './WeekCategoryItem';
import * as S from './WeekCategoryList.styled';

import { CategoryType } from '@/types/filter';

interface WeekTodoListProps {
  categoryRanking: {
    category: CategoryType;
    usageTime: number;
    count: number;
  }[];
}

const WeekCategoryList = ({ categoryRanking }: WeekTodoListProps) => {
  return (
    <S.WeekCategoryListContainer>
      {categoryRanking.map((item, index) => (
        <WeekCategoryItem key={item.category} {...item} index={index + 1} />
      ))}
    </S.WeekCategoryListContainer>
  );
};

export default WeekCategoryList;
