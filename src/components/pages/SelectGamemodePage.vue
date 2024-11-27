<template>
	<div id="mode-select">
		<h1>Select a game mode</h1>
		<RouterLink to="/start-singleplayer" class="btn">Play Singleplayer</RouterLink>
		<RouterLink to="/create-lobby" class="btn">Create Multiplayer Lobby</RouterLink>
		<RouterLink to="/join-lobby" class="btn">Join Multiplayer Lobby</RouterLink>
	</div>
</template>

<script>
export default {
  name: 'SelectGamemodePage'
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
}

#mode-select :is(button, .btn) {
    width: 40%;
}
</style>
