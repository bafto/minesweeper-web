<template>
	<div id="game">
		<div id="stat-container">
			<h2>Time: {{elapsed}}s</h2>
			<h2>Undos: {{ undos }}</h2>
		</div>
		
		<div id="grids-container">
			<div v-if="end" id="end-overlay">
				<h1>You {{end}}!</h1>
				<button @click="retry()">Retry</button>
			</div>
			<div v-for="player in players" :key="player">
				<GameComponent :ref="player" :inputEnabled="player === username"></GameComponent>
			</div>
		</div>
		
		<div id="button-container">
			<button @click="main_menu()">Main Menu</button>
			<button :disabled="end || undos === 0" @click="undo()">Undo</button>
			<button :disabled="end" @click="redo()">Redo</button>
		</div>
	</div>
</template>

<script>

import GameComponent from '../GameComponent.vue';
import GameSocket from '../../websocket.js';

export default {
	name: 'MultiplayerPage',
	components: {
		GameComponent
	},
	props: {
		username: String,
		players: Array,
	},
	data() {
		return {
			elapsed: 0,
			undos: 0,
			end: undefined
		}
	},
	created() {
		let self = this;
		GameSocket.Get().onmessage = (msg) => handleWsMessage(msg, self);

		// create timer
		setInterval(() => {
			if (!self.end) {
				self.elapsed = self.elapsed + 1;
			}
		}, 1000);
	},
	methods: {
		main_menu() {
			fetch('/api/restart')
		},
		undo() {
			GameSocket.Get().send(JSON.stringify({
				"type": "undo"
			}));
		},
		redo() {
			GameSocket.Get().send(JSON.stringify({
				"type": "redo"
			}));
		}
	}
}

async function updateGame(state, self, gameID) {
	if (self.$refs[gameID][0].cells.length === 0) {
		const rootStyle = document.querySelector(':root').style;
		rootStyle.setProperty('--player-count', self.players.length);
		rootStyle.setProperty('--grid-width', state.width);
		rootStyle.setProperty('--grid-height', state.height);
	}

	// update the cells
	self.$refs[gameID][0].cells = state.cells;

	// sync the timer with the server timer
	self.elapsed = state.timer;

	// update the undos
	self.undos = state.undos;
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
			if (msg.reload) {
				reload_page();
				return;
			}

			if (msg.end) {
				self.end = msg.end;
				return;
			}

			updateGame(msg, self, msg.username);
			break;
		}
		case "error": {
			console.error(msg.message)
		}
	}
}

function reload_page() {
	window.location.reload();
}
</script>

<style>
:root {
    --player-count: 1;
    --cell-size: clamp(16px, round(calc(5vw / var(--player-count)), 1px), 32px);
    --game-width: calc(var(--grid-width) * var(--cell-size));
}
</style>
<style scoped>
#game {
    display: flex;
    flex-direction: column;
    align-items: center;
}

#stat-container {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 2rem;
}

#end-overlay {
    position: absolute;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: rgb(0 0 0 / 70%);
    width: calc(var(--grid-height) * var(--cell-size));
    height: calc(var(--grid-height) * var(--cell-size));
    margin: 1rem;
    z-index: 1;
}

#grids-container {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;
    gap: 1rem;
}

#button-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-top: 2rem;
    width: calc((var(--game-width) + 1rem) * var(--player-count) + 1rem);
    gap: 10px;
}

#button-container button {
    text-wrap: nowrap;
    width: 100%
}
</style>
