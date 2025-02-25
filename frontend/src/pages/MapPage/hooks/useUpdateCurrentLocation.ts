import { useEffect } from 'react';

import useUpdateCurrentLocationMutation from '@/hooks/useUpdateCurrentLocationMutation';

const UPDATE_CURRENT_LOCATION_INTERVAL = 5 * 60 * 1000;

const useUpdateCurrentLocation = () => {
  const { mutate: updateCurrentLocation } = useUpdateCurrentLocationMutation();

  useEffect(() => {
    const intervalId = setInterval(() => {
      navigator.geolocation.getCurrentPosition((position) => {
        updateCurrentLocation({
          x_coordinate: position.coords.longitude,
          y_coordinate: position.coords.latitude,
        });
      });
    }, UPDATE_CURRENT_LOCATION_INTERVAL);

    return () => clearInterval(intervalId);
  }, [updateCurrentLocation]);

  return { updateCurrentLocation };
};

export default useUpdateCurrentLocation;
