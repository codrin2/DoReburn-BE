import { useEffect, useRef, useState } from 'react';

import CurrentLocationMarker from '@/assets/images/currentLocationMarker.svg';
import { MAP_ID } from '@/constants/config';
import { ERROR_MESSAGE } from '@/constants/message';
import useToast from '@/hooks/useToast';
import { createMarkerImage } from '@/utils/map';

const INIT_CENTER = {
  lat: 37.468665,
  lng: 127.042446,
};

const useInitMap = () => {
  const [center, setCenter] = useState(INIT_CENTER);

  const navigatorRef = useRef<number | null>(null);
  const mapRef = useRef<kakao.maps.Map | null>(null);
  const currentLocationRef = useRef<kakao.maps.Marker | null>(null);
  const [isDragged, setIsDragged] = useState(false);
  const { toast } = useToast();

  const handleDragStart = () => {
    setIsDragged(true);
  };

  const handleDragEnd = () => {
    setIsDragged(false);
  };

  useEffect(() => {
    const locPosition = new kakao.maps.LatLng(center.lat, center.lng);
    mapRef.current?.setCenter(locPosition);

    const currentLocationImage = createMarkerImage(CurrentLocationMarker, 28, 28, null);

    if (mapRef.current) {
      if (currentLocationRef.current) {
        // 기존 마커가 있으면 위치만 업데이트
        currentLocationRef.current.setPosition(locPosition);
      } else {
        // 마커가 없으면 새로 생성
        currentLocationRef.current = new window.kakao.maps.Marker({
          map: mapRef.current,
          image: currentLocationImage,
          position: locPosition,
          zIndex: 1,
        });
      }
    }
  }, [center]);

  useEffect(() => {
    const initMap = () => {
      if (mapRef.current) return;

      const container = document.getElementById(MAP_ID) as HTMLElement;
      const options = {
        center: new kakao.maps.LatLng(INIT_CENTER.lat, INIT_CENTER.lng),
        level: 5,
        draggable: true,
        scrollwheel: true,
      };

      mapRef.current = new kakao.maps.Map(container, options);

      if (navigator.geolocation) {
        navigatorRef.current = navigator.geolocation.watchPosition(
          (pos) => {
            setCenter({ lat: pos.coords.latitude, lng: pos.coords.longitude });
          },
          (error) => {
            const isChangedCenter =
              center.lat !== INIT_CENTER.lat || center.lng !== INIT_CENTER.lng;

            if (error.code === error.PERMISSION_DENIED) {
              // 권한이 없는 경우 홈으로 라우팅
              const isConfirm = confirm(ERROR_MESSAGE.locationPermission);

              if (isConfirm) {
                window.location.href = '/';
              }
            } else if (error.code === error.TIMEOUT) {
              // 한번이라도 값을 받아온 경우 토스트 띄우고 화면 유지
              if (isChangedCenter) {
                toast({ message: ERROR_MESSAGE.location });

                return;
              }

              // 값을 아예 못 불러오는 경우 홈으로 라우팅
              const isConfirm = confirm(ERROR_MESSAGE.locationPermission);

              if (isConfirm) {
                window.location.href = '/';
              }
            }
          },
          {
            enableHighAccuracy: false,
            timeout: 10000,
            maximumAge: 0,
          },
        );
      }
    };

    kakao.maps.load(() => initMap());

    if (mapRef.current) {
      kakao.maps.event.addListener(mapRef.current, 'dragstart', handleDragStart);
    }

    return () => {
      if (navigatorRef.current) {
        navigator.geolocation.clearWatch(navigatorRef.current);
      }

      if (mapRef.current) {
        kakao.maps.event.removeListener(mapRef.current, 'dragstart', handleDragStart);
      }
    };
  }, []);

  return { mapRef, currentLocationRef, center, isDragged, handleDragEnd };
};

export default useInitMap;
