import '@testing-library/jest-dom';
import { afterAll, afterEach, beforeAll } from 'vitest';

import { server } from '@/mocks/server';

// ✅ 모든 테스트 시작 전에 MSW 실행
beforeAll(() => server.listen());

// ✅ 각 테스트 후 MSW 핸들러 리셋
afterEach(() => server.resetHandlers());

// ✅ 모든 테스트 완료 후 MSW 종료
afterAll(() => server.close());
