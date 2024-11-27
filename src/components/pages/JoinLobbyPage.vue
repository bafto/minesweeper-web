<template>
	<div id="lobby-list" v-if="state === 'list-lobbies'">
		<h1>Lobbies</h1>
		<div id="opts">
			<label>Join as: </label>
			<input type="text" v-model="username" required />
			<button @click="updateLobbies">Refresh List</button>
		</div>
		<div id="cards">
			<div v-for="lobby in lobbies" :key="lobby.username" class="lobby-card">
				<span>{{lobby.name}}'s Lobby</span>
				<span>{{lobby.players.length}} Players</span>
				<button @click="joinLobby(lobby.name)" :disabled="!username">Join</button>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	name: 'JoinLobbyPage',
	props: {
		lobbies: Array,
	}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
