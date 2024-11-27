const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
	proxy: {
		'^/api': {
			target: 'http://localhost:9000',
			ws: false
		},
		'^/api/ws': {
			target: 'http://localhost:9000',
			ws: true
		}
	}
  }
})
