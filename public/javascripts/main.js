function reload_page() {
	window.location.reload();
}

function retry() {
	fetch('/api/retry').then(reload_page);
}
