<template>
	<div class="lobby-card">
		<span>{{this.lobby.name}}'s Lobby</span>
		<span>{{this.lobby.players.length}} Players</span>
		<button @click="joinLobby()" :disabled="!username">Join</button>
	</div>
</template>

<script>
import GameSocket from '../websocket.js';

export default {
	name: "LobbyCard",
	props: {
		lobby: {
            type: Object,
            required: true,
        },
		username: {
			type: String,
			required: true
		}
	},
	methods: {
		joinLobby() {
			if (GameSocket.Get()) {
				return;
			}

			const socket = GameSocket.Connect(`/api/ws/multiplayer?username=${this.username}&lobby=${this.lobby.name}`)
			socket.onopen = () => {
				console.log("ws open");
			};
			socket.onclose = () => console.log("ws close");
			socket.onerror = () => console.error("ws error");
			socket.onmessage = (msg) => handleWsMessage(msg, this);
		}
	}
}


function handleWsMessage(m, self) {
	const msg = JSON.parse(m.data);

	switch (msg.type) {
		case "status": {
			console.log(msg.message);
			break;
		}
		case "setup": {
			console.log("setup", msg.players);
			self.$router.push({
				path: "/multiplayer",
				query: {players: msg.players, username: self.username },
			});
			break;
		}
		case "error": {
			console.error(msg.message)
		}
	}
}
</script>

<style scoped>

.lobby-card {
    display: grid;
    width: 90%;
    height: 2rem;
    padding: .5rem;
    grid-template-columns: 3fr 1fr 1fr;
    align-items: center;
    background-color: var(--dark-color);
    border-radius: 1rem;
    border: var(--medium-color) inset 1px;
}

.lobby-card span {
    align-content: center;
    padding: .5rem;
    border-right: 1px var(--highlight-color) solid;
}

.lobby-card button {
    height: 2rem;
    width: 5rem;
    margin: auto;
}

</style>