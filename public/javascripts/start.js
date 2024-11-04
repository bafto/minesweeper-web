async function startGame() {
	const width = document.getElementById('input-width');
	const height = document.getElementById('input-height');
	const bomb_chance = document.getElementById('input-bomb_chance');
	const max_undos = document.getElementById('input-max_undos');

	if (validate(width) | validate(height) | validate(max_undos)) {
		return;
	}

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
