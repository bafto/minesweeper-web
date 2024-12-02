<template>
	<form id="settings" @submit.prevent="">
		<h2>Board Settings</h2>
		<div id="settings-inputs">
			<label v-if="multiplayer">Username:</label>
			<input v-if="multiplayer" v-model="username" type="text" required />

			<label>Board Width:</label>
			<input v-model="width" type="number" required min="1" max="32" />

			<label>Board Height:</label>
			<input v-model="height" type="number" required min="1" max="32" />

			<label>Bomb Chance:</label>
			<div id="range-wrapper">
				<input type="range" v-model="bomb_chance" required step="0.01" min="0" max="1" />
				<output>{{ (bomb_chance * 100).toFixed(0) }}%</output>
			</div>

			<label>Max Undos:</label>
			<input type="number" v-model="max_undos" :invalid="max_undos===''" required min="0" max="10" />
		</div>

		<div id="settings-buttons">
			<RouterLink to="/singleplayer" v-if="!multiplayer" @click="startGame()" :disabled="disableButtons" class="btn">Start Singleplayer</RouterLink>
			<button v-if="multiplayer" @click=selectMultiplayer() :disabled="disableButtons" class="btn">Create Lobby</button>
			<button v-if="multiplayer" @click=startMultiplayer() :disabled="disableButtons" class="btn">Start Game</button>
		</div>
	</form>
</template>

<script>
import GameSocket from '../../websocket.js';

export default {
	name: 'SettingsComponent',
	props: {
		multiplayer: Boolean
	},
	data() {
		return {
			username: "dummy",
			width: 10,
			height: 10,
			bomb_chance: 0.5,
			max_undos: 3,
		}
	},
	computed: {
		disableButtons() {
			return !(this.width && this.height && this.max_undos && this.username.trim())
		},
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
			}).catch(console.error);
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
					start_multiplayer_ws(this.username, this);
				}
			}).catch(console.error);
		},
		startMultiplayer() {
			fetch("/api/start_multiplayer", {
				method: "POST",
				body: JSON.stringify({
					lobby: this.username,
				}),
				headers: {
					'Content-Type': 'application/json',
					'Csrf-Token': getCookieByName('play-csrf-token'),
				},
			}).then(() => console.log("started")).catch(console.error);
		}
	}
}

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

function start_multiplayer_ws(username, self) {
	const socket = GameSocket.Connect(`ws://localhost:9000/api/ws/multiplayer?username=${username}&lobby=${username}`);
	socket.onopen = () => {
		console.log("ws open");
	};
	socket.onclose = () => console.log("ws close");
	socket.onerror = () => console.error("ws error");
	socket.onmessage = (msg) => handleWsMessage(msg, self);

	return socket;
}

function handleWsMessage(m, self) {
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

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#settings {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    --inputs-grid: 3fr 2fr;
}

#settings-inputs {
    display: grid;
    grid-template-columns: var(--inputs-grid);
    width: 60%;
    margin-bottom: 2rem;
    row-gap: 5px;
}

#range-wrapper {
    display: grid;
    grid-template-columns: auto 4ch;
    gap: 10px;
    align-items: center;
}

#settings-buttons {
    display: flex;
    flex-direction: row;
    justify-content: center;
    gap: 2rem;
    width: 60%;
}

#settings-buttons .btn {
    width: 100%;
    text-wrap: nowrap;
}

input:user-invalid {
    border-color: red !important;
    background-color: rgb(161, 77, 77) !important;
}

@media screen and (max-width: 500px) {
    #settings-inputs {
        grid-template-columns: none;
        grid-template-rows: var(--inputs-grid);
    }
    #header-title h1 {
        font-size: 1.5rem;
    }
}

@media screen and (max-width: 320px) {
    #settings-buttons button {
        text-wrap: wrap;
    }
    #header-title h1 {
        display: none;
    }
}
</style>
