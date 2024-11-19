function getValidatedInput() {
	const width = document.getElementById('input-width');
	const height = document.getElementById('input-height');
	const bomb_chance = document.getElementById('input-bomb_chance');
	const max_undos = document.getElementById('input-max_undos');

	if (validate(width) | validate(height) | validate(max_undos)) {
		return false;
	}
	return {
		width: width,
		height: height,
		bomb_chance: bomb_chance,
		max_undos: max_undos,
	}
}

async function startGame() {
	const validated = getValidatedInput();
	if (validated === false) {
		return;
	}
	const { width, height, bomb_chance, max_undos } = validated;

	fetch('/api/start_game', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Csrf-Token': getCookieByName('play-csrf-token'),
		},
		body: JSON.stringify({
			width: Number.parseInt(width.value),
			height: Number.parseInt(height.value),
			bomb_chance: Number.parseFloat(bomb_chance.value),
			max_undos: Number.parseInt(max_undos.value)
		})
	}).then(reload_page).catch(console.error);
}

async function selectMultiplayer() {
	const validated = getValidatedInput();
	if (validated === false) {
		return;
	}
	const { width, height, bomb_chance, max_undos } = validated;

	const username = prompt("Enter username", "Max Mustermann");

	if (!username) {
		return;
	}

	fetch('/api/select_multiplayer', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Csrf-Token': getCookieByName('play-csrf-token'),
		},
		body: JSON.stringify({
			username: username,
			width: Number.parseInt(width.value),
			height: Number.parseInt(height.value),
			bomb_chance: Number.parseFloat(bomb_chance.value),
			max_undos: Number.parseInt(max_undos.value)
		})
	}).then(async (resp) => {
		const json = await resp.json();
		if (json.error) {
			console.error(error);
		} else {
			start_multiplayer_ws(username);
		}
	}).catch(console.error);
}

function validate(elm) {
	if (elm.value !== "") {
		elm.classList.remove("invalid");
		return false;
	}

	elm.classList.add("invalid");

	return true;
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

function handleWsMessage(m) {
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
			console.log("setup", msg.numPlayers);
			break;
		}
	}
}

function start_multiplayer_ws(username) {
	const socket = new WebSocket(`ws://localhost:9000/api/multiplayer_websocket?username=${username}&lobby=${username}`)
	socket.onopen = () => {
		console.log("ws open");
	};
	socket.onclose = () => console.log("ws close");
	socket.onerror = () => console.error("ws error");
	socket.onmessage = handleWsMessage;

	return socket;
}

async function start_multiplayer(lobby) {
	fetch("/api/start_multiplayer", {
		method: "POST",
		body: JSON.stringify({
			lobby: lobby,
		}),
		headers: {
			'Content-Type': 'application/json',
			'Csrf-Token': getCookieByName('play-csrf-token'),
		},
	}).then(() => console.log("started")).catch(console.error);
}

function joinLobby() {
	const username = prompt("enter your username", "Max Mustermann");
	const lobby = prompt("enter your the lobby username", "Max Mustermann");

	if (!username || !lobby) {
		return;
	}

	const socket = new WebSocket(`ws://localhost:9000/api/multiplayer_websocket?username=${username}&lobby=${lobby}`)
	socket.onopen = () => {
		console.log("ws open");
	};
	socket.onclose = () => console.log("ws close");
	socket.onerror = () => console.error("ws error");
	socket.onmessage = handleWsMessage;

	return socket;
}
