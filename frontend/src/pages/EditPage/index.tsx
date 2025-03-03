import * as S from './EditPage.styled';

import FavoriteTab from '@/components/FavoriteTab';
import Header from '@/components/Header';
import RecommendTab from '@/components/RecommendTab';
import { Tab } from '@/components/Tab';
import TodoTab from '@/components/TodoTab';
import { TABS } from '@/constants/config';
import useQueryParamsDate from '@/hooks/useQueryParamsDate';

const EditPage = () => {
  const { isToday, dateType } = useQueryParamsDate();

  return (
    <>
      <Header>
        <Header.BackButton />
        <Header.Title>{isToday ? '오늘' : '내일'} 할 일 수정하기</Header.Title>
      </Header>
      <Tab.Root tabList={TABS}>
        <S.TabList>
          {TABS.map((tab) => (
            <li key={tab.value}>
              <Tab.Trigger value={tab.value}>{tab.label}</Tab.Trigger>
            </li>
          ))}
        </S.TabList>
        <S.TabContent>
          <TodoTab todoType={dateType} />
          <FavoriteTab todoType={dateType} />
          <RecommendTab todoType={dateType} />
        </S.TabContent>
      </Tab.Root>
    </>
  );
};

export default EditPage;
