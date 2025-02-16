import * as S from './CategoryBadge.styled';

import { CATEGORY_LABEL_MAPPER } from '@/constants/config';
import { CategoryType } from '@/types/filter';

interface CategoryBadgeProps {
  category: CategoryType;
}

const CategoryBadge = ({ category }: CategoryBadgeProps) => {
  return <S.CategoryBadge $category={category}>#{CATEGORY_LABEL_MAPPER[category]}</S.CategoryBadge>;
};

export default CategoryBadge;
