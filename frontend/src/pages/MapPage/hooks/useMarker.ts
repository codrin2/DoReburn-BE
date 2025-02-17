import { useRef } from 'react';

import { NearbyMember } from '@/api/map';
import Marker from '@/assets/images/marker.svg';
import { CategoryType } from '@/types/filter';
import { createMarkerImage } from '@/utils/map';

const useMarker = () => {
  const markerListRef = useRef<kakao.maps.Marker[]>([]);

  const addMarker = (marker: kakao.maps.Marker) => {
    markerListRef.current.push(marker);
  };

  const clearMarkerList = () => {
    markerListRef.current.forEach((marker) => {
      marker.setMap(null);
    });

    markerListRef.current = [];
  };

  const putMarker = ({
    map,
    coord,
    onClick,
    category,
  }: {
    map: kakao.maps.Map | null;
    coord: NearbyMember;
    onClick: () => void;
    category: CategoryType | null;
  }) => {
    const markerImage = createMarkerImage(Marker, 40, 48, category);

    const marker = new kakao.maps.Marker({
      position: new kakao.maps.LatLng(coord.y_coordinate, coord.x_coordinate),
      image: markerImage,
    });

    addMarker(marker);

    // 마커 클릭 이벤트 등록
    kakao.maps.event.addListener(marker, 'click', onClick);

    marker.setMap(map);
  };

  const putMarkerList = ({
    map,
    onClick,
    category,
    nearbyMemberList,
  }: {
    map: kakao.maps.Map | null;
    onClick: (memberId: number) => void;
    category: CategoryType | null;
    nearbyMemberList?: NearbyMember[];
  }) => {
    clearMarkerList();
    nearbyMemberList?.forEach((nearByMember) => {
      putMarker({
        map,
        coord: nearByMember,
        onClick: () => onClick(nearByMember.memberId),
        category,
      });
    });
  };

  return { clearMarkerList, putMarker, putMarkerList };
};

export default useMarker;
