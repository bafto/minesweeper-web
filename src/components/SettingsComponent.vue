<template>
	<form id="settings" @submit.prevent="">
		<h2>Board Settings</h2>
		<div id="settings-inputs">
			<label>Username:</label>
			<input v-model="username" type="text" :invalid="!username" required />

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
			<button v-if="multiplayer === 'false'" @click="startGame()" :disabled="!width || !height || max_undos===''">Start Singleplayer</button>
			<button v-if="multiplayer === 'true'" :disabled=createLobbyDisabled >Create Lobby</button>
			<button v-if="multiplayer === 'true'" :disabled=startMultiplayerDisabled >Start Game</button>
		</div>
	</form>
</template>

<script>
export default {
	name: 'SettingsComponent',
	props: {
		multiplayer: Boolean
	},
	data() {
		return {
			username: "",
			width: 10,
			height: 10,
			bomb_chance: 0.5,
			max_undos: 3,
		}
	}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#settings {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    --inputs-grid: 3fr 2fr;
}

#settings-inputs {
    display: grid;
    grid-template-columns: var(--inputs-grid);
    width: 60%;
    margin-bottom: 2rem;
    row-gap: 5px;
}

#range-wrapper {
    display: grid;
    grid-template-columns: auto 4ch;
    gap: 10px;
    align-items: center;
}

#settings-buttons {
    display: flex;
    flex-direction: row;
    justify-content: center;
    gap: 2rem;
    width: 60%;
}

#settings-buttons button {
    width: 100%;
    text-wrap: nowrap;
}

[invalid=true] {
    border-color: red !important;
    background-color: rgb(161, 77, 77) !important;
}

@media screen and (max-width: 500px) {
    #settings-inputs {
        grid-template-columns: none;
        grid-template-rows: var(--inputs-grid);
    }
    #header-title h1 {
        font-size: 1.5rem;
    }
}

@media screen and (max-width: 320px) {
    #settings-buttons button {
        text-wrap: wrap;
    }
    #header-title h1 {
        display: none;
    }
}
</style>
