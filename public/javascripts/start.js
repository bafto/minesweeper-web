async function startGame() {
	const width = document.getElementById('input-width').value;
	const height = document.getElementById('input-height').value;
	const bomb_chance = document.getElementById('input-bomb_chance').value;
	const max_undos = document.getElementById('input-max_undos').value;

	fetch('/api/start_game', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Csrf-Token': getCookieByName('play-csrf-token'),
		},
		body: JSON.stringify({
			width: Number.parseInt(width),
			height: Number.parseInt(height),
			bomb_chance: Number.parseFloat(bomb_chance),
			max_undos: Number.parseInt(max_undos)
		})
	}).then(reload_page).catch(console.error);
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
