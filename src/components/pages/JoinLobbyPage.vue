<template>
	<div id="lobby-list">
		<h1>Lobbies</h1>
		<div id="opts">
			<label>Join as: </label>
			<input type="text" v-model="username" required />
			<button @click="updateLobbies">Refresh List</button>
		</div>
		<div id="cards">
			<div v-for="lobby in lobbies" :key="lobby.username" class="lobby-card">
				<span>{{lobby.name}}'s Lobby</span>
				<span>{{lobby.players.length}} Players</span>
				<button @click="joinLobby(lobby.name)" :disabled="!username">Join</button>
			</div>
			<p v-if="lobbies.length === 0">No lobbies found</p>
		</div>
	</div>
</template>

<script>
export default {
	name: 'JoinLobbyPage',
	data() {
		return {
			lobbies: [],
		}
	},
	mounted() {
		this.updateLobbies()
	},
	methods: {
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
		async updateLobbies() {
			this.lobbies = await fetch('/api/lobbies')
				.then(r => r.json())
				.catch(e => console.error('Error fetching lobbies: ', e));
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
#lobby-list {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}

#lobby-list #opts {
    height: 2rem;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1rem;
}

#lobby-list #opts input { height: 1rem; }

#cards {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
}

#cards .lobby-card {
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

#cards .lobby-card span {
    align-content: center;
    padding: .5rem;
    border-right: 1px var(--highlight-color) solid;
}

#cards .lobby-card button {
    height: 2rem;
    width: 5rem;
    margin: auto;
}
</style>
