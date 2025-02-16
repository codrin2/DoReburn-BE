import { useNavigate } from 'react-router';

import Header from '@/components/Header';
import { useOnboarding } from '@/pages/OnboardingPage/hooks/useOnboarding';
import useOnboardingMutation from '@/pages/OnboardingPage/hooks/useOnboardingMutation';
import { useStepValidity } from '@/pages/OnboardingPage/hooks/useStepValidity';
import {
  ONBOARDING_FIRST_STEP,
  ONBOARDING_LAST_STEP,
} from '@/pages/OnboardingPage/OnboardingPage.constants';

const OnboardingHeader = () => {
  const { onboardingStep, setOnboardingStep, onboardingUserInfo } = useOnboarding();
  const navigate = useNavigate();
  const { isCurrentStepValid } = useStepValidity();
  const isButtonDisabled = !isCurrentStepValid(onboardingStep);
  const { mutate: saveOnboarding } = useOnboardingMutation();
  const goToStep = (newStep: number) => {
    setOnboardingStep(newStep);
    navigate(`/onboarding?step=${newStep}`);
  };

  const goToBack = () => {
    if (onboardingStep === ONBOARDING_FIRST_STEP) {
      navigate('/landing');
    } else {
      goToStep(onboardingStep - 1);
    }
  };

  const goToNext = () => {
    if (!isButtonDisabled) {
      goToStep(onboardingStep + 1);
    }
  };

  const goToMain = async () => {
    saveOnboarding(onboardingUserInfo);
  };

  return (
    <Header>
      <Header.Left>
        <Header.BackButton onClick={goToBack} />
      </Header.Left>
      <Header.Center>
        <Header.Title>회원가입</Header.Title>
      </Header.Center>
      <Header.Right>
        {onboardingStep !== ONBOARDING_LAST_STEP ? (
          <Header.NextButton disabled={isButtonDisabled} onClick={goToNext} />
        ) : (
          <Header.CompleteButton disabled={isButtonDisabled} onClick={goToMain} />
        )}
      </Header.Right>
    </Header>
  );
};

export default OnboardingHeader;
