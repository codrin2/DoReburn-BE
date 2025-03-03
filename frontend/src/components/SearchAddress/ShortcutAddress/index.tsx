import * as S from './ShortcutAddress.styled';
import { AddressMainProps } from '../SearchAddress.types';

import Icon from '@/components/Icon';
import useMemberAddressQuery from '@/pages/MainPage/hooks/useMemberAddressQuery';
import { colors } from '@/styles/theme';

interface ShortcutAddressProps {
  onSelectAddressMain: (address: AddressMainProps) => void;
  onClose: () => void;
}

const ShortcutAddress = ({ onSelectAddressMain, onClose }: ShortcutAddressProps) => {
  const { data: memberInfo } = useMemberAddressQuery();

  const handleSelectHome = () => {
    onSelectAddressMain({
      title: memberInfo?.homeTitle || '',
      coordinateX: memberInfo?.homeXCoordinate || 0,
      coordinateY: memberInfo?.homeYCoordinate || 0,
    });

    onClose();
  };

  const handleSelectSchool = () => {
    if (onSelectAddressMain) {
      onSelectAddressMain({
        title: memberInfo?.schoolTitle || '',
        coordinateX: memberInfo?.schoolXCoordinate || 0,
        coordinateY: memberInfo?.schoolYCoordinate || 0,
      });
    }

    onClose();
  };

  return (
    <S.ShortcutButtonContainer>
      <S.IconButtonWrapper
        icon={
          <Icon
            icon="AddressHome"
            width={20}
            height={20}
            color={colors.green300}
            cursor="pointer"
          />
        }
        onClick={handleSelectHome}
        text="집"
      />
      <S.IconButtonWrapper
        icon={
          <Icon
            icon="AddressUniv"
            width={20}
            height={20}
            color={colors.green300}
            cursor="pointer"
          />
        }
        color={colors.green300}
        onClick={handleSelectSchool}
        text="학교"
      />
    </S.ShortcutButtonContainer>
  );
};

export default ShortcutAddress;
