import { useEffect } from 'react';

import MyPageAccountInfo from './components/MyPageAccountInfo';
import MyPageEditableSection from './components/MyPageEditableSection';
import MyPageHeader from './components/MyPageHeader';
import MemberProvider from './contexts/MemberProvider';
import { useMember } from './hooks/useMember';
import { useMemberInfoQuery } from './hooks/useMemberInfoQuery';
import * as S from './MyPage.styled';

const MyPageContent = () => {
  const { data: member, isLoading } = useMemberInfoQuery();
  const { setCategories, setHome, setSchool } = useMember();

  useEffect(() => {
    if (member) {
      setCategories(member.categories ?? []);
      setHome({
        homeTitle: member.homeTitle ?? '',
        homeAddress: member.homeAddress ?? '',
        homeAddressX: member.homeAddressX ?? 0,
        homeAddressY: member.homeAddressY ?? 0,
      });
      setSchool({
        schoolTitle: member.schoolTitle ?? '',
        schoolAddress: member.schoolAddress ?? '',
        schoolAddressX: member.schoolAddressX ?? 0,
        schoolAddressY: member.schoolAddressY ?? 0,
      });
    }
  }, [member, setCategories, setHome, setSchool]);

  if (isLoading) return <div>Loading...</div>;

  return (
    <S.MyPageContainer>
      <MyPageHeader />
      <S.MyPageMainContainer>
        <MyPageAccountInfo email={member?.email ?? ''} nickname={member?.nickname ?? ''} />
        <S.Divider />
        <MyPageEditableSection />
        <S.NotificationInfoContainer>
          <S.NotificationInfoTitle>
            <span>푸시 알림 설정을 변경하시겠어요?</span>
          </S.NotificationInfoTitle>
          <S.NotificationInfoText>
            알림을 다시 설정하려면{' '}
            <S.NotificationInfoTextStrong>"요청(기본값)"</S.NotificationInfoTextStrong>으로 변경해
            주세요.
          </S.NotificationInfoText>
        </S.NotificationInfoContainer>
      </S.MyPageMainContainer>
    </S.MyPageContainer>
  );
};

const MyPage = () => {
  return (
    <MemberProvider>
      <MyPageContent />
    </MemberProvider>
  );
};

export default MyPage;
