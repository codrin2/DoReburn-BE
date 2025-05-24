import { CATEGORY_OPTIONS, DIFFICULTY_OPTIONS, TODO_TYPE } from '@/constants/config';
import { CategoryType, DifficultyType } from '@/types/filter';
import { DateType } from '@/types/todo';

export const getFilterCategories = (selectedCategoryList: CategoryType[]) => {
  return CATEGORY_OPTIONS.reduce(
    (acc, { value }) => {
      acc[value] = selectedCategoryList.includes(value);

      return acc;
    },
    {} as Record<CategoryType, boolean>,
  );
};

export const getFilterDifficulties = (selectedDifficultyList: DifficultyType[]) => {
  return DIFFICULTY_OPTIONS.reduce(
    (acc, { value }) => {
      acc[value] = selectedDifficultyList.includes(value);

      return acc;
    },
    {} as Record<DifficultyType, boolean>,
  );
};

interface GetTodoTypeParams {
  dateType: DateType;
  isFavoritePage: boolean;
  planId?: string;
}

export const getTodoType = ({ planId, isFavoritePage, dateType }: GetTodoTypeParams) => {
  if (planId) {
    return TODO_TYPE.PATH;
  } else if (isFavoritePage) {
    return TODO_TYPE.FAVORITE;
  }

  return dateType;
};
