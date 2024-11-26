<template>
	<JoinLobbyPage v-if="state === 'list-lobbies'" :state="state" @state-changed="handleStateChanged"/>
	<SelectGamemodePage v-if="state === 'mode-select'" :state="state" @state-changed="handleStateChanged" />
	<SingleplayerPage v-if="state === 'singleplayer'" :state="state" @state-changed="handleStateChanged" />
	<CreateLobbyPage v-if="state === 'create-lobby'" :state="state" @state-changed="handleStateChanged" />
	<SettingsComponent v-if="state === 'singleplayer-settings' || state == 'create-lobby'" :state="state" @state-changed="handleStateChanged" />
</template>

<script>
import SelectGamemodePage from './components/pages/SelectGamemodePage.vue';
import CreateLobbyPage from './components/pages/CreateLobbyPage.vue';
import SingleplayerPage from './components/pages/SingleplayerPage.vue';
import JoinLobbyPage from './components/pages/JoinLobbyPage.vue';
import SettingsComponent from './components/SettingsComponent.vue';

export default {
	name: 'App',
	components: {
		SelectGamemodePage,
		CreateLobbyPage,
		SingleplayerPage,
		JoinLobbyPage,
		SettingsComponent,
	},
	data() {
		return {
			lobbies: [],
			state: "mode-select",
			username: "",
			width: 10,
			height: 10,
			bomb_chance: 0.5,
			max_undos: 3,
			socket: null
		};

	},
	methods: {
		handleStateChanged(newState) {
			console.log('oldState = ' + this.state);
			console.log('newState = ' + newState);
			this.state = newState;
		}
	}
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
