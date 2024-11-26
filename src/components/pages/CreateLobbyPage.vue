<template>
	<SettingsComponent lobbies="lobbies" state="state"/>
</template>

<script>
import SettingsComponent from '../SettingsComponent.vue';

export default {
	name: 'JoinLobbyPage',
	props: {
		state: String,
		lobbies: Array,
	},
	components: {
		SettingsComponent
	}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
