import { useState } from 'react';

import useSearchAddressQuery from './hooks/useSearchAddressQuery';
import * as S from './SearchAddress.styled';
import { AddressMainProps, AddressOnboardingProps } from './SearchAddress.types';
import SearchAddressHeader from './SearchAddressHeader';
import SearchAddressResult from './SearchAddressResult';
import SearchAddressSearchBar from './SearchAddressSearchBar';
import ShortcutAddress from './ShortcutAddress';

import { SearchAddress as SearchAddressType } from '@/api/search';

interface SearchAddressProps {
  onClose: () => void;
  onSelectAddressMain?: (address: AddressMainProps) => void;
  onSelectAddress?: (address: AddressOnboardingProps) => void;
}

const SearchAddress = ({ onSelectAddressMain, onSelectAddress, onClose }: SearchAddressProps) => {
  const [addressInput, setAddressInput] = useState('');
  const { data: addresses = [], isLoading } = useSearchAddressQuery(addressInput);

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAddressInput(e.target.value);
  };

  const handleSelectAddress = (searchAddress: SearchAddressType) => {
    const { title, roadAddress, x_coordinate, y_coordinate } = searchAddress;

    if (onSelectAddress) {
      onSelectAddress({
        title,
        address: roadAddress,
        coordinateX: x_coordinate,
        coordinateY: y_coordinate,
      });
    } else if (onSelectAddressMain) {
      onSelectAddressMain({
        title: title || roadAddress,
        coordinateX: x_coordinate,
        coordinateY: y_coordinate,
      });
    }

    onClose();
  };

  const handleClose = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
    onClose();
  };

  return (
    <S.SearchAddressLayout>
      <SearchAddressHeader onClick={handleClose} />
      <S.TopContainer>
        <SearchAddressSearchBar query={addressInput} handleSearch={handleSearch} />
        {onSelectAddressMain && (
          <ShortcutAddress onSelectAddressMain={onSelectAddressMain} onClose={onClose} />
        )}
      </S.TopContainer>

      {isLoading ? (
        <div>Loading...</div>
      ) : (
        <SearchAddressResult addresses={addresses} handleSelectAddress={handleSelectAddress} />
      )}
    </S.SearchAddressLayout>
  );
};

export default SearchAddress;
