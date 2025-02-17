import { useRef } from 'react';

import { NearbyMember } from '@/api/map';
import Marker from '@/assets/images/marker.svg';
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

  const putMarker = (map: kakao.maps.Map | null, coord: NearbyMember, onClick: () => void) => {
    const markerImage = createMarkerImage(Marker, 40, 48);

    const marker = new kakao.maps.Marker({
      position: new kakao.maps.LatLng(coord.y_coordinate, coord.x_coordinate),
      image: markerImage,
    });

    addMarker(marker);

    // 마커 클릭 이벤트 등록
    kakao.maps.event.addListener(marker, 'click', onClick);

    marker.setMap(map);
  };

  const putMarkerList = (
    map: kakao.maps.Map | null,
    onClick: (memberId: number) => void,
    nearbyMemberList?: NearbyMember[],
  ) => {
    nearbyMemberList?.forEach((nearByMember) => {
      putMarker(map, nearByMember, () => onClick(nearByMember.memberId));
    });
  };

  return { clearMarkerList, putMarker, putMarkerList };
};

export default useMarker;
