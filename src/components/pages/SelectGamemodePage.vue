<template>
	<div id="mode-select">
		<h1>Select a game mode</h1>
		<div v-if="!isOnline">
			<h1>You are Offline!</h1>
			<p>Wait until you are online to play the game!</p>
		</div>
		<RouterLink v-if="isOnline" to="/start-singleplayer" class="btn">Play Singleplayer</RouterLink>
		<RouterLink v-if="isOnline" to="/create-lobby" class="btn">Create Multiplayer Lobby</RouterLink>
		<RouterLink v-if="isOnline" to="/join-lobby" class="btn">Join Multiplayer Lobby</RouterLink>
	</div>
</template>

<script>
export default {
	name: 'SelectGamemodePage',
	data() {
		return {
			isOnline: navigator.onLine,
		};
	},
	mounted() {
		window.addEventListener('online', this.updateOnlineStatus);
		window.addEventListener('offline', this.updateOnlineStatus);
	},
	methods: {
		// Update the isOnline property based on navigator.onLine
		updateOnlineStatus() {
			this.isOnline = navigator.onLine;
		},
	},
}
</script>

<style scoped>
#mode-select {
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	height: 100%;
	gap: 1rem;

	:is(button, .btn) {
		width: 40%;
	}
}
</style>
