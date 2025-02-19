import { useState } from 'react';

import * as S from './MyPageGoal.styled';

import Icon from '@/components/Icon';
import { CATEGORY_LABEL_MAPPER } from '@/constants/config';
import { useMember } from '@/pages/MyPage/hooks/useMember';
import theme from '@/styles/theme';
import { CategoryType } from '@/types/filter';

const CATEGORY_ROWS: CategoryType[][] = [
  ['READING', 'ENGLISH', 'LANGUAGE'],
  ['NEWS', 'HOBBY', 'OTHERS'],
];
const GUIDE_MESSAGE = '최소 1개 이상 3개 이하 선택할 수 있어요';

const MyPageGoal = ({ editMode }: { editMode: boolean }) => {
  const { memberInfo, setCategories } = useMember();
  const { categories } = memberInfo;
  const [isError, setIsError] = useState(false);

  const handleCategoryClick = (category: CategoryType) => {
    setCategories((prev) => {
      const isSelected = prev.includes(category);

      if (isSelected) {
        const newCategories = prev.filter((c) => c !== category);
        setIsError(newCategories.length === 0);

        return newCategories;
      }

      if (prev.length >= 3) {
        return prev;
      }

      setIsError(false);

      return [...prev, category];
    });
  };

  return (
    <S.MyPageGoalContainer>
      <S.Title>목표</S.Title>
      <S.GoalList>
        {editMode && <S.GuideMessage $isError={isError}>{GUIDE_MESSAGE}</S.GuideMessage>}
        {CATEGORY_ROWS.map((row, rowIndex) => (
          <S.GoalRow key={rowIndex}>
            {row.map((category) => (
              <S.GoalItem
                key={category}
                isSelected={categories.includes(category)}
                onClick={() => handleCategoryClick(category)}
                disabled={!editMode}
              >
                <Icon
                  icon="Check"
                  width={16}
                  height={16}
                  color={
                    categories.includes(category) ? theme.colors.green700 : theme.colors.gray200
                  }
                />
                <S.GoalItemText isSelected={categories.includes(category)}>
                  {CATEGORY_LABEL_MAPPER[category]}
                </S.GoalItemText>
              </S.GoalItem>
            ))}
          </S.GoalRow>
        ))}
      </S.GoalList>
    </S.MyPageGoalContainer>
  );
};

export default MyPageGoal;
