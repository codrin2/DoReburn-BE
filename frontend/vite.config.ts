import react from '@vitejs/plugin-react';
import { visualizer } from 'rollup-plugin-visualizer';
import type { PluginOption } from 'vite';
// import { VitePWA } from 'vite-plugin-pwa';
import { defineConfig } from 'vitest/config';

// https://vite.dev/config/
export default defineConfig(({ mode }) => ({
  plugins: [
    react(),
    // VitePWA({
    //   strategies: 'injectManifest',
    //   srcDir: 'src',
    //   filename: 'service-worker.js',
    //   includeAssets: ['favicon.ico', 'apple-touch-icon'],
    //   manifest: {
    //     name: '통학생을 위한 이동 시간 할일 관리 서비스, 두리번',
    //     short_name: '두리번',
    //     description: '통학생을 위한 이동 시간 할일 관리 서비스, 두리번',
    //     theme_color: '#ffffff',
    //     background_color: '#ffffff',
    //     display: 'standalone',
    //     orientation: 'portrait',
    //     start_url: '/',
    //     icons: [
    //       { src: '/icons/192x192.png', sizes: '192x192', type: 'image/png' },
    //       { src: '/icons/512x512.png', sizes: '512x512', type: 'image/png' },
    //       { src: '/icons/apple-touch-icon.png', sizes: '180x180', type: 'image/png' },
    //       { src: '/icons/96x96.png', sizes: '96x96', type: 'image/png' },
    //       { src: '/icons/32x32.png', sizes: '32x32', type: 'image/png' },
    //     ],
    //   },
    //   devOptions: {
    //     enabled: mode !== 'development',
    //   },
    //   registerType: 'autoUpdate',
    // }),
    ...(mode === 'development'
      ? [
          visualizer({
            emitFile: true,
            filename: 'stats.html',
            open: true,
          }) as PluginOption,
        ]
      : []),
  ],
  resolve: {
    alias: [{ find: '@', replacement: '/src' }],
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/tests/setup.ts',
  },
}));
