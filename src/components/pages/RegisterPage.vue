<template>
	<div id="box">
		<h1>Register or Sign in</h1>
		<form>
			<input type="text" placeholder="Email" v-model="email" />
			<input type="password" placeholder="Password" v-model="password" />
			
			<div id="btn-group">
				<button @click="register">Register</button>
				<button @click="login">Sign in</button>
			</div>
		</form>
	</div>
</template>

<style scoped>
#box {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

form {
	display: flex;
	flex-direction: column;
	width: 300px;
	gap: 5px;
}

#btn-group {
	display: flex;
	gap: 10px;
	justify-content: center;
}

button {
	width: calc(50% - 10px);

}
</style>

<script>
	import { auth } from '../../firebaseConfig'
	import { createUserWithEmailAndPassword, signInWithEmailAndPassword } from 'firebase/auth';

	export default {
		name: 'RegisterPage',
		data() {
			return {
				email: '',
				password: ''
			}
		},
		methods: {
			register() {
				createUserWithEmailAndPassword(auth, this.email, this.password)
					.then((data) => {
						console.log(data)
						console.log('Successfully registered!');
						this.$router.push('/')
					})
					.catch(error => {
						console.log(error.code)
						alert(error.message);
					});
			},
			login() {
				signInWithEmailAndPassword(auth, this.email, this.password)
					.then((data) => {
						console.log(data);
						console.log('Successfully logged in!');
						this.$router.push('/');
					})
					.catch(error => {
						console.log(error.code);
						alert(error.message);
					});
			}
		}
	}
</script>