import Header from '@/components/Header';

const MyPageHeader = () => {
  return (
    <Header>
      <Header.Left>
        <Header.BackButton />
      </Header.Left>
      <Header.Title>마이 페이지</Header.Title>
      <Header.Right>
        <Header.MenuButton />
      </Header.Right>
    </Header>
  );
};

export default MyPageHeader;
