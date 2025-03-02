import { redirect } from 'react-router';

import { CustomError } from '@/api/error';
import { getMemberStatus } from '@/api/member';
import { DATE_TYPE } from '@/constants/config';
import { queryClient } from '@/constants/queryClient';
import { QUERY_KEY } from '@/constants/queryKey';

const MEMBER_STATUS = {
  onboarding: 'ONBOARDING',
  stop: 'STOP',
  move: 'MOVE',
  feedback: 'FEEDBACK',
};

// 접근 제한이 필요한 라우터의 loader 설정
const memberStatusLoader = async ({ request }: { request: Request }) => {
  try {
    const memberStatus = await queryClient.fetchQuery({
      queryKey: [QUERY_KEY.memberStatus],
      queryFn: getMemberStatus,
      staleTime: 5 * 1000,
    });

    const url = new URL(request.url);
    const currentPath = url.pathname;
    const dateType = url.searchParams.get('dateType') || DATE_TYPE.TODAY;
    const mainURL = dateType === DATE_TYPE.TODAY ? '/' : `/?dateType=${dateType}`;

    if (memberStatus.status === MEMBER_STATUS.onboarding && currentPath !== '/onboarding') {
      return redirect('/onboarding');
    } else if (memberStatus.status === MEMBER_STATUS.stop && currentPath !== '/') {
      return redirect(mainURL);
    } else if (memberStatus.status === MEMBER_STATUS.move && currentPath !== '/plan') {
      return redirect('/plan');
    } else if (memberStatus.status === MEMBER_STATUS.feedback && currentPath !== '/feedback') {
      return redirect('/feedback');
    }

    return memberStatus;
  } catch (err) {
    const error = err as CustomError;

    const isExpired =
      error.errorCode === 'TOKEN_INVALID' ||
      error.errorCode === 'TOKEN_BLACKLISTED' ||
      error.errorCode === 'MISSING_TOKEN_IN_COOKIE' ||
      error.errorCode === 'REFRESH_TOKEN_EXPIRED';

    if (isExpired) {
      return redirect('/landing');
    }
  }
};

export default memberStatusLoader;
