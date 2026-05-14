import { usePermission } from '@/utils/permission'

const checkPermission = (el, binding) => {
  const { value } = binding
  const { hasPermission, hasAnyRole, hasAllRoles, hasAnyPermission, hasAllPermissions } = usePermission()

  if (!value) {
    throw new Error('v-permission 需要传入值，例如 v-permission="\'knowledgeBaseUpload\'"')
  }

  let hasAuth = false

  if (typeof value === 'string') {
    hasAuth = hasPermission(value)
  } else if (Array.isArray(value)) {
    hasAuth = hasAnyPermission(value)
  } else if (typeof value === 'object') {
    if (value.roles) {
      hasAuth = hasAnyRole(value.roles)
    } else if (value.allRoles) {
      hasAuth = hasAllRoles(value.allRoles)
    } else if (value.permission) {
      hasAuth = hasPermission(value.permission)
    } else if (value.any) {
      hasAuth = hasAnyPermission(value.any)
    } else if (value.all) {
      hasAuth = hasAllPermissions(value.all)
    }
  }

  if (!hasAuth) {
    el.style.display = 'none'
    el.setAttribute('v-permission-hidden', 'true')
  } else {
    el.style.display = ''
    el.removeAttribute('v-permission-hidden')
  }
}

export default {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  updated(el, binding) {
    checkPermission(el, binding)
  }
}
