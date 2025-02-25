import * as S from './ErrorPage.styled';

import Icon from '@/components/Icon';

const RouteErrorPage = () => {
  const goToHome = () => {
    window.location.href = '/';
  };

  return (
    <S.ErrorLayout>
      <Icon icon="Fire" width={240} height={240} />
      <S.TitleContainer>
        <S.ErrorTitle>잘못된 경로에 접근했어요:)</S.ErrorTitle>
        <S.ErrorSubTitle>메인 화면으로 이동해주세요!</S.ErrorSubTitle>
      </S.TitleContainer>
      <S.ErrorButton onClick={goToHome}>홈으로</S.ErrorButton>
    </S.ErrorLayout>
  );
};

export default RouteErrorPage;
