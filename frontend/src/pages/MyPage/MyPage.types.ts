import { CategoryType } from '@/types/filter';

export interface MemberContextType {
  memberInfo: {
    categories: CategoryType[];
    homeTitle: string;
    homeAddress: string;
    homeAddressX: number;
    homeAddressY: number;
    schoolTitle: string;
    schoolAddress: string;
    schoolAddressX: number;
    schoolAddressY: number;
  };
  setCategories: (categories: CategoryType[] | ((prev: CategoryType[]) => CategoryType[])) => void;
  setHome: (home: {
    homeTitle: string;
    homeAddress: string;
    homeAddressX: number;
    homeAddressY: number;
  }) => void;
  setSchool: (school: {
    schoolTitle: string;
    schoolAddress: string;
    schoolAddressX: number;
    schoolAddressY: number;
  }) => void;
}
