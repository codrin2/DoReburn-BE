import { useEffect } from 'react';
import { useNavigate } from 'react-router';

import useMemberStatusQuery from './useMemberStatusQuery';
import useQueryParamsDate from './useQueryParamsDate';

import { DATE_TYPE } from '@/constants/config';

const useRedirectByMemberStatus = () => {
  const { data: memberStatus, isError } = useMemberStatusQuery();
  const navigate = useNavigate();
  const { dateType } = useQueryParamsDate();

  if (isError) {
    navigate('/landing');
  }

  useEffect(() => {
    if (!memberStatus) return;

    const mainURL = dateType === DATE_TYPE.TODAY ? '/' : `/?dateType=${dateType}`;

    switch (memberStatus?.status) {
      case 'ONBOARDING':
        navigate('/onboarding');
        break;
      case 'STOP':
        navigate(mainURL);
        break;
      case 'MOVE':
        navigate('/plan');
        break;
      case 'FEEDBACK':
        navigate('/feedback');
        break;
      default:
        navigate('/landing');
        break;
    }
  }, [memberStatus, navigate, dateType]);
};

export default useRedirectByMemberStatus;
