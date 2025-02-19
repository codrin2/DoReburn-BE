import FilterForm from '../components/FilterForm';

import useOverlay from '@/hooks/useOverlay';
import { CategoryType, DifficultyType } from '@/types/filter';

export interface OpenFilterBottomSheetParams {
  categoryList: CategoryType[];
  difficultyList: DifficultyType[];
  onConfirm: (categoryList: CategoryType[], difficultyList: DifficultyType[]) => void;
}

const useFilterBottomSheet = () => {
  const overlay = useOverlay();

  const openFilterBottomSheet = ({
    categoryList,
    difficultyList,
    onConfirm,
  }: OpenFilterBottomSheetParams) => {
    overlay.open(() => (
      <FilterForm
        selectedCategoryList={categoryList}
        selectedDifficultyList={difficultyList}
        onClose={overlay.close}
        onConfirm={onConfirm}
      />
    ));
  };

  return openFilterBottomSheet;
};

export default useFilterBottomSheet;
