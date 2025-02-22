import { createPortal } from 'react-dom';

import * as S from './HomeModal.styled';

const getContent = (content: string[]) => {
  return content.map((text) => (
    <span key={text}>
      {text}
      <br />
    </span>
  ));
};

interface HomeModalProps {
  title: string;
  content: string[];
  close: () => void;
  onConfirm: () => void;
  confirmText?: string;
}

const HomeModal = ({ title, content, close, onConfirm, confirmText }: HomeModalProps) => {
  return (
    <>
      {createPortal(
        <S.ModalContainer>
          <S.ModalDimmed onClick={close} />
          <S.Modal>
            <S.ModalTitle>{title}</S.ModalTitle>
            <S.ModalContent>{getContent(content)}</S.ModalContent>
            <S.ModalFooter>
              <S.ModalCancelButton onClick={close}>취소하기</S.ModalCancelButton>
              <S.ModalConfirmButton onClick={onConfirm}>
                {confirmText || '홈으로 이동'}
              </S.ModalConfirmButton>
            </S.ModalFooter>
          </S.Modal>
        </S.ModalContainer>,
        document.body,
      )}
    </>
  );
};

export default HomeModal;
