<template>
	<div id="lobby-list">
		<h1>Lobbies</h1>
		<div id="opts">
			<label>Join as: </label>
			<input type="text" v-model="username" required />
			<button @click="updateLobbies">Refresh List</button>
		</div>
		<div id="cards">
			<LobbyCard v-for="lobby in lobbies" :key="lobby.username" :lobby="lobby" />
			<p v-if="lobbies.length === 0">No lobbies found</p>
		</div>
	</div>
</template>

<script>
import LobbyCard from '../LobbyCard.vue';

export default {
	name: 'JoinLobbyPage',
	components: {
		LobbyCard
	},
	data() {
		return {
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
}

#lobby-list #opts {
    height: 2rem;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1rem;
}

#lobby-list #opts input { height: 1rem; }

#cards {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
}
</style>
