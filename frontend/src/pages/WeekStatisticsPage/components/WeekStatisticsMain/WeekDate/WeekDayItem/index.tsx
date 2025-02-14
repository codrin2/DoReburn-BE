import * as S from './WeekDayItem.styled';

interface WeekDayItemProps {
  day: number;
}

const WeekDayItem = ({ day }: WeekDayItemProps) => {
  return (
    <S.WeekDayItemContainer>
      <button>
        <S.WeekDayItemDate>{day}</S.WeekDayItemDate>
      </button>
    </S.WeekDayItemContainer>
  );
};

export default WeekDayItem;
