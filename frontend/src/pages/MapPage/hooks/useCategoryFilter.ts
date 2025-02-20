import { useState } from 'react';

import { NearbyMember } from '@/api/map';
import { CategoryType } from '@/types/filter';

const useCategoryFilter = (memberLocations: NearbyMember[]) => {
  const [category, setCategory] = useState<CategoryType | null>(null);

  const filteredLocations = memberLocations.filter((location) => {
    if (category && location.category && location.category.length > 0) {
      if (location.category.includes(category)) {
        return location;
      }
    }
  });

  const locations =
    filteredLocations.length === 0 && category === null ? memberLocations : filteredLocations;

  const handleSelectCategoryFilter = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value as CategoryType;

    if (value === category) {
      setCategory(null);
    } else {
      setCategory(value);
    }
  };

  return { category, handleSelectCategoryFilter, filteredLocations: locations };
};

export default useCategoryFilter;
