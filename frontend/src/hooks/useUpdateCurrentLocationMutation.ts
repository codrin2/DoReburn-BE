import { useMutation, useQueryClient } from '@tanstack/react-query';

import { CurrentLocationParams, updateCurrentLocation } from '@/api/map';
import { QUERY_KEY } from '@/constants/queryKey';

const useUpdateCurrentLocationMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: CurrentLocationParams) => updateCurrentLocation(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [QUERY_KEY.nearbyUsers] });
    },
  });
};

export default useUpdateCurrentLocationMutation;
