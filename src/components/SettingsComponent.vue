<template>
	<form id="settings" @@submit.prevent="" v-if="state === 'singleplayer-settings' || state === 'create-lobby'">
		<h2>Board Settings</h2>
		<div id="settings-inputs">
			<label v-if="state === 'create-lobby'">Username:</label>
			<input v-model="username" v-if="state === 'create-lobby'" type="text" :invalid="!username" required />

			<label>Board Width:</label>
			<input v-model="width" type="number" :invalid="!width" required min="1" max="32" />

			<label>Board Height:</label>
			<input v-model="height" type="number" :invalid="!height" required min="1" max="32" />

			<label>Bomb Chance:</label>
			<div id="range-wrapper">
				<input type="range" v-model="bomb_chance" required step="0.01" min="0" max="1" />
				<output>{{ (bomb_chance * 100).toFixed(0) }}%</output>
			</div>

			<label>Max Undos:</label>
			<input type="number" v-model="max_undos" :invalid="max_undos===''" required min="0" max="10" />
		</div>

		<div id="settings-buttons">
			<button v-if="state === 'singleplayer-settings'" @click="startGame()" :disabled="!width || !height || max_undos===''">Start Singleplayer</button>
			<button v-if="state === 'create-lobby'" :disabled=createLobbyDisabled >Create Lobby</button>
			<button v-if="state === 'create-lobby'" :disabled=startMultiplayerDisabled >Start Game</button>
		</div>
	</form>
</template>

<script>
export default {
	name: 'SettingsComponent',
	props: {
		state: String
	},
	emits: ['state-changed'],
	setup(props, ctx) {
		function startGame() {
			ctx.emit('state-changed', 'singleplayer');
		}
		return {
			startGame,
		}
	}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
