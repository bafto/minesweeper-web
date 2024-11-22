let gameSocket = null;

document.addEventListener('DOMContentLoaded', () => {
	const { createApp, ref } = Vue

	createApp({
		created() {
			let self = this;
			gameSocket = new WebSocket("ws://localhost:9000/api/ws")
			gameSocket.onopen = () => {
				console.log("ws open");
				gameSocket.send(JSON.stringify({ type: "open" }));
			};
			gameSocket.onclose = () => console.log("ws close");
			gameSocket.onerror = () => console.error("ws error");
			gameSocket.onmessage = (msg) => handleWsMessage(msg, self);
		},
		data() {
			return {
				cells: [],
				elapsed: 0,
				undos: 0,
				end: undefined
			}
		},
		methods: {
			main_menu() {
				fetch('/api/restart').then(reload_page);
			},
			retry() {
				fetch('/api/retry').then(reload_page);
			},
			undo() {
				gameSocket.send(JSON.stringify({
					"type": "undo"
				}));
			},
			redo() {
				gameSocket.send(JSON.stringify({
					"type": "redo"
				}));
			},
			reveal(event) {
				const xy = getCellXY(event.target);
				gameSocket.send(JSON.stringify({
					type: "reveal",
					data: {
						x: Number.parseInt(xy.x),
						y: Number.parseInt(xy.y),
					}
				}));
			},
			flag(event) {
				const xy = getCellXY(event.target);
				gameSocket.send(JSON.stringify({
					type: "flag",
					data: {
						x: Number.parseInt(xy.x),
						y: Number.parseInt(xy.y),
					}
				}));
				return false;
			},
			getCellImgClass(cell) {
				if (cell.isRevealed) {
					if (cell.isBomb) {
						return {
							class: "bomb",
						};
					} else if (cell.nearbyBombs === 0) {
						return {
							class: "revealed"
						};
					} else {
						return {
							class: `revealed-${cell.nearbyBombs}`
						};
					}
				} else if (cell.isFlagged) {
					return {
						class: "flagged"
					};
				} else {
					return {
						class: "unrevealed"
					};
				}
			}
		}
	}).mount('#game')
})

function createTimer(startTime, self) {
	self.elapsed = startTime;
	return setInterval(() => {
		if (!self.end) {
			self.elapsed = self.elapsed + 1;
		}
	}, 1000);
}

async function updateGame(state, self) {
	if (document.querySelectorAll('.game-grid div').length === 0) {
		createGrid(state, self);
		return;
	}

	// update the cells
	self.cells = state.cells

	// sync the timer with the server timer
	self.elapsed = state.timer;

	// update the undos
	self.undos = state.undos;
}

function createGrid(state, self) {
	// update the cells
	self.cells = state.cells;

	createTimer(state.timer, self);

	// update the undos
	self.undos = state.undos;

	const root = document.querySelector(':root');
	root.style.setProperty('--grid-width', state.width);
	root.style.setProperty('--grid-height', state.height);
}

function getCellXY(cell) {
	return {
		x: cell.getAttribute('cell-x'),
		y: cell.getAttribute('cell-y')
	};
}

function handleWsMessage(msg, self) {
	const state = JSON.parse(msg.data);

	if (state.reload) {
		reload_page();
		return;
	}

	if (state.end) {
		self.end = state.end;
		return;
	}

	updateGame(state, self);
}

function reload_page() {
	window.location.reload();
}