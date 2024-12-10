import { createApp } from 'vue'
import { createMemoryHistory, createRouter } from 'vue-router';
import App from './App.vue'

import SelectGamemodePage from './components/pages/SelectGamemodePage.vue';
import JoinLobbyPage from './components/pages/JoinLobbyPage.vue';
import SettingsPage from './components/pages/SettingsPage.vue';
import AboutPage from './components/pages/AboutPage.vue';
import SingleplayerPage from './components/pages/SingleplayerPage.vue';
import MultiplayerPage from './components/pages/MultiplayerPage.vue';
import './registerServiceWorker'

const routes = [
	{ path: '/', component: SelectGamemodePage },
	{ path: '/about', component: AboutPage },
	{ path: '/start-singleplayer', component: SettingsPage },
	{ path: '/create-lobby', component: SettingsPage, props: { multiplayer: true } },
	{ path: '/singleplayer', component: SingleplayerPage },
	{ path: '/join-lobby', component: JoinLobbyPage },
	{ path: '/multiplayer', component: MultiplayerPage, props: route => ({ players: route.query.players, username: route.query.username }), },
];

const router = createRouter({
	history: createMemoryHistory(),
	routes,
})

createApp(App).use(router).mount('#app')
