import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router';

import PlanHeader from './components/PlanHeader';
import PlanInfoHeader from './components/PlanInfoHeader';
import PlanContent from './components/TimeBlockContent/PlanContent';
import usePlanInfoQuery from './hooks/usePlanInfoQuery';
import * as S from './PlanPage.styled';

import { finishPlan } from '@/api/plan';
import useRedirectByMemberStatus from '@/hooks/useRedirectByMemberStatus';

const useFinishPlanMutation = () => {
  return useMutation({
    mutationFn: finishPlan,
  });
};

const PlanPage = () => {
  useRedirectByMemberStatus();
  const { data } = usePlanInfoQuery();
  const navigate = useNavigate();
  const { mutate: finishPlan } = useFinishPlanMutation();

  const handleClickFinish = () => {
    finishPlan(undefined, {
      onSuccess: () => {
        navigate('/feedback');
      },
    });
  };

  return (
    <S.PlanPageLayout>
      <PlanHeader />

      {/* 이동 정보 */}
      <PlanInfoHeader createdAt={data?.createdAt} totalSectionTime={data?.totalSectionTime} />

      <S.HorizontalLine />

      {/* 경로별 할 일 정보 */}
      <PlanContent paths={data?.paths} />

      {/* 이동 완료 버튼 영역 */}
      <S.FinishButton onClick={handleClickFinish}>이동 완료</S.FinishButton>
    </S.PlanPageLayout>
  );
};

export default PlanPage;
