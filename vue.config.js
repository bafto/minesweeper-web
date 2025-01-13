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
	},
	pwa: {
		iconPaths: {
			faviconSVG: null,
			favicon32: 'favicon.ico',
			favicon16: 'favicon.ico',
			appleTouchIcon: null,
			maskIcon: null,
			msTileImage: null,
		},
	},
	chainWebpack: config => {
		config.plugin('html').tap(args => {
			args[0].favicon = 'public/favicon.ico'; // Explicitly set the favicon path
			return args;
		});
	},
})
