import * as S from './MyPageAccountInfo.styled';

interface MyPageAccountInfoProps {
  email: string;
  nickname: string;
}

const MyPageAccountInfo = ({ email, nickname }: MyPageAccountInfoProps) => {
  return (
    <S.AccountInfoContainer>
      <S.AccountInfoItem>
        <S.AccountInfoLabel>계정</S.AccountInfoLabel>
        <S.AccountInfoValue>{email}</S.AccountInfoValue>
      </S.AccountInfoItem>
      <S.AccountInfoItem>
        <S.AccountInfoLabel>닉네임</S.AccountInfoLabel>
        <S.AccountInfoValue>{nickname}</S.AccountInfoValue>
      </S.AccountInfoItem>
    </S.AccountInfoContainer>
  );
};

export default MyPageAccountInfo;
