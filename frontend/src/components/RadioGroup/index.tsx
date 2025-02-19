import * as S from './RadioGroup.styled';

import { CategoryType, Filter } from '@/types/filter';

interface RadioGroupProps {
  name: string;
  filters: Filter[] | readonly Filter[];
  selectedValue: string;
  handleChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  disabled?: boolean;
  category?: CategoryType | null;
  width?: string;
  isFilter?: boolean;
  type?: 'radio' | 'checkbox';
}

const RadioGroup = ({
  name,
  filters,
  selectedValue,
  handleChange,
  disabled,
  category,
  width,
  isFilter,
  type = 'radio',
}: RadioGroupProps) => {
  return (
    <S.RadioGroupWrapper $width={width} $isFilter={isFilter}>
      {filters.map((filter) => (
        <label key={filter.value}>
          <S.HiddenInput
            type={type}
            name={name}
            value={filter.value}
            onChange={handleChange}
            disabled={disabled}
          />
          <S.RadioBadge
            $isSelected={selectedValue === filter.value}
            $disabled={disabled}
            $category={category}
            $isFilter={isFilter}
          >
            {filter.label}
          </S.RadioBadge>
        </label>
      ))}
    </S.RadioGroupWrapper>
  );
};

export default RadioGroup;
