import { keepPreviousData, useQuery } from '@tanstack/react-query';

import { getNearbyUsers } from '@/api/map';
import { QUERY_KEY } from '@/constants/queryKey';

const INITIAL_RADIUS = 3;

interface useNearbyUsersQueryProps {
  lng: number;
  lat: number;
}

const useNearbyUsersQuery = ({ lng, lat }: useNearbyUsersQueryProps) => {
  return useQuery({
    queryKey: [QUERY_KEY.nearbyUsers, lng, lat],
    queryFn: () =>
      getNearbyUsers({
        radius: INITIAL_RADIUS,
        x_coordinate: lng,
        y_coordinate: lat,
      }),
    enabled: !!lng && !!lat,
    gcTime: 0,

    placeholderData: keepPreviousData,
  });
};

export default useNearbyUsersQuery;
