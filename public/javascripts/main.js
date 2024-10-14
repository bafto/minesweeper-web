document.addEventListener('DOMContentLoaded', () => {
	const time = document.getElementById('time');
	let elapsed = Number.parseInt(time.innerHTML);
	setInterval(() => {
		elapsed = elapsed + 1;
		time.innerHTML = `${elapsed}`;
	}, 1000);
})

function reload_page() {
	window.location.reload();
}

function reveal(cell) {
	console.log('click');
	fetch(`/api/reveal?x=${cell.getAttribute('x')}&y=${cell.getAttribute('y')}`)
		.then(reload_page);
}

async function flag(cell) {
	console.log('flag');
	fetch(`/api/flag?x=${cell.getAttribute('x')}&y=${cell.getAttribute('y')}`)
		.then(reload_page);
	return false;
}

function main_menu() {
	fetch('/api/restart')
		.then(reload_page);
}

function retry() {
	fetch('/api/retry')
		.then(reload_page);
}

function undo() {
	fetch('/api/undo')
		.then(reload_page);
}
function redo() {
	fetch('/api/redo')
		.then(reload_page);
}
