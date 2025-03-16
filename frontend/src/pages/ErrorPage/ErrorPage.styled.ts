import styled from 'styled-components';

export const ErrorLayout = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  gap: 3.2rem;
  background-color: ${({ theme }) => theme.colors.lightGreen100};
`;

export const TitleContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
`;

export const ErrorTitle = styled.h1`
  ${({ theme }) => theme.fonts.heading22}
`;

export const ErrorSubTitle = styled.h2`
  ${({ theme }) => theme.fonts.headline17}
  word-break: keep-all;
  max-width: 36rem;
  text-align: center;
`;
export const ButtonContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;
`;

export const ErrorButton = styled.button`
  ${({ theme }) => theme.fonts.body16};
  background-color: ${({ theme }) => theme.colors.green600};
  color: ${({ theme }) => theme.colors.white};

  display: flex;
  justify-content: center;
  padding: 1.5rem 3rem;
  gap: 0.4rem;
  border-radius: 3.2rem;
`;
