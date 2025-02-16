import { TabContent, TabList } from './FavoritePage.styled';
import FavoriteTab from '../EditPage/components/FavoriteTab';
import RecommendTodoContainer from '../RecommendTodoPage/components/RecommendTodoContainer';
import FavoriteHeader from './components/FavoriteHeader/FavoriteHeader';

import { Tab } from '@/components/Tab';
import { TODO_TYPE } from '@/constants/config';

const FAVORITE_TABS = [
  { label: '즐겨찾기', value: 'favorite' },
  { label: '모든 추천', value: 'all' },
] as const;

const FavoritePage = () => {
  return (
    <Tab.Root tabList={FAVORITE_TABS}>
      <FavoriteHeader />

      <TabList>
        {FAVORITE_TABS.map((tab) => (
          <li key={tab.value}>
            <Tab.Trigger value={tab.value}>{tab.label}</Tab.Trigger>
          </li>
        ))}
      </TabList>
      <TabContent>
        <FavoriteTab todoType={TODO_TYPE.SAVE} />
        <RecommendTodoContainer isFavoritePage />
      </TabContent>
    </Tab.Root>
  );
};

export default FavoritePage;
