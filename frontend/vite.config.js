import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  define: {
    // Polyfill for libraries like sockjs-client
    global: 'window',
  },
  server: {
    host: true,
    port: 3001,
    strictPort: false,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
});
