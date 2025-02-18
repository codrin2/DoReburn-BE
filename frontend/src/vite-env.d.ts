/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_BASE_URL: string;
  readonly VITE_KAKAO_APP_KEY: string;
  readonly VITE_KAKAO_REDIRECT_URI: string;
  readonly VITE_KAKAO_CLIENT_SECRET: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
