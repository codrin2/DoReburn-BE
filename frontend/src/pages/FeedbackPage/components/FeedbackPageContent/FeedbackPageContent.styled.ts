import styled from 'styled-components';

import BackgroundFeedback from '@/assets/images/backgroundFeedback.png';

export const FeedbackPageLayout = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 100%;
  background-image: url(${BackgroundFeedback});
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;

  padding-bottom: 9.6rem;
  touch-action: none;
`;

export const ButtonBox = styled.button`
  display: flex;
  align-items: center;

  position: absolute;
  bottom: 1.6rem;

  color: ${({ theme }) => theme.colors.white};
  background-color: ${({ theme }) => theme.colors.green600};
  padding: 1.6rem 3rem;
  border-radius: 3.2rem;
  ${({ theme }) => theme.fonts.body16};
  gap: 1rem;
`;
