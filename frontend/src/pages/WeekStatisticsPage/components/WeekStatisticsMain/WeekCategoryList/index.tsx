import WeekCategoryItem from './WeekCategoryItem';
import * as S from './WeekCategoryList.styled';

interface WeekTodoListProps {
  categoryRanking: {
    category: string;
    time: number;
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
