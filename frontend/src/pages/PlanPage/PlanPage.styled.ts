import styled from 'styled-components';

export const PlanPageLayout = styled.section`
  position: relative;
  height: inherit;

  display: flex;
  flex-direction: column;
  gap: 2.4rem;
  overflow-y: scroll;
`;

export const HorizontalLine = styled.div`
  width: 100%;
  height: 1.2rem;
  background-color: ${({ theme }) => theme.colors.gray50};
`;

export const FinishButton = styled.button`
  position: fixed;
  bottom: 4rem;
  left: 50%;
  transform: translateX(-50%);

  display: flex;
  padding: 1.5rem 3rem;
  gap: 0.4rem;
  border-radius: 3.2rem;

  ${({ theme }) => theme.fonts.body16};
  background-color: ${({ theme }) => theme.colors.green600};
  color: ${({ theme }) => theme.colors.white};
`;
