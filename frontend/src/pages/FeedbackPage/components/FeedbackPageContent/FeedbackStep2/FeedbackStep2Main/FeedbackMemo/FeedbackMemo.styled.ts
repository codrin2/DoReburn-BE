import styled from 'styled-components';

export const FeedbackMemoLayout = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  padding: 1.6rem;
  width: 85%;
  background-color: ${({ theme }) => `${theme.colors.green100}4D`};
  border-radius: 2.4rem;
`;

export const MemoTitleLayout = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const Title = styled.div`
  ${({ theme }) => theme.fonts.body15};
  color: ${({ theme }) => theme.colors.gray600};
`;

export const MemoLength = styled.div`
  ${({ theme }) => theme.fonts.label13};
  color: ${({ theme }) => theme.colors.gray600};
`;

export const MemoInput = styled.input`
  ${({ theme }) => theme.fonts.body15};
  color: ${({ theme }) => theme.colors.gray900};
  padding: 1.2rem;
  border-radius: 1.2rem;
`;
