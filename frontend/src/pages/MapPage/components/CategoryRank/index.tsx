import * as S from './CategoryRank.styled';
import CategoryRankItem from './CategoryRankItem';
import useNearbyUsersQuery from '../../hooks/useNearbyUsersQuery';

import Icon from '@/components/Icon';

const CATEGORY_RANK_TEXT = {
  title: '내 주변 통학생들은\n다음 목표를 수행하고 있어요',
  slogan: '지도의 마커를 누르고\n다른 사람들의 할 일을 즐겨찾기에 추가해보세요',
  empty: '주변에 통학생이 없네요.\n잠시 후 다시 이용해주세요:)',
} as const;

interface CategoryRankProps {
  lng: number;
  lat: number;
}

const CategoryRank = ({ lng, lat }: CategoryRankProps) => {
  const { data: nearbyUsersData, refetch } = useNearbyUsersQuery({ lng, lat });

  const categoryRankList = nearbyUsersData?.categoryRank;
  const isEmptyNearbyUsers = nearbyUsersData === null;

  return (
    <S.CategoryRankLayout>
      <S.CategoryRankHeader>
        <S.HeaderWrapper>
          <S.HeaderTitle>
            {isEmptyNearbyUsers ? CATEGORY_RANK_TEXT.empty : CATEGORY_RANK_TEXT.title}
          </S.HeaderTitle>
          {isEmptyNearbyUsers && (
            <S.IconButtonWrapper
              icon={<Icon icon="Reload" cursor="pointer" />}
              onClick={() => refetch()}
            />
          )}
        </S.HeaderWrapper>
        <S.SloganWrapper>{CATEGORY_RANK_TEXT.slogan}</S.SloganWrapper>
      </S.CategoryRankHeader>

      {/* 카테고리 순위 */}
      <S.CategoryRankList>
        {categoryRankList?.map((rankItem, idx) => (
          <CategoryRankItem key={idx} rankItem={rankItem} />
        ))}
      </S.CategoryRankList>
    </S.CategoryRankLayout>
  );
};

export default CategoryRank;
