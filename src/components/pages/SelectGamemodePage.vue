<template>
  <div id="start-body">
	<div v-if="state === 'mode-select'" id="mode-select">
		<h1>Select a game mode</h1>
		<button @click="$emit('state-changed', 'singleplayer-settings')">Play Singleplayer</button>
		<button @click="$emit('state-changed', 'create-lobby')">Create Multiplayer Lobby</button>
		<button @click="$emit('state-changed', 'list-lobby')"> Join Multiplayer Lobby</button>
	</div>
  </div>
</template>

<script>
export default {
  name: 'SelectGamemodePage',
	props: {
		state: String,
	}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
