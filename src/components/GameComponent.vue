<template>
	<div class="game-grid" :aria-disabled="!inputEnabled">
		<div v-for="cell in cells" :key="cell.x+cell.y" class="cell"
			:class="getCellImgClass(cell.cell)" alt="cell" 
			width="32px" height="32px" :cell-x="cell.x" :cell-y="cell.y"
			@click="reveal($event)" @contextmenu.prevent="flag($event)">
		</div>
	</div>
</template>

<script>
import GameSocket from '../websocket.js';

export default {
	name: "GameComponent",
	props: {
		inputEnabled: Boolean,
	},
	data() {
		return {
			cells: []
		}
	},
	methods: {
		reveal(event) {
			if (!this.inputEnabled) {
				return;
			}

			const xy = getCellXY(event.target);
			GameSocket.Get().send(JSON.stringify({
				type: "reveal",
				data: {
					x: Number.parseInt(xy.x),
					y: Number.parseInt(xy.y),
				}
			}));
		},
		flag(event) {
			if (!this.inputEnabled) {
				return;
			}

			const xy = getCellXY(event.target);
			GameSocket.Get().send(JSON.stringify({
				type: "flag",
				data: {
					x: Number.parseInt(xy.x),
					y: Number.parseInt(xy.y),
				}
			}));
			return false;
		},
		getCellImgClass(cell) {
			if (cell.isRevealed) {
				if (cell.isBomb) {
					return "bomb"
				} else if (cell.nearbyBombs === 0) {
					return "revealed"
				} else {
					return `revealed-${cell.nearbyBombs}`
				}
			} else if (cell.isFlagged) {
				return "flagged"
			} else {
				return "unrevealed"
			}
		}
	}
}

function getCellXY(cell) {
	return {
		x: cell.getAttribute('cell-x'),
		y: cell.getAttribute('cell-y')
	};
}
</script>

<style scoped>

.game-grid {
	display: grid;
	width: var(--game-width);
	grid-template-columns: repeat(var(--grid-width), var(--cell-size));
	grid-template-rows: repeat(var(--grid-height), var(--cell-size));
	padding: calc(var(--cell-size) / 2);
	background-color: var(--dark-color);
	border-radius: calc(var(--cell-size) / 2);
	margin-top: 5px;

	&[aria-disabled=true] {
		filter: brightness(20%) contrast(0.9);
		pointer-events: none;
	}
}

.cell {
	width: var(--cell-size);
	height: var(--cell-size);
	background-size: cover;
	background-repeat: no-repeat;

	&.unrevealed { background-image: url(/assets/images/unrevealed.png); }
	&.revealed { background-image: url(/assets/images/revealed.png); }
	&.bomb { background-image: url(/assets/images/bomb.png); }
	&.flagged { background-image: url(/assets/images/flagged.png); }
	&.revealed-1 { background-image: url(/assets/images/1.png); }
	&.revealed-2 { background-image: url(/assets/images/2.png); }
	&.revealed-3 { background-image: url(/assets/images/3.png); }
	&.revealed-4 { background-image: url(/assets/images/4.png); }
	&.revealed-5 { background-image: url(/assets/images/5.png); }
	&.revealed-6 { background-image: url(/assets/images/6.png); }
	&.revealed-7 { background-image: url(/assets/images/7.png); }
	&.revealed-8 { background-image: url(/assets/images/8.png); }

	&:hover {
		filter: brightness(120%);
	}
}
</style>