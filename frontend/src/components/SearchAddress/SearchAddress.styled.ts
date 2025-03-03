import styled from 'styled-components';

export const SearchAddressLayout = styled.div`
  background-color: ${({ theme }) => theme.colors.green50};
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const TopContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  gap: 2rem;
  padding: 2rem 3.6rem 4rem 3.6rem;
`;
