<template>
	<div id="game">
		<div id="stat-container">
			<h2>Time: {{elapsed}}s</h2>
			<h2>Undos: {{ undos }}</h2>
		</div>
		
		<div id="grids-container">
			<div v-if="end" id="end-overlay">
				<h1>You {{end}}!</h1>
				<button @@click="retry()">Retry</button>
			</div>
			<div class="game-grid">
				<div v-for="(cell, index) in cells" :key="index" class="cell"
					:class="getCellImgClass(cell.cell).class" alt="cell" 
					width="32px" height="32px" :cell-x="cell.x" :cell-y="cell.y"
					@@click="reveal($event)" @@contextmenu.prevent="flag($event)">
				</div>
			</div>
		</div>
		
		<div id="button-container">
			<button @@click="main_menu()">Main Menu</button>
			<button :disabled="end || undos === 0" @@click="undo()">Undo</button>
			<button :disabled="end" @@click="redo()">Redo</button>
		</div>
	</div>
</template>

<script>
export default {
	name: 'SingleplayerPage',
	props: {
		state: String
	}

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
