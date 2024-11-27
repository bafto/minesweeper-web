<template>
	<div class="lobby-card">
		<span>{{this.lobby.name}}'s Lobby</span>
		<span>{{this.lobby.players.length}} Players</span>
		<button @click="joinLobby()" :disabled="!username">Join</button>
	</div>
</template>

<script>
export default {
	name: "LobbyCard",
	props: {
		lobby: {
            type: Object,
            required: true,
        },
	},
	methods: {
		joinLobby() {
			if (this.socket) {
				return;
			}

			const socket = new WebSocket(`ws://localhost:9000/api/multiplayer_websocket?username=${this.username}&lobby=${this.lobby.name}`)
			socket.onopen = () => {
				console.log("ws open");
			};
			socket.onclose = () => console.log("ws close");
			socket.onerror = () => console.error("ws error");
			socket.onmessage = handleWsMessage;

			this.socket = socket;
		}
	}
}


function handleWsMessage(msg) {
	const state = JSON.parse(msg.data);

	if (state.reload) {
		window.location.reload();
		return;
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