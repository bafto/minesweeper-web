import { createApp } from 'vue'
import { createMemoryHistory, createRouter } from 'vue-router';
import App from './App.vue'

import SelectGamemodePage from './components/pages/SelectGamemodePage.vue';
import CreateLobbyPage from './components/pages/CreateLobbyPage.vue';
import SingleplayerPage from './components/pages/SingleplayerPage.vue';
import JoinLobbyPage from './components/pages/JoinLobbyPage.vue';
import SettingsComponent from './components/SettingsComponent.vue';
import AboutPage from './components/pages/AboutPage.vue';
import StartSingleplayerPage from './components/pages/StartSingleplayerPage.vue';

const routes = [
  { path: '/', component: SelectGamemodePage },
  { path: '/about', component: AboutPage },
  { path: '/create-lobby', component: CreateLobbyPage },
  { path: '/start-singleplayer', component: StartSingleplayerPage},
  { path: '/singleplayer', component: SingleplayerPage },
  { path: '/join-lobby', component: JoinLobbyPage },
  { path: '/settings', component: SettingsComponent },
];

const router = createRouter({
  history: createMemoryHistory(),
  routes,
})

createApp(App).use(router).mount('#app')
