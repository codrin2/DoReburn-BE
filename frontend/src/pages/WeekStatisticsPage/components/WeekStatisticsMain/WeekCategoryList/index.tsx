import WeekCategoryItem from './WeekCategoryItem';
import * as S from './WeekCategoryList.styled';

interface WeekTodoListProps {
  categoryRanking: {
    category: string;
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
