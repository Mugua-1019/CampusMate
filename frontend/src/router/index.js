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
import AdminDashboardView from '../views/admin/AdminDashboardView.vue'
import { getCurrentAdmin, getCurrentRole, getCurrentUser, isAuthenticatedAdmin, isAuthenticatedUser, ROLE_ADMIN, ROLE_USER } from '../utils/currentUser'
import { fetchProfile } from '../api/profile'

const UNVERIFIED_ALLOWED_ROUTE_NAMES = new Set(['home', 'authCenter', 'profile'])
const APPROVED_VERIFY_STATUSES = new Set(['approved', 'passed'])

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
  },
  {
    path: '/admin',
    name: 'adminDashboard',
    component: AdminDashboardView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const currentRole = getCurrentRole()
  const currentAdmin = currentRole === ROLE_ADMIN ? getCurrentAdmin() : null
  if (to.name === 'adminDashboard') {
    return isAuthenticatedAdmin(currentAdmin) ? undefined : { name: 'login' }
  }

  if (currentRole === ROLE_ADMIN && to.name !== 'login') {
    return { name: 'adminDashboard' }
  }

  const currentUser = currentRole === ROLE_USER ? getCurrentUser() : null
  if ((to.name === 'profile' || to.name === 'authCenter') && !isAuthenticatedUser(currentUser)) {
    return { name: 'login' }
  }
  if (!isAuthenticatedUser(currentUser) || UNVERIFIED_ALLOWED_ROUTE_NAMES.has(to.name)) {
    return undefined
  }

  try {
    const { data } = await fetchProfile(currentUser.userId)
    const profile = data.data || {}
    if (!profile.verified || !APPROVED_VERIFY_STATUSES.has(profile.verifyStatus)) {
      return {
        name: 'authCenter',
        query: { redirect: to.fullPath }
      }
    }
  } catch (error) {
    return { name: 'authCenter' }
  }
})

export default router
