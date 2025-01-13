<template>
	<header>
		<div id="header-title">
			<router-link to="/">
				<img id="logo" src='/images/logo.png' alt="minesweeper logo" width="32px" height="32px" title="go back">
			</router-link>
			<h1>Minesweeper</h1>
			
		</div>
		<div id="header-right">
			<div id="user-control">
				<span>{{ currentUser === null ? "Not logged in" : "Hello, " + currentUser.displayName }}</span>
				<router-link to="/register" v-if="currentUser === null">Register or sign in</router-link>
				<a href="" v-if="currentUser !== null" @click="logout()">Log out</a>
			</div>
			<a href="https://github.com/bafto/minesweeper-web" target="_blank">
				<img invert src='/images/github.svg' alt="github logo" width="32px" height="32px" title="Github">
			</a>
		</div>
	</header>

	<main>
		<RouterView></RouterView>
	</main>

	<footer>
		<router-link to="/about">About Minesweeper</router-link>
	</footer>
</template>

<script>
import { auth } from './firebaseConfig';

export default {
	name: 'App',
	data() {
		return {
			lobbies: [],
			username: "",
			width: 10,
			height: 10,
			bomb_chance: 0.5,
			max_undos: 3,
			socket: null,
			currentUser: null
		};
	},
	created() {
		let a = this;
		auth.onAuthStateChanged(function(user) {
			a.currentUser = user
		})
	},
	methods: {
		logout() {
			auth.signOut()
		}
	}
}
</script>

<style>
:root {
	font-family: "Roboto Light", Arial, Helvetica, sans-serif;

	--text-color: #eaeaea;
	--medium-color: #333;
	--dark-color: #222;
	--light-color: #444;
	--highlight-color: #555;
}

:link,
a:visited {
	color: var(--text-color)
}

img[invert] {
	filter: invert();
}

html, body {
	height: 100%;
	margin: 0;
	padding: 0;
}

#app {
	height: 100%;
	color: var(--text-color);
	background-color: var(--medium-color);
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

header {
	display: flex;
	align-items: center;
	flex-direction: row;
	justify-content: space-between;
	background-color: var(--dark-color);
	padding: 0 1rem;
	height: 80px;

	#logo {
		transition: transform 1s ease;

		&:hover {
			transform: rotate(360deg);
		}
	}
}

#user-control {
	display: flex;
	flex-direction: column;
}

#header-title {
	display: flex;
	align-items: center;
	flex-direction: row;
	text-decoration: none;
	gap: 1rem;
}

#header-right {
	display: flex;
	gap: 1rem;
}


@media screen and (max-width: 500px) {
	#header-title h1 {
		font-size: 1.5rem;
	}
}

@media screen and (max-width: 320px) {
	#header-title h1 {
		display: none;
	}
}

main {
	padding: 1rem;
	height: 100%;
}

footer {
	padding: 1rem;
}

button, .btn {
	text-align: center;
	box-sizing: border-box;
	border-width: 2px;
	border-radius: .5rem;
	padding: .5rem;
	color: white;
	background-color: var(--light-color);
	border-style: outset;
	border-color: var(--dark-color);
	cursor: pointer;
	transition: background-color 200ms linear;
	text-decoration: none;

	&:disabled {
		background-color: color-mix(in srgb, var(--light-color), transparent 60%);
		border-color: color-mix(in srgb, var(--dark-color), transparent 60%);
		color: color-mix(in srgb, white, transparent 60%);
		cursor: revert;
	}
}

input {
	border-color: var(--dark-color);
	background-color: var(--light-color);
	color: white;
	outline: none;
	border-style: inset;
	appearance: textfield; /* funktioniert für firefox, aber nicht chrome */
	transition: background-color 200ms linear;

	&:user-invalid {
		border-color: red !important;
		background-color: rgb(161, 77, 77) !important;
	}
}

input[type=number]::-webkit-inner-spin-button,
input[type=number]::-webkit-outer-spin-button {
	appearance: none;
}

/* style für sliders */
input[type=range] {
	appearance: none;
	border-color: var(--dark-color);
	background-color: var(--light-color);
	border-style: inset;
	cursor: pointer;
	width: 100%;
	padding: 1px 0;
	border-width: 2px;
	margin: 0;

	/*  Style muss leider kopiert werden, da selector lists ignoriert werden,
	wenn ein pseudo element, welches der Browser nicht kennt, vorhanden ist */

	/* Slider Thumb style für webkit browsers */
	&::-webkit-slider-thumb {
		appearance: none;
		height: 1rem;
		width: 0.5rem;
		border: 1px outset var(--dark-color);
		background-color: var(--medium-color);
		border-radius: 2px;
	}

	/* Slider Thumb für Firefox */
	&::-moz-range-thumb {
		appearance: none;
		height: 1rem;
		width: 0.5rem;
		border: 1px outset var(--dark-color);
		background-color: var(--medium-color);
		border-radius: 2px;
	}
}

:is(button, .btn, input):not(:disabled):hover {
	background-color: var(--highlight-color);
}
</style>
