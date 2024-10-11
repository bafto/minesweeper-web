function reload_page() {
	window.location.reload()
}

function reveal(cell) {
	console.log('click');
	fetch(`/reveal?x=${cell.getAttribute('x')}&y=${cell.getAttribute('y')}`)
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
