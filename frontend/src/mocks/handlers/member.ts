import { http, HttpResponse } from 'msw';

import MEMBER_ADDRESS from '../data/memberAddress.json';
import MEMBER_INFO from '../data/memberInfo.json';
import MEMBER_STATUS from '../data/memberStatus.json';

import { MOCK_API_URL } from '@/constants/url';

const getMemberAddressHandler = () => {
  return HttpResponse.json(MEMBER_ADDRESS);
};

const updateMemberStatusHandler = () => {
  return new HttpResponse(null, { status: 204 });
};

const getMemberStatusHandler = () => {
  return HttpResponse.json(MEMBER_STATUS);
};

const getMemberInfoHandler = () => {
  return HttpResponse.json(MEMBER_INFO);
};

export const handlers = [
  http.get(MOCK_API_URL.memberAddress, getMemberAddressHandler),
  http.patch(MOCK_API_URL.memberStatus, updateMemberStatusHandler),
  http.get(MOCK_API_URL.memberStatus, getMemberStatusHandler),
  http.get(MOCK_API_URL.memberInfo, getMemberInfoHandler),
];
