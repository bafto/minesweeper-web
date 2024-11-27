<template>
	<div class="game-grid">
		<div v-for="cell in cells" :key="cell.x+cell.y" class="cell"
			:class="getCellImgClass(cell.cell)" alt="cell" 
			width="32px" height="32px" :cell-x="cell.x" :cell-y="cell.y"
			@click="reveal($event)" @contextmenu.prevent="flag($event)">
		</div>
	</div>
</template>

<script>
export default {
	name: "GameComponent",
	props: {
		gameSocket: {
            type: Object,
            required: true,
        },
	},
	data() {
		return {
			cells: []
		}
	},
	methods: {
		reveal(event) {
			const xy = getCellXY(event.target);
			this.gameSocket.send(JSON.stringify({
				type: "reveal",
				data: {
					x: Number.parseInt(xy.x),
					y: Number.parseInt(xy.y),
				}
			}));
		},
		flag(event) {
			const xy = getCellXY(event.target);
			this.gameSocket.send(JSON.stringify({
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

</style>