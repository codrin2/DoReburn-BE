import * as S from './DayFeedback.styled';

const FEEDBACK_MOOD_MAPPER = {
  SATISFIED: `" 만족해요 "`,
  MODERATE: `" 보통이에요 "`,
  DISSATISFIED: `" 아쉬워요 "`,
};

interface DayFeedbackProps {
  feedbacks: {
    mood: string;
    memo: string;
  }[];
}

const DayFeedback = ({ feedbacks }: DayFeedbackProps) => {
  return (
    <S.DayFeedbackContainer>
      <S.DayFeedbackTitle>오늘의 피드백</S.DayFeedbackTitle>
      <S.DayFeedbackList>
        {feedbacks.map((feedback, index) => (
          <S.DayFeedbackItem key={index}>
            <S.DayFeedbackMood>
              {FEEDBACK_MOOD_MAPPER[feedback.mood as keyof typeof FEEDBACK_MOOD_MAPPER]}
            </S.DayFeedbackMood>
            {feedback.memo ? (
              <S.DayFeedbackMemo>{feedback.memo}</S.DayFeedbackMemo>
            ) : (
              <S.DayFeedbackEmpty>피드백 메모를 남기지 않았어요</S.DayFeedbackEmpty>
            )}
          </S.DayFeedbackItem>
        ))}
      </S.DayFeedbackList>
    </S.DayFeedbackContainer>
  );
};

export default DayFeedback;
