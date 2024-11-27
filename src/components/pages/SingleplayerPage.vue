<template>
	<div id="game">
		<div id="stat-container">
			<h2>Time: {{elapsed}}s</h2>
			<h2>Undos: {{ undos }}</h2>
		</div>
		
		<div id="grids-container">
			<div v-if="end" id="end-overlay">
				<h1>You {{end}}!</h1>
				<button @click="retry()">Retry</button>
			</div>
			<div class="game-grid">
				<div v-for="(cell, index) in cells" :key="index" class="cell"
					:class="getCellImgClass(cell.cell).class" alt="cell" 
					width="32px" height="32px" :cell-x="cell.x" :cell-y="cell.y"
					@click="reveal($event)" @contextmenu.prevent="flag($event)">
				</div>
			</div>
		</div>
		
		<div id="button-container">
			<button @click="main_menu()">Main Menu</button>
			<button :disabled="end || undos === 0" @click="undo()">Undo</button>
			<button :disabled="end" @click="redo()">Redo</button>
		</div>
	</div>
</template>

<script>
export default {
	name: 'SingleplayerPage'
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
:root {
    --player-count: 1;
    --cell-size: clamp(16px, round(calc(5vw / var(--player-count)), 1px), 32px);
    --game-width: calc(var(--grid-width) * var(--cell-size));
}

#game {
    display: flex;
    flex-direction: column;
    align-items: center;
}

#stat-container {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 2rem;
}

#end-overlay {
    position: absolute;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: rgb(0 0 0 / 70%);
    width: calc(var(--grid-height) * var(--cell-size));
    height: calc(var(--grid-height) * var(--cell-size));
    margin: 1rem;
    z-index: 1;
}

#grids-container {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    align-items: center;
    gap: 1rem;
}

.game-grid {
    display: grid;
    width: var(--game-width);
    grid-template-columns: repeat(var(--grid-width), var(--cell-size));
    grid-template-rows: repeat(var(--grid-height), var(--cell-size));
    padding: calc(var(--cell-size) / 2);
    background-color: var(--dark-color);
    border-radius: calc(var(--cell-size) / 2);
}

.cell {
    width: var(--cell-size);
    height: var(--cell-size);
    background-size: cover;
    background-repeat: no-repeat;
}

.cell:hover {
    filter: brightness(120%);
}

.cell.unrevealed { background-image: url(/public/images/unrevealed.png); }
.cell.revealed { background-image: url(/public/images/revealed.png); }
.cell.bomb { background-image: url(/public/images/bomb.png); }
.cell.flagged { background-image: url(/public/images/flagged.png); }
.cell.revealed-1 { background-image: url(/public/images/1.png); }
.cell.revealed-2 { background-image: url(/public/images/2.png); }
.cell.revealed-3 { background-image: url(/public/images/3.png); }
.cell.revealed-4 { background-image: url(/public/images/4.png); }
.cell.revealed-5 { background-image: url(/public/images/5.png); }
.cell.revealed-6 { background-image: url(/public/images/6.png); }
.cell.revealed-7 { background-image: url(/public/images/7.png); }
.cell.revealed-8 { background-image: url(/public/images/8.png); }

#button-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-top: 2rem;
    width: var(--grid-width);
    gap: 10px;
}

#button-container button {
    text-wrap: nowrap;
    width: 100%
}
</style>
