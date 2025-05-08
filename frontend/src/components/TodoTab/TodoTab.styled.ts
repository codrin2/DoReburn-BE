import styled from 'styled-components';

export const TodoTabLayout = styled.div`
  background-color: ${({ theme }) => theme.colors.white};
`;

export const SloganWrapper = styled.div`
  padding: 1.6rem 2.4rem;
  border-bottom: 0.15rem solid ${({ theme }) => theme.colors.gray100};
`;

export const Slogan = styled.p`
  display: flex;
  flex-direction: column;
  align-items: center;

  padding: 1.2rem 0;
  border-radius: 0.8rem;

  background-color: ${({ theme }) => theme.colors.gray50};
  white-space: pre-wrap;
  text-align: center;

  ${({ theme }) => theme.fonts.label14Med};
  color: ${({ theme }) => theme.colors.gray900};
`;

export const TodoEditList = styled.ul`
  display: flex;
  flex-direction: column;

  border-collapse: separate;
  border-bottom: 0.15rem solid ${({ theme }) => theme.colors.gray100};
`;
