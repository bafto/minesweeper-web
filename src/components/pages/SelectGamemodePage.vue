<template>
  <div id="start-body">
	<div id="mode-select">
		<h1>Select a game mode</h1>
		<router-link to="/start-singleplayer" class="btn">Play Singleplayer</router-link>
		<router-link to="/create-lobby" class="btn">Create Multiplayer Lobby</router-link>
		<router-link to="/join-lobby" class="btn">Join Multiplayer Lobby</router-link>
	</div>
  </div>
</template>

<script>
export default {
  name: 'SelectGamemodePage'
}
</script>

<style scoped>
#start-body {
    height: 100%;
}

#mode-select {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    gap: 1rem;
}

#mode-select :is(button, .btn) {
    width: 40%;
}
</style>
