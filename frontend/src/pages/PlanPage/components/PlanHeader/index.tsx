import React, { useState } from 'react';
import { useNavigate } from 'react-router';

import useCancelPlanMutation from '../../hooks/useCancelPlanMutation';
import usePlanInfoQuery from '../../hooks/usePlanInfoQuery';
import HomeModal from '../HomeModal';

import Header from '@/components/Header';

const PlanHeader = () => {
  const navigate = useNavigate();
  const { data: planInfo } = usePlanInfoQuery();
  const { mutate: cancelPlan } = useCancelPlanMutation();

  const [isOpen, setIsOpen] = useState(false);

  const open = () => setIsOpen(true);
  const close = () => setIsOpen(false);

  const handleConfirm = () => {
    cancelPlan(Number(planInfo?.planId), {
      onSuccess: () => {
        close();
        navigate('/', { replace: true });
      },
    });
  };

  return (
    <>
      <Header>
        <Header.Left>
          <Header.HomeButton onClick={open} />
        </Header.Left>
        <Header.Right>
          <Header.MenuButton />
        </Header.Right>
      </Header>
      {isOpen && (
        <HomeModal
          title="처음으로 가기"
          content={['현재 경로 정보와 할 일이 모두 사라져요.', '그래도 이동할까요?']}
          close={close}
          onConfirm={handleConfirm}
        />
      )}
    </>
  );
};

export default PlanHeader;
