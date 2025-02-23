import { useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';

import * as S from './BottomSheet.styled';
import IconButton from '../Button/IconButton';
import Icon from '../Icon';

const CLOSE_THRESHOLD = 30;

interface BottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  content: React.ReactNode;
  onConfirm?: () => void;
  title?: string;
  cancelText?: string;
  confirmText?: string;
  confirmDisabled?: boolean;
  delay?: number;
  subTitle?: string;
  onAnimationEnd?: () => void;
}

const BottomSheet = ({
  isOpen,
  onClose,
  onConfirm,
  content,
  title,
  cancelText,
  confirmText,
  confirmDisabled,
  delay = 200,
  subTitle,
  onAnimationEnd,
}: BottomSheetProps) => {
  const [isAnimating, setIsAnimating] = useState(isOpen);

  const [translateY, setTranslateY] = useState(0);
  const startYRef = useRef<number | null>(null);

  const handleTouchStart = (e: React.TouchEvent<HTMLDivElement>) => {
    startYRef.current = e.touches[0].clientY;
  };

  const handleTouchMove = (e: React.TouchEvent<HTMLDivElement>) => {
    if (startYRef.current === null) return;

    const deltaY = e.touches[0].clientY - startYRef.current;

    setTranslateY(deltaY);
  };

  const handleTouchEnd = () => {
    if (translateY > CLOSE_THRESHOLD) {
      onClose();
    }

    startYRef.current = null;
  };

  const handleAnimationEnd = () => {
    if (!isOpen) {
      setIsAnimating(false);
      document.body.style.setProperty('overflow', '');
      if (onAnimationEnd) onAnimationEnd();
    }
  };

  useEffect(() => {
    if (isOpen) {
      setIsAnimating(true);
      document.body.style.setProperty('overflow', 'hidden');
    } else {
      setTranslateY(0);
    }
  }, [isOpen]);

  if (!isAnimating) return null;

  return (
    <>
      {createPortal(
        <S.SheetContainer>
          <S.Backdrop onClick={onClose} $isOpen={isOpen} />
          <S.Sheet
            $isOpen={isOpen}
            $delay={delay}
            onAnimationEnd={handleAnimationEnd}
            onTouchStart={handleTouchStart}
            onTouchMove={handleTouchMove}
            onTouchEnd={handleTouchEnd}
          >
            {(title || subTitle) && (
              <S.Header>
                <S.DragHandle />
                {title && <S.Title>{title}</S.Title>}
                {subTitle && <S.SubTitle>{subTitle}</S.SubTitle>}
                <IconButton onClick={onClose} icon={<Icon icon="Close" cursor="pointer" />} />
              </S.Header>
            )}
            <S.Content>{content}</S.Content>
            {(cancelText || confirmText) && (
              <S.Footer>
                {cancelText && <S.CancelButton onClick={onClose}>{cancelText}</S.CancelButton>}
                {confirmText && (
                  <S.ConfirmButton onClick={onConfirm} disabled={confirmDisabled}>
                    {confirmText}
                  </S.ConfirmButton>
                )}
              </S.Footer>
            )}
          </S.Sheet>
        </S.SheetContainer>,
        document.body,
      )}
    </>
  );
};

export default BottomSheet;
