const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  productionSourceMap: false,
  css: {
    loaderOptions: {
      css: {
        esModule: false
      }
    }
  },
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: process.env.SKY_API_TARGET || 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }
})
