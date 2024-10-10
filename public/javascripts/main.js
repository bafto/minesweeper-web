function reveal(cell) {
	console.log('click');
	fetch(`/reveal?x=${cell.getAttribute('x')}&y=${cell.getAttribute('y')}`).then(() => {
		window.location.reload()
	})
}

function retry() {
	fetch('/retry').then(() => {
		window.location.reload()
	})
}
