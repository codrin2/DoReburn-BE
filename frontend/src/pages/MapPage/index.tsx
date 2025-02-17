import { CATEGORY_OPTIONS } from '../EditPage/EditPage.constants';
import CategoryRank from './components/CategoryRank';
import useCategoryFilter from './hooks/useCategoryFilter';
import useInitMap from './hooks/useInitMap';
import useMapBottomSheet from './hooks/useMapBottomSheet';
import useMarker from './hooks/useMarker';
import useMarkerBottomSheet from './hooks/useMarkerBottomSheet';
import useNearbyUsersQuery from './hooks/useNearbyUsersQuery';
import useUpdateCurrentLocation from './hooks/useUpdateCurrentLocation';
import * as S from './MapPage.styled';

import BottomSheet from '@/components/BottomSheet';
import Header from '@/components/Header';
import Icon from '@/components/Icon';
import RadioGroup from '@/components/RadioGroup';
import { MAP_ID } from '@/constants/config';
import { colors } from '@/styles/theme';

const MapPage = () => {
  const { updateCurrentLocation } = useUpdateCurrentLocation();

  const { mapRef, center, isDragged, handleDragEnd } = useInitMap();
  const { putMarkerList } = useMarker();
  const { isOpen, close } = useMapBottomSheet();

  const { data: nearbyUsersData } = useNearbyUsersQuery({
    lng: center.lng,
    lat: center.lat,
  });

  const { category, handleSelectCategoryFilter, filteredLocations } = useCategoryFilter(
    nearbyUsersData?.memberLocations ?? [],
  );

  const {
    isOpen: isMarkerBottomSheetOpen,
    open: openMarkerBottomSheet,
    close: closeMarkerBottomSheet,
    content: markerBottomSheetContent,
  } = useMarkerBottomSheet();

  const handleBackCenter = () => {
    mapRef.current?.setCenter(new kakao.maps.LatLng(center.lat, center.lng));

    handleDragEnd();
  };

  const handleReload = async () => {
    await updateCurrentLocation({
      x_coordinate: center.lng,
      y_coordinate: center.lat,
    });
  };

  putMarkerList({
    map: mapRef.current,
    onClick: openMarkerBottomSheet,
    category,
    nearbyMemberList: filteredLocations,
  });

  return (
    <S.MapContainer id={MAP_ID}>
      <S.HeaderOverlay>
        <Header>
          <Header.Left>
            <Header.BackButton />
          </Header.Left>
          <Header.Right>
            <Header.MenuButton />
          </Header.Right>
        </Header>

        <S.FilterBadgeWrapper>
          <RadioGroup
            type="checkbox"
            name="category"
            filters={CATEGORY_OPTIONS}
            handleChange={handleSelectCategoryFilter}
            selectedValue={category ?? ''}
            category={category}
            width="100%"
            isFilter
          />
        </S.FilterBadgeWrapper>
      </S.HeaderOverlay>

      <BottomSheet
        isOpen={isOpen}
        onClose={close}
        content={<CategoryRank lng={center.lng} lat={center.lat} />}
        subTitle="반경 3km 이내"
      />

      <BottomSheet
        isOpen={isMarkerBottomSheetOpen}
        onClose={closeMarkerBottomSheet}
        content={markerBottomSheetContent}
      />

      <S.FloatingButtonContainer>
        {/* 주변 사용자 갱신 */}
        <S.ReloadButton
          icon={<Icon icon="Reload" width={28} height={28} cursor="pointer" />}
          onClick={handleReload}
        />

        {/* 현재 위치로 이동 버튼 */}
        <S.CurrentLocationButton
          icon={
            <Icon
              icon="Target"
              width={32}
              height={32}
              cursor="pointer"
              color={isDragged ? colors.iconBlue : colors.gray400}
            />
          }
          onClick={handleBackCenter}
        />
      </S.FloatingButtonContainer>
    </S.MapContainer>
  );
};

export default MapPage;
