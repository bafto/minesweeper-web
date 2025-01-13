<template>
	<div id="box">
		<h2>Register or Sign in</h2>
		<div id="tabs">
			<button :class="{ active: isRegisterTab }" @click="isRegisterTab = true">Register</button>
			<button :class="{ active: !isRegisterTab }" @click="isRegisterTab = false">Sign in</button>
		</div>
		<div id="form" @submit.prevent="">
			<input v-if="isRegisterTab" type="text" placeholder="Username" v-model="username" required />
			<input type="text" placeholder="Email" v-model="email" required />
			<input type="password" placeholder="Password" v-model="password" required />
			<div id="btn-group">
				<button v-if="isRegisterTab" @click="register" type="submit">Register</button>
				<button v-else @click="login" type="submit">Sign in</button>
			</div>
		</div>
	</div>
</template>

<style scoped>
#box {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 20px;
	background-color: #2b2b2b;
	border: 1px solid #444;
	border-radius: 8px;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.5);
	width: 320px;
	margin: 20px auto;
	color: #eaeaea;
}

#tabs {
	display: flex;
	gap: 0;
	width: 100%;
	margin-bottom: 10px;
	background-color: #3a3a3a;
	border-bottom: 1px solid #444;
	border-radius: 1rem 1rem 0 0;
}

#tabs button {
	flex: 1;
	padding: 10px 0;
	cursor: pointer;
	background-color: #3a3a3a;
	border: none;
	border-radius: 8px 8px 0 0;
	color: #b0b0b0;
	font-weight: bold;
	transition: background-color 0.3s, color 0.3s;

	&.active {
		background-color: #2b2b2b;
		border: 1px solid #444;
		border-bottom: none;
		color: #fff;
	}
}

#form {
	display: flex;
	flex-direction: column;
	width: 100%;
	gap: 10px;
	padding: 10px;
}

#btn-group {
	display: flex;
	gap: 10px;
	justify-content: center;
}

button {
	padding: 10px;
	border: none;
	border-radius: 4px;
	background-color: #3a3a3a;
	color: white;
	cursor: pointer;
	font-weight: bold;
	transition: background-color 0.3s, transform 0.2s;

	&:disabled {
		background-color: #555;
		cursor: not-allowed;
	}
}
</style>

<script>
	import { auth } from '../../firebaseConfig';
	import { createUserWithEmailAndPassword, signInWithEmailAndPassword, updateProfile } from 'firebase/auth';

	export default {
		name: 'RegisterPage',
		data() {
			return {
				isRegisterTab: true,
				email: '',
				password: '',
				username: '',
			};
		},
		methods: {
			register() {
				if (!this.username) {
					alert('Please enter a username.');
					return;
				}
				createUserWithEmailAndPassword(auth, this.email, this.password)
					.then((userCredential) => {
						const user = userCredential.user;
						return updateProfile(user, { displayName: this.username });
					})
					.then(() => {
						console.log('Successfully registered and display name set!');
						this.$router.push('/');
					})
					.catch((error) => {
						console.error('Error during registration:', error);
						alert(error.message);
					});
			},
			login() {
				signInWithEmailAndPassword(auth, this.email, this.password)
					.then((data) => {
						console.log(data)
						console.log(`Successfully logged in as ${data.user.displayName}!`);
						this.$router.push('/');
					})
					.catch((error) => {
						console.error('Error during login:', error);
						alert(error.message);
					});
			},
		},
	};
</script>
