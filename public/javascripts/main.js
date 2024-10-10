function reveal(cell) {
	console.log('click');
	fetch(`/reveal?x=${cell.getAttribute('x')}&y=${cell.getAttribute('y')}`)
}

function retry() {
	fetch('/retry')
}
