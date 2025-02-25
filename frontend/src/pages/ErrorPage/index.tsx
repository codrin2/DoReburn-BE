import * as S from './ErrorPage.styled';

import Icon from '@/components/Icon';

interface ErrorPageProps {
  onClick?: () => void;
}

const ErrorPage = ({ onClick }: ErrorPageProps) => {
  const goToHome = () => {
    window.location.href = '/';
  };

  return (
    <S.ErrorLayout>
      <Icon icon="Fire" width={240} height={240} />
      <S.TitleContainer>
        <S.ErrorTitle>서비스에 오류가 발생했어요 :)</S.ErrorTitle>
        <S.ErrorSubTitle>잠시 후 다시 시도해주세요!</S.ErrorSubTitle>
      </S.TitleContainer>
      <S.ErrorButton onClick={onClick || goToHome}>다시 시도</S.ErrorButton>
    </S.ErrorLayout>
  );
};

export default ErrorPage;
