import * as S from './DayCategoryAchievement.styled';

import CategoryBadge from '@/components/CategoryBadge';
import { CategoryType } from '@/types/filter';

interface DayCategoryAchievementProps {
  categories: {
    category: string;
    count: number;
  }[];
}

const DayCategoryAchievement = ({ categories }: DayCategoryAchievementProps) => {
  return (
    <S.DayCategoryAchievementContainer>
      <S.DayCategoryAchievementTitle>목표별 수행 개수</S.DayCategoryAchievementTitle>
      <S.CategoryList>
        {categories.map((category, index) => (
          <S.CategoryItem key={index}>
            <CategoryBadge category={category.category as CategoryType} />
            <S.CategoryCount>{category.count}개</S.CategoryCount>
          </S.CategoryItem>
        ))}
      </S.CategoryList>
    </S.DayCategoryAchievementContainer>
  );
};

export default DayCategoryAchievement;
