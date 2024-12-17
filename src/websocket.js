class GameSocket {
	constructor() {
		if (process.env.NODE_ENV === 'production') {
			this.host = 'wss://minesweeper.bafto.dev';
		} else {
			this.host = 'ws://localhost:9000'
		}
	}

	Connect(url) {
		if (this.socket) {
			this.socket.close();
		}
		this.socket = new WebSocket(this.host + url);
		return this.socket;
	}
	Get() {
		return this.socket;
	}
}

const instance = new GameSocket();

export default instance;
