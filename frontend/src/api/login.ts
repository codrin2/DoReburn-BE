import fetchClient from './fetchClient';

import { API_URL } from '@/constants/url';
import { KakaoLogin } from '@/types/auth';

const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_CLIENT_ID;
const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI;

export const kakaoLogin = () => {
  const KAKAO_LOGIN = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${KAKAO_REDIRECT_URI}&response_type=code`;

  window.location.href = KAKAO_LOGIN;
};

export const kakaoLoginAuth = async (code: string): Promise<KakaoLogin> => {
  const result = await fetchClient.post<KakaoLogin>(API_URL.loginKakaoAuth, {
    body: { code },
  });

  return result;
};

interface AuthReissueResponse {
  accessToken: string;
}

export const authReissue = async (): Promise<AuthReissueResponse> => {
  const result = await fetchClient.post<AuthReissueResponse>(API_URL.authReissue);

  return result;
};
