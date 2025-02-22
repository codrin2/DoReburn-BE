import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { useNavigate } from 'react-router';

import HomeModal from './components/HomeModal';
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

  const [isOpen, setIsOpen] = useState(false);
  const open = () => setIsOpen(true);
  const close = () => setIsOpen(false);

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
      <S.FinishButton onClick={open}>이동 완료</S.FinishButton>
      {isOpen && (
        <HomeModal
          title="이동을 완료할까요?"
          content={['오늘도 수고했어요 🔥', '완료를 누르면 피드백 화면으로 이동합니다.']}
          confirmText="완료하기"
          close={close}
          onConfirm={handleClickFinish}
        />
      )}
    </S.PlanPageLayout>
  );
};

export default PlanPage;
