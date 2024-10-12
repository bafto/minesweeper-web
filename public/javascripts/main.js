document.addEventListener('DOMContentLoaded', () => {
	let time = document.getElementById('time');
	setInterval(() => time.innerHTML = `${Number.parseInt(time.innerHTML) + 1}`, 1000)
})

function reload_page() {
	window.location.reload()
}

function reveal(cell) {
	console.log('click');
	fetch(`/reveal?x=${cell.getAttribute('x')}&y=${cell.getAttribute('y')}`)
		.then(reload_page)
}

function main_menu() {
	fetch('/api/restart')
		.then(reload_page)
}

function retry() {
	fetch('/api/retry')
		.then(reload_page)
}

function undo() {
	fetch('/api/undo')
		.then(reload_page)
}
function redo() {
	fetch('/api/redo')
		.then(reload_page)
}
