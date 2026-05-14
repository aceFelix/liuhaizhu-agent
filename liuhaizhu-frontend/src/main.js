import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/styles/variables.css'

import App from './App.vue'
import router from './router'
import permissionDirective from '@/directives/permission'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.directive('permission', permissionDirective)

app.mount('#app')

router.isReady().then(() => {
  const loadingElement = document.getElementById('app-loading')
  if (loadingElement) {
    loadingElement.classList.add('fade-out')
    setTimeout(() => {
      loadingElement.remove()
    }, 500)
  }
})
