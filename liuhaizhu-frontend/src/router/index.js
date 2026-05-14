import { createRouter, createWebHistory } from 'vue-router'
import LandingView from '@/views/LandingView.vue'
import ChatView from '@/views/ChatView.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import FeaturesView from '@/views/FeaturesView.vue'
import DemoView from '@/views/DemoView.vue'
import IntegrationsView from '@/views/IntegrationsView.vue'
import UpgradeVIPView from '@/views/UpgradeVIPView.vue'
import QuizView from '@/views/QuizView.vue'
import AuthorProfileView from '@/views/AuthorProfileView.vue'
import TeamView from '@/views/TeamView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'landing',
      component: LandingView,
    },
    {
      path: '/chat',
      name: 'chat',
      component: ChatView,
    },
    {
      path: '/features',
      name: 'features',
      component: FeaturesView,
    },
    {
      path: '/demo',
      name: 'demo',
      component: DemoView,
    },
    {
      path: '/integrations',
      name: 'integrations',
      component: IntegrationsView,
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminDashboard,
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/upgrade-vip',
      name: 'upgrade-vip',
      component: UpgradeVIPView,
    },
    {
      path: '/quiz',
      name: 'quiz',
      component: QuizView,
    },
    {
      path: '/author',
      name: 'author',
      component: AuthorProfileView,
    },
    {
      path: '/team',
      name: 'team',
      component: TeamView,
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth' }
    }
    if (to.path === '/' && from.path !== '/') {
      return false
    }
    return { top: 0 }
  },
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)
  const requiresAdmin = to.matched.some((record) => record.meta.requiresAdmin)

  if (requiresAuth) {
    const isAuthenticated = await authStore.checkAuth()
    if (!isAuthenticated) {
      next({ path: '/', query: { redirect: to.fullPath } })
      return
    }

    // 检查管理员权限
    if (requiresAdmin && authStore.user?.role !== 'ADMIN') {
      next({ path: '/' })
      return
    }
  }

  next()
})

export default router
