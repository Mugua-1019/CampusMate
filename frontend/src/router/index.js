import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/user/home/HomeView.vue'
import PublishPostView from '../views/user/home/PublishPostView.vue'
import MyMatchView from '../views/user/home/MyMatchView.vue'
import SecurityFeedbackView from '../views/user/home/SecurityFeedbackView.vue'
import MatchPostDetailView from '../views/user/home/MatchPostDetailView.vue'
import VentPostDetailView from '../views/user/home/VentPostDetailView.vue'
import ChatView from '../views/user/chat/ChatView.vue'
import LoginView from '../views/user/login/LoginView.vue'
import ProfileView from '../views/user/profile/ProfileView.vue'
import AuthenticationView from '../views/user/authCenter/AuthenticationView.vue'
import { getCurrentUser, isAuthenticatedUser } from '../utils/currentUser'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView
  },
  {
    path: '/home',
    name: 'home',
    component: HomeView
  },
  {
    path: '/publish',
    name: 'publish',
    component: PublishPostView
  },
  {
    path: '/my-match',
    name: 'myMatch',
    component: MyMatchView
  },
  {
    path: '/safety-feedback',
    name: 'safetyFeedback',
    component: SecurityFeedbackView
  },
  {
    path: '/chat',
    name: 'chat',
    component: ChatView
  },
  {
    path: '/match-post/:id',
    name: 'matchPostDetail',
    component: MatchPostDetailView
  },
  {
    path: '/vent-post/:id',
    name: 'ventPostDetail',
    component: VentPostDetailView
  },
  {
    path: '/match-post-detail',
    redirect: '/match-post/1'
  },
  {
    path: '/vent-post-detail',
    redirect: '/vent-post/1'
  },
  {
    path: '/auth-center',
    name: 'authCenter',
    component: AuthenticationView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if ((to.name === 'profile' || to.name === 'authCenter') && !isAuthenticatedUser(getCurrentUser())) {
    return { name: 'login' }
  }
})

export default router
