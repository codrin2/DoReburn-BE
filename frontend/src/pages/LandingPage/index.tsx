import CarouselList from './components/CarouselList';
import KakaoLoginButton from './components/KakaoLoginButton';
import LandingHeader from './components/LandingHeader';
import LandingMessage from './components/LandingMessage';
import * as S from './LandingPage.styled';

const LandingPage = () => {
  console.log('배포3');

  return (
    <S.LandingPageLayout>
      <LandingHeader />
      <S.MainContent>
        <LandingMessage />
        <CarouselList />
      </S.MainContent>
      <KakaoLoginButton />
    </S.LandingPageLayout>
  );
};

export default LandingPage;
