import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // Cho phép Docker map port
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://api-gateway:8080', // Trỏ tới API Gateway trong Docker network
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
