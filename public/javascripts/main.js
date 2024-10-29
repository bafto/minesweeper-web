function reload_page() {
	window.location.reload();
}

function main_menu() {
	fetch('/api/restart')
		.then(reload_page);
}

function retry() {
	fetch('/api/retry')
		.then(reload_page);
}
