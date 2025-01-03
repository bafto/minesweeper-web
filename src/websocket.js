class GameSocket {
	Connect(url) {
		if (this.socket) {
			this.socket.close();
		}
		this.socket = new WebSocket(url);
		return this.socket;
	}
	Get() {
		return this.socket;
	}
}

const instance = new GameSocket();

export default instance;
