// eslint-disable-next-line import/named
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';

import TodoTab from '.';

import { MAX_TODO_ITEM_LENGTH } from '@/constants/config';
import { customRender } from '@/tests/test-utils';

describe('TodoTab 컴포넌트 테스트', () => {
  describe('todoType(TODAY, TOMORROW, PATH)에 따른 텍스트', () => {
    it('오늘에 대한 텍스트가 출력된다.', async () => {
      const EXPECTED_TEXT = `오늘 할 일, 작은 목표로 시작해봐요 최대 ${MAX_TODO_ITEM_LENGTH}개까지 고를 수 있어요`;
      customRender(<TodoTab todoType="TODAY" />);

      await waitFor(() => {
        expect(screen.getByText(EXPECTED_TEXT)).toBeInTheDocument();
      });
    });

    it('내일에 대한 텍스트가 출력된다.', async () => {
      const EXPECTED_TEXT = `내일 할 일, 작은 목표로 시작해봐요 최대 ${MAX_TODO_ITEM_LENGTH}개까지 고를 수 있어요`;
      customRender(<TodoTab todoType="TOMORROW" />);

      await waitFor(() => {
        expect(screen.getByText(EXPECTED_TEXT)).toBeInTheDocument();
      });
    });

    it('경로에 대한 텍스트가 출력된다.', async () => {
      const EXPECTED_TEXT = '이 구간에서 할 일을 골라보세요';
      customRender(<TodoTab todoType="PATH" />);

      await waitFor(() => {
        expect(screen.getByText(EXPECTED_TEXT)).toBeInTheDocument();
      });
    });
  });

  describe('직접 추가하기 버튼을 클릭했을 때', () => {
    it('바텀시트를 노출하여 할 일을 추가할 수 있다.', async () => {
      const user = userEvent.setup();
      const EXPECTED_TEXT = '할 일 추가하기';
      const ADD_BUTTON_TEXT = '직접 추가하기';

      customRender(<TodoTab todoType="TODAY" />);

      const button = await screen.findByRole('button', { name: ADD_BUTTON_TEXT });
      await user.click(button);

      await waitFor(() => {
        expect(screen.getByText(EXPECTED_TEXT)).toBeInTheDocument();
      });
    });
  });
});
