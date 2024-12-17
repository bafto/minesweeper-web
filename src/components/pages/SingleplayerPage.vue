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
			<GameComponent ref="game_grid" :inputEnabled="true"></GameComponent>
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
	name: 'SingleplayerPage',
	components: {
		GameComponent
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
		const gameSocket = GameSocket.Connect("/api/ws/singleplayer")
		gameSocket.onopen = () => {
			console.log("ws open");
			gameSocket.send(JSON.stringify({ type: "open" }));
		};
		gameSocket.onclose = () => console.log("ws close");
		gameSocket.onerror = () => console.error("ws error");
		gameSocket.onmessage = (msg) => handleWsMessage(msg, self);
	},
	methods: {
		main_menu() {
			fetch('/api/restart');
			this.$router.push({path: '/'});
		},
		retry() {
			const self = this;
			fetch('/api/retry').then(() => {
				self.end = undefined;
			})
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
	if (self.$refs[gameID].cells.length === 0) {
		const root = document.querySelector(':root');
		root.style.setProperty('--grid-width', state.width);
		root.style.setProperty('--grid-height', state.height);

		// create timer
		setInterval(() => {
			if (!self.end) {
				self.elapsed = self.elapsed + 1;
			}
		}, 1000);
	}

	// update the cells
	self.$refs[gameID].cells = state.cells

	// sync the timer with the server timer
	self.elapsed = state.timer;

	// update the undos
	self.undos = state.undos;
}

function handleWsMessage(msg, self) {
	const state = JSON.parse(msg.data);

	if (state.end) {
		self.end = state.end;
		return;
	}

	if (state.reload) {
		return;
	}

	updateGame(state, self, 'game_grid');
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
    width: var(--grid-width);
    gap: 10px;
}

#button-container button {
    text-wrap: nowrap;
    width: 100%
}
</style>
