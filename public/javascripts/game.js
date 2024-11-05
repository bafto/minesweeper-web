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
	createTimer(Number.parseInt(timeElement.innerHTML));
	gameSocket = connectToWs();
})

async function updateGame(state) {
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

async function flag(cell) {
	event.preventDefault();
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
	socket.onopen = () => console.log("ws open");
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
