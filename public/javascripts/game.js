let elapsed = 0;
const timeElement = document.getElementById('time');

function createTimer(startTime) {
	elapsed = startTime;
	return setInterval(() => {
		elapsed = elapsed + 1;
		timeElement.innerHTML = `${elapsed}`;
	}, 1000);
}

document.addEventListener('DOMContentLoaded', () => {
	createTimer(Number.parseInt(timeElement.innerHTML));
})

async function updateGame(state) {
	// if won/lost, reload the page to get to the
	// won/lost views
	if (state.won || state.lost) {
		reload_page();
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
	const resp = await fetch(`/api/reveal?x=${xy.x}&y=${xy.y}`);
	if (!resp.ok) {
		console.err("something went wrong revealing!");
		return;
	}
	const state = await resp.json();
	updateGame(state);
}

async function flag(cell) {
	const xy = getCellXY(cell);
	const resp = await fetch(`/api/flag?x=${xy.x}&y=${xy.y}`);
	if (!resp.ok) {
		console.err("something went wrong flagging!");
		return;
	}
	const state = await resp.json();
	updateGame(state);
	return false;
}

async function undo() {
	const resp = await fetch('/api/undo');
	if (!resp.ok) {
		console.err("something went wrong undoing!");
		return;
	}
	const state = await resp.json();
	updateGame(state);
}

async function redo() {
	const resp = await fetch('/api/redo');
	if (!resp.ok) {
		console.err("something went wrong redoing!");
		return;
	}
	const state = await resp.json();
	updateGame(state);
}
