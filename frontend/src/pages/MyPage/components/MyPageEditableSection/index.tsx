import { useState } from 'react';

import MyPageAddress from './MyPageAddress';
import * as S from './MyPageEditableSection.styled';
import MyPageGoal from './MyPageGoal';

import Icon from '@/components/Icon';
import { useMember } from '@/pages/MyPage/hooks/useMember';
import useMemberInfoMutation from '@/pages/MyPage/hooks/useMemberInfoMutation';
import { useMemberInfoQuery } from '@/pages/MyPage/hooks/useMemberInfoQuery';
import theme from '@/styles/theme';

const MyPageEditableSection = () => {
  const [editMode, setEditMode] = useState(false);
  const { memberInfo, setCategories, setHome, setSchool } = useMember();
  const { data: member } = useMemberInfoQuery();
  const { mutate: updateMember } = useMemberInfoMutation();

  const handleCancel = () => {
    setEditMode(false);
    setCategories(member?.categories ?? []);
    setHome({
      homeTitle: member?.homeTitle ?? '',
      homeAddress: member?.homeAddress ?? '',
      homeAddressX: member?.homeAddressX ?? 0,
      homeAddressY: member?.homeAddressY ?? 0,
    });
    setSchool({
      schoolTitle: member?.schoolTitle ?? '',
      schoolAddress: member?.schoolAddress ?? '',
      schoolAddressX: member?.schoolAddressX ?? 0,
      schoolAddressY: member?.schoolAddressY ?? 0,
    });
  };

  const handleSave = () => {
    updateMember(memberInfo);
    setEditMode(false);
  };

  return (
    <S.MyPageEditableSectionContainer>
      <MyPageGoal editMode={editMode} />
      <MyPageAddress editMode={editMode} />
      {editMode ? (
        <S.ButtonContainer>
          <S.CancelButton onClick={handleCancel}>
            <span>취소</span>
          </S.CancelButton>
          <S.ConfirmButton disabled={memberInfo.categories.length === 0} onClick={handleSave}>
            <span>저장</span>
          </S.ConfirmButton>
        </S.ButtonContainer>
      ) : (
        <S.EditButton onClick={() => setEditMode(true)}>
          <Icon icon="Pen" width={16} height={16} color={theme.colors.gray500} />
          <span>수정하기</span>
        </S.EditButton>
      )}
    </S.MyPageEditableSectionContainer>
  );
};

export default MyPageEditableSection;
