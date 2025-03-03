import * as S from './CategoryRankItem.styled';

import { CategoryRank } from '@/api/map';
import { CATEGORY_MAPPER } from '@/constants/config';

interface CategoryRankItemProps {
  rankItem: CategoryRank;
}

const CategoryRankItem = ({ rankItem }: CategoryRankItemProps) => {
  return (
    <S.CategoryRankItemLayout>
      <S.RankWrapper>
        <span>{rankItem.rank}</span>
        <S.CategoryBadge $category={rankItem.category}>
          {`#${CATEGORY_MAPPER[rankItem.category]}`}
        </S.CategoryBadge>
      </S.RankWrapper>
      <span>{rankItem.count}명</span>
    </S.CategoryRankItemLayout>
  );
};

export default CategoryRankItem;
