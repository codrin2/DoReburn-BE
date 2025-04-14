import * as S from './ErrorPage.styled';

import Icon from '@/components/Icon';

interface ErrorPageProps {
  message?: string;
  onClick?: () => void;
}

const ErrorPage = ({ message, onClick }: ErrorPageProps) => {
  const goToHome = () => {
    window.location.href = '/';
  };

  console.log('배포테스트1');

  return (
    <S.ErrorLayout>
      <Icon icon="Fire" width={240} height={240} />
      <S.TitleContainer>
        {message ? (
          <S.ErrorSubTitle>{message}</S.ErrorSubTitle>
        ) : (
          <>
            <S.ErrorTitle>서비스에 오류가 발생했어요 :)</S.ErrorTitle>
            <S.ErrorSubTitle>잠시 후 다시 시도해주세요!</S.ErrorSubTitle>
          </>
        )}
      </S.TitleContainer>
      <S.ButtonContainer>
        <S.ErrorButton onClick={onClick}>다시 시도</S.ErrorButton>
        <S.ErrorButton onClick={goToHome}>홈으로</S.ErrorButton>
      </S.ButtonContainer>
    </S.ErrorLayout>
  );
};

export default ErrorPage;
