<template>
	<div id="lobby-list">
		<h1>Lobbies</h1>
		<div id="opts">
			<label>Join as: </label>
			<input type="text" v-model="username" required />
			<button @click="updateLobbies">Refresh List</button>
		</div>
		<div id="cards">
			<LobbyCard v-for="lobby in lobbies" :key="lobby.username" :lobby="lobby" :username="username" />
			<p v-if="!lobbies || lobbies.length === 0">No lobbies found</p>
		</div>
		<RouterLink to="/" class="btn">Go back</RouterLink>
	</div>
</template>

<script>
import LobbyCard from '../LobbyCard.vue';
import { auth } from '../../firebaseConfig';

export default {
	name: 'JoinLobbyPage',
	components: {
		LobbyCard
	},
	data() {
		return {
			username: auth.currentUser ? auth.currentUser.displayName : "",
			lobbies: [],
		}
	},
	mounted() {
		this.updateLobbies()
	},
	methods: {
		async updateLobbies() {
			this.lobbies = await fetch('/api/lobbies')
				.then(r => r.json())
				.catch(e => console.error('Error fetching lobbies: ', e));
		}
	}
}

</script>

<style scoped>
#lobby-list {
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;

	#opts {
		height: 2rem;
		display: flex;
		flex-direction: row;
		align-items: center;
		gap: 1rem;
		margin-bottom: 1rem;

		input {
			height: 1rem;
		}
	}
}

#cards {
	width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 1rem;
}
</style>
