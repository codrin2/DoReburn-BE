import * as S from './AchievementDetail.styled';

import Icon, { IconType } from '@/components/Icon';
import theme from '@/styles/theme';

const ICON_MAPPER: Record<string, IconType> = {
  ENGLISH: 'English',
  LANGUAGE: 'Language',
  READING: 'Reading',
  HOBBY: 'Hobby',
  NEWS: 'News',
  OTHERS: 'Others',
} as const;

const TITLE_TEXT = {
  HAS_ACHIEVEMENT: '오늘 한 일을 보여드려요',
  NO_ACHIEVEMENT: '오늘 한 일이 없어요',
} as const;

interface Achievement {
  category: IconType;
  title: string;
}

interface AchievementDetailProps {
  achievements: Achievement[];
}

const AchievementDetail = ({ achievements }: AchievementDetailProps) => {
  return (
    <S.AchievementDetailLayout>
      <S.Title>
        {achievements?.length > 0 ? TITLE_TEXT.HAS_ACHIEVEMENT : TITLE_TEXT.NO_ACHIEVEMENT}
      </S.Title>
      <S.AchievementList>
        {achievements?.length > 0 ? (
          achievements?.map((achievement, index) => {
            return (
              <S.AchievementItem key={index}>
                <Icon icon={ICON_MAPPER[achievement.category]} />
                <S.TodoTitle>{achievement.title}</S.TodoTitle>
              </S.AchievementItem>
            );
          })
        ) : (
          <S.EmptyAchievement>
            <Icon icon="Fire" width={96} height={96} color={theme.colors.white} />
          </S.EmptyAchievement>
        )}
      </S.AchievementList>
    </S.AchievementDetailLayout>
  );
};

export default AchievementDetail;
