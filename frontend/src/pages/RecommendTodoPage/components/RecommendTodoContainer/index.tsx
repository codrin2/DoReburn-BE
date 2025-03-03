import { useEffect, useState } from 'react';
import { useParams } from 'react-router';

import * as S from './RecommendTodoContainer.styled';
import useFilterBottomSheet from '../../hooks/useFilterBottomSheet';
import useRecommendTodoFilterQuery from '../../hooks/useRecommendTodoFilterQuery';
import { getTodoType } from '../../RecommendTodoPage.utils';

import IconButton from '@/components/Button/IconButton';
import Icon from '@/components/Icon';
import IntersectionObserverScroll from '@/components/IntersectionObserverScroll';
import TodoEditItem from '@/components/TodoEditItem';
import { TODO_TOAST_MESSAGE } from '@/constants/message';
import useAddTodoFromArchivedMutation from '@/hooks/todo/useAddTodoFromArchivedMutation';
import useMemberInfoQuery from '@/hooks/useMemberInfoQuery';
import useOverlay from '@/hooks/useOverlay';
import useQueryParamsDate from '@/hooks/useQueryParamsDate';
import useToast from '@/hooks/useToast';
import { CategoryType, DifficultyType } from '@/types/filter';

interface RecommendTodoContainerProps {
  isFavoritePage: boolean;
}

const RecommendTodoContainer = ({ isFavoritePage }: RecommendTodoContainerProps) => {
  const { planId } = useParams();
  const { dateType } = useQueryParamsDate();
  const openFilterBottomSheet = useFilterBottomSheet();

  const [categoryList, setCategoryList] = useState<CategoryType[]>([]);
  const [difficultyList, setDifficultyList] = useState<DifficultyType[]>([]);
  const [isInitialized, setIsInitialized] = useState(false);

  const { data: memberInfo } = useMemberInfoQuery();

  const todoType = getTodoType({ dateType, isFavoritePage, planId });

  const {
    data: recommendList,
    isFetching,
    hasNextPage,
    fetchNextPage,
  } = useRecommendTodoFilterQuery(todoType, categoryList, difficultyList, Number(planId));
  const { mutate: addTodoFromArchived } = useAddTodoFromArchivedMutation(
    categoryList,
    difficultyList,
  );
  const { toast } = useToast();
  const overlay = useOverlay();

  const isCategory = categoryList.length > 0;
  const isDifficulty = difficultyList.length > 0;

  const handleFilter = (categoryList: CategoryType[], difficultyList: DifficultyType[]) => {
    setCategoryList(categoryList);
    setDifficultyList(difficultyList);
    overlay.close();
  };

  const handleAddTodoFromRecommendAll = (todoId: number) => {
    addTodoFromArchived(
      { todoType, todoId, planId: Number(planId) },
      {
        onSuccess: () =>
          toast({
            message: isFavoritePage
              ? TODO_TOAST_MESSAGE.addFavorite
              : TODO_TOAST_MESSAGE.add(todoType),
          }),
      },
    );
  };

  // 초기 로딩 시에만 응답값 설정
  useEffect(() => {
    if (!isInitialized && memberInfo) {
      setCategoryList(memberInfo.categories);
      setIsInitialized(true);
    }
  }, [isInitialized, memberInfo]);

  // 데이터가 없을 경우
  if (!recommendList) return <div>추천 할 일 데이터가 없습니다.</div>;

  return (
    <>
      <S.FilterWrapper
        onClick={() =>
          openFilterBottomSheet({ categoryList, difficultyList, onConfirm: handleFilter })
        }
      >
        <S.IconButtonWrapper
          $isSelected={isCategory}
          flex="row-reverse"
          icon={isCategory && <Icon icon="FilledArrow" cursor="pointer" width={16} height={16} />}
          text={isCategory ? `목표 ${categoryList.length}` : '목표'}
        />

        <S.IconButtonWrapper
          $isSelected={isDifficulty}
          flex="row-reverse"
          icon={isDifficulty && <Icon icon="FilledArrow" cursor="pointer" width={16} height={16} />}
          text={isDifficulty ? `난이도 ${difficultyList.length}` : '난이도'}
        />
      </S.FilterWrapper>
      <S.RecommendTabList>
        <IntersectionObserverScroll hasNextPage={hasNextPage} onReachBottom={fetchNextPage}>
          {recommendList.pages.map((page) =>
            page.data.map((todo) => (
              <TodoEditItem
                key={todo.todoId}
                todo={todo}
                disabled={todo.hasChild}
                left={
                  <IconButton
                    icon={
                      todo.hasChild ? (
                        <Icon icon="CheckCircle" cursor="pointer" />
                      ) : (
                        <Icon icon="PlusCircle" cursor="pointer" />
                      )
                    }
                    onClick={() => handleAddTodoFromRecommendAll(todo.todoId)}
                    disabled={todo.hasChild}
                  />
                }
              />
            )),
          )}
          {isFetching && <S.LoadingBlock />}
        </IntersectionObserverScroll>
      </S.RecommendTabList>
    </>
  );
};

export default RecommendTodoContainer;
