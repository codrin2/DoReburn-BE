import { useQueryClient } from '@tanstack/react-query';
import { createPortal } from 'react-dom';
import { useNavigate } from 'react-router';

import * as S from './FeedbackSuccessModal.styled';

import { QUERY_KEY } from '@/constants/queryKey';

interface FeedbackSuccessModalProps {
  onClose: () => void;
}

const FeedbackSuccessModal = ({ onClose }: FeedbackSuccessModalProps) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const goToHome = async () => {
    await queryClient.invalidateQueries({ queryKey: [QUERY_KEY.memberStatus] });
    onClose();
    navigate('/');
  };

  const goToStatistics = () => {
    onClose();
    navigate('/statistics/week');
  };

  return createPortal(
    <S.ModalContainer>
      <S.ModalDimmed />
      <S.Modal>
        <S.ModalTitle>피드백이 저장되었어요!</S.ModalTitle>
        <S.ModalFooter>
          <S.ModalHomeButton onClick={goToHome}>홈으로 가기</S.ModalHomeButton>
          <S.ModalStatisticsButton onClick={goToStatistics}>통계 보러 가기</S.ModalStatisticsButton>
        </S.ModalFooter>
      </S.Modal>
    </S.ModalContainer>,
    document.body,
  );
};

export default FeedbackSuccessModal;
