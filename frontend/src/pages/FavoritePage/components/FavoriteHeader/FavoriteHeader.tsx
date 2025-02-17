import Header from '@/components/Header';
import useTabContext from '@/hooks/useTabContext';

const FavoriteHeader = () => {
  const { selectedTab } = useTabContext();

  const isFavorite = selectedTab === 'favorite';

  return (
    <Header>
      <Header.Left>
        <Header.BackButton />
      </Header.Left>
      <Header.Title>{isFavorite ? '즐겨찾기 수정하기' : '모든 할 일 추천'}</Header.Title>
    </Header>
  );
};

export default FavoriteHeader;
