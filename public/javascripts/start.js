document.addEventListener('DOMContentLoaded', () => {
	const { createApp, ref } = Vue

	createApp({
		data() {
			return {
				width: 10,
				height: 10,
				bomb_chance: 0.5,
				max_undos: 3
			}
		},
		methods: {
			startGame() {
				fetch('/api/start_game', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'Csrf-Token': getCookieByName('play-csrf-token'),
					},
					body: JSON.stringify({
						width: this.width,
						height: this.height,
						bomb_chance: this.bomb_chance,
						max_undos: this.max_undos
					})
				}).then(window.location.reload).catch(console.error);
			}
		}
	}).mount('#settings')
})

function getCookieByName(name) {
	const cookies = document.cookie.split(";");
	for (let cookie of cookies) {
		cookie = cookie.trim();
		if (cookie.startsWith(name + "=")) {
			return cookie.substring(name.length + 1);
		}
	}
	return null;
}
