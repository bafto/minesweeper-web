document.addEventListener('DOMContentLoaded', () => {
	const { createApp, ref } = Vue

	createApp({
		data() {
			return {
				lobbies: [],
				username: "",
				width: 10,
				height: 10,
				bomb_chance: 0.5,
				max_undos: 3,
				socket: null
			};
		},
		computed: {
			createLobbyDisabled() {
				console.log(this.socket);
				return !this.width || !this.height || this.max_undos === '' || !this.username || this.socket !== null;
			},
			startMultiplayerDisabled() {
				return this.socket === null;
			}
		},
		mounted() {
			this.updateLobbies()
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
						bomb_chance: parseFloat(this.bomb_chance),
						max_undos: this.max_undos
					})
				}).then(window.location.reload.bind(window.location)).catch(console.error);
			},
			async selectMultiplayer() {
				fetch('/api/select_multiplayer', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'Csrf-Token': getCookieByName('play-csrf-token'),
					},
					body: JSON.stringify({
						username: this.username,
						width: this.width,
						height: this.height,
						bomb_chance: parseFloat(this.bomb_chance),
						max_undos: this.max_undos
					})
				}).then(async (resp) => {
					const json = await resp.json();
					if (json.error) {
						console.error(json.error);
					} else {
						this.socket = start_multiplayer_ws(this.username);
					}
				}).catch(console.error);
			},
			joinLobby(lobby) {
				if (this.socket) {
					return;
				}

				const socket = new WebSocket(`ws://localhost:9000/api/multiplayer_websocket?username=${this.username}&lobby=${lobby}`)
				socket.onopen = () => {
					console.log("ws open");
				};
				socket.onclose = () => console.log("ws close");
				socket.onerror = () => console.error("ws error");
				socket.onmessage = handleWsMessage;

				this.socket = socket;
			},
			startMultiplayer() {
				fetch("/api/start_multiplayer", {
					method: "POST",
					body: JSON.stringify({
						lobby: username,
					}),
					headers: {
						'Content-Type': 'application/json',
						'Csrf-Token': getCookieByName('play-csrf-token'),
					},
				}).then(() => console.log("started")).catch(console.error);
			},
			async updateLobbies() {
				this.lobbies = await fetch('/api/lobbies')
					.then(r => r.json())
					.catch(e => console.error('Error fetching lobbies: ', e));
			}
		}
	}).mount('#start-body')
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

function start_multiplayer_ws(username) {
	const socket = new WebSocket(`ws://localhost:9000/api/multiplayer_websocket?username=${username}&lobby=${username}`)
	socket.onopen = () => {
		console.log("ws open");
	};
	socket.onclose = () => console.log("ws close");
	socket.onerror = () => console.error("ws error");
	socket.onmessage = handleWsMessage;

	return socket;
}

async function start_multiplayer(lobby) {
	fetch("/api/start_multiplayer", {
		method: "POST",
		body: JSON.stringify({
			lobby: lobby,
		}),
		headers: {
			'Content-Type': 'application/json',
			'Csrf-Token': getCookieByName('play-csrf-token'),
		},
	}).then(() => console.log("started")).catch(console.error);
}

function handleWsMessage(m) {
	const msg = JSON.parse(m.data);

	switch (msg.type) {
		case "status": {
			console.log(msg.message);
			break;
		}
		case "won/lost": {
			console.log("won/lost");
			break;
		}
		case "update": {
			console.log("update");
			console.log(msg)
			break;
		}
		case "setup": {
			console.log("setup", msg.numPlayers);
			break;
		}
	}
}