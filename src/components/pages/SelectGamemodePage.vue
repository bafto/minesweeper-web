<template>
  <div id="start-body">
	<div>
		<h1>Select a game mode</h1>
		<router-link to="/singleplayer" class="btn">Play Singleplayer</router-link>
		<router-link to="/create-lobby" class="btn">Create Multiplayer Lobby</router-link>
		<router-link to="/lobby-list" class="btn">Join Multiplayer Lobby</router-link>
	</div>
  </div>
</template>

<script>
export default {
  name: 'SelectGamemodePage'
}
</script>

<style scoped>
</style>
