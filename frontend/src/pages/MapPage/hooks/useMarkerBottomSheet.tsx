import DetailUserTodo from '../components/DetailTodo';

import useOverlay from '@/hooks/useOverlay';

const useMarkerBottomSheet = () => {
  const overlay = useOverlay();

  const openMarkerBottomSheet = (memberId: number) => {
    overlay.open(() => <DetailUserTodo memberId={memberId} />);
  };

  return openMarkerBottomSheet;
};

export default useMarkerBottomSheet;
