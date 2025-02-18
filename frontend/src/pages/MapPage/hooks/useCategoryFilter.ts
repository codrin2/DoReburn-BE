import { useState } from 'react';

import { NearbyMember } from '@/api/map';
import { CategoryType } from '@/types/filter';

const useCategoryFilter = (memberLocations: NearbyMember[]) => {
  const [category, setCategory] = useState<CategoryType | null>(null);

  const filteredLocations = memberLocations.filter((location) => {
    if (category) {
      if (location.category.includes(category)) {
        return location;
      }
    } else {
      return location;
    }
  });

  const handleSelectCategoryFilter = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value as CategoryType;

    if (value === category) {
      setCategory(null);
    } else {
      setCategory(value);
    }
  };

  return { category, handleSelectCategoryFilter, filteredLocations };
};

export default useCategoryFilter;
