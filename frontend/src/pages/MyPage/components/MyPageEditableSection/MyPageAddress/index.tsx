import { useState } from 'react';

import * as S from './MyPageAddress.styled';

import Icon from '@/components/Icon';
import SearchAddress from '@/components/SearchAddress';
import { useMember } from '@/pages/MyPage/hooks/useMember';

const GUIDE_MESSAGE = '주소를 변경할 수 있어요';

const MyPageAddress = ({ editMode }: { editMode: boolean }) => {
  const { memberInfo, setHome, setSchool } = useMember();
  const { homeTitle, schoolTitle } = memberInfo;
  const [isSearchAddressOpen, setIsSearchAddressOpen] = useState(false);
  const [selectedAddressType, setSelectedAddressType] = useState<'home' | 'school' | null>(null);

  const handleAddressClick = (type: 'home' | 'school') => {
    setSelectedAddressType(type);
    setIsSearchAddressOpen(true);
  };

  const updateAddress = ({
    title,
    address,
    coordinateX,
    coordinateY,
  }: {
    title: string;
    address: string;
    coordinateX: number;
    coordinateY: number;
  }) => {
    if (selectedAddressType === 'home') {
      setHome({
        homeTitle: title || address,
        homeAddress: address,
        homeAddressX: coordinateX,
        homeAddressY: coordinateY,
      });
    } else if (selectedAddressType === 'school') {
      setSchool({
        schoolTitle: title || address,
        schoolAddress: address,
        schoolAddressX: coordinateX,
        schoolAddressY: coordinateY,
      });
    }
  };

  return (
    <S.AddressContainer>
      <S.TitleContainer>
        <S.TitleText>기본 주소</S.TitleText>
        {editMode && <S.TitleMessage>{GUIDE_MESSAGE}</S.TitleMessage>}
      </S.TitleContainer>
      <S.AddressList>
        <S.AddressItem disabled={!editMode} onClick={() => handleAddressClick('home')}>
          <S.AddressLabel>
            <Icon icon="AddressHome" />
            <span>집</span>
          </S.AddressLabel>
          <S.AddressTitle>{homeTitle}</S.AddressTitle>
        </S.AddressItem>
        <S.AddressItem disabled={!editMode} onClick={() => handleAddressClick('school')}>
          <S.AddressLabel>
            <Icon icon="AddressUniv" />
            <span>학교</span>
          </S.AddressLabel>
          <S.AddressTitle>{schoolTitle}</S.AddressTitle>
        </S.AddressItem>
      </S.AddressList>
      {isSearchAddressOpen && (
        <SearchAddress
          onClose={() => setIsSearchAddressOpen(false)}
          onSelectAddress={updateAddress}
        />
      )}
    </S.AddressContainer>
  );
};

export default MyPageAddress;
