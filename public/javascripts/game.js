let elapsed = 0;
const timeElement = document.getElementById('time');
let gameSocket = null;

function createTimer(startTime) {
	elapsed = startTime;
	return setInterval(() => {
		elapsed = elapsed + 1;
		timeElement.innerHTML = `${elapsed}`;
	}, 1000);
}

document.addEventListener('DOMContentLoaded', () => {
	gameSocket = connectToWs();
})

const grid = document.getElementById('grid-container');

async function updateGame(state) {
	if (!grid.hasChildNodes()) {
		createGrid(state);
		return;
	}

	// update the cells
	for (const cell of state.cells) {
		const cellImg = document.querySelector(`[x="${cell.x}"][y="${cell.y}"]`)
		const cellData = getCellImgClass(cell.cell)
		cellImg.setAttribute("src", cellData.img);
		cellImg.className = "cell " + cellData.class;
	}

	// sync the timer with the server timer
	elapsed = state.timer;

	// update the undos
	document.getElementById('undos').innerText = state.undos;
}

function createGrid(state) {
	// update the cells
	for (const cell of state.cells) {
		const cellImg = document.createElement('img');
		const cellData = getCellImgClass(cell.cell)

		cellImg.setAttribute("src", cellData.img);
		cellImg.className = "cell " + cellData.class;

		cellImg.setAttribute('alt', 'cell');
		cellImg.setAttribute('width', '32px');
		cellImg.setAttribute('height', '32px');
		cellImg.setAttribute('x', cell.x);
		cellImg.setAttribute('y', cell.y);

		cellImg.addEventListener('click', (e) => reveal(e.target));
		if (cellData.class === 'unrevealed' || cellData.class === 'flagged') {
			cellImg.addEventListener('contextmenu', (e) => flag(e));
		}

		grid.appendChild(cellImg);
	}

	createTimer(state.timer);

	// update the undos
	document.getElementById('undos').innerText = state.undos;

	const root = document.querySelector(':root');
	root.style.setProperty('--grid-width', state.width);
	root.style.setProperty('--grid-height', state.height);
}

function getCellImgClass(cell) {
	if (cell.isRevealed) {
		if (cell.isBomb) {
			return {
				img: imageBasePath + "bomb.png",
				class: "bomb",
			};
		} else if (cell.nearbyBombs === 0) {
			return {
				img: imageBasePath + "revealed.png",
				class: "revealed"
			};
		} else {
			return {
				img: imageBasePath + cell.nearbyBombs + ".png",
				class: `revealed-${cell.nearbyBombs}`
			};
		}
	} else if (cell.isFlagged) {
		return {
			img: imageBasePath + "flagged.png",
			class: "flagged"
		};
	} else {
		return {
			img: imageBasePath + "unrevealed.png",
			class: "unrevealed"
		};
	}
}

function getCellXY(cell) {
	return {
		x: cell.getAttribute('x'),
		y: cell.getAttribute('y')
	};
}

async function reveal(cell) {
	const xy = getCellXY(cell);
	gameSocket.send(JSON.stringify({
		type: "reveal",
		data: {
			x: Number.parseInt(xy.x),
			y: Number.parseInt(xy.y),
		}
	}));
}

async function flag(event) {
	event.preventDefault();
	const cell = event.target;
	const xy = getCellXY(cell);
	gameSocket.send(JSON.stringify({
		type: "flag",
		data: {
			x: Number.parseInt(xy.x),
			y: Number.parseInt(xy.y),
		}
	}));
	return false;
}

async function undo() {
	gameSocket.send(JSON.stringify({
		"type": "undo"
	}));
}

async function redo() {
	gameSocket.send(JSON.stringify({
		"type": "redo"
	}));
}

function connectToWs() {
	const socket = new WebSocket("ws://localhost:9000/api/ws")
	socket.onopen = () => {
		console.log("ws open");
		socket.send(JSON.stringify({ type: "open" }));
	};
	socket.onclose = () => console.log("ws close");
	socket.onerror = () => console.error("ws error");
	socket.onmessage = handleWsMessage;

	return socket;
}

function handleWsMessage(msg) {
	const state = JSON.parse(msg.data);

	if (state.reload) {
		reload_page();
		return;
	}

	updateGame(state);
}
