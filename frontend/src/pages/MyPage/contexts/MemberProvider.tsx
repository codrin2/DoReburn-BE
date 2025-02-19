import { useState } from 'react';

import { MemberContext } from './MemberContext';

import { CategoryType } from '@/types/filter';

export const MemberProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [categories, setCategories] = useState<CategoryType[]>([]);
  const [school, setSchool] = useState({
    schoolTitle: '',
    schoolAddress: '',
    schoolAddressX: 0,
    schoolAddressY: 0,
  });
  const [home, setHome] = useState({
    homeTitle: '',
    homeAddress: '',
    homeAddressX: 0,
    homeAddressY: 0,
  });

  return (
    <MemberContext.Provider
      value={{
        memberInfo: {
          categories,
          schoolTitle: school.schoolTitle,
          schoolAddress: school.schoolAddress,
          schoolAddressX: school.schoolAddressX,
          schoolAddressY: school.schoolAddressY,
          homeTitle: home.homeTitle,
          homeAddress: home.homeAddress,
          homeAddressX: home.homeAddressX,
          homeAddressY: home.homeAddressY,
        },
        setCategories,
        setHome,
        setSchool,
      }}
    >
      {children}
    </MemberContext.Provider>
  );
};

export default MemberProvider;
