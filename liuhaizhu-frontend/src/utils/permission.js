import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export const RoleEnum = {
  ADMIN: 'ADMIN',
  VIP: 'VIP',
  USER: 'USER'
}

export function usePermission() {
  const authStore = useAuthStore()

  const userRole = computed(() => {
    return authStore.user?.role || RoleEnum.USER
  })

  const isAdmin = computed(() => {
    return userRole.value === RoleEnum.ADMIN
  })

  const isVip = computed(() => {
    return userRole.value === RoleEnum.VIP || userRole.value === RoleEnum.ADMIN
  })

  const isNormalUser = computed(() => {
    return userRole.value === RoleEnum.USER
  })

  const hasPermission = (permission) => {
    const role = userRole.value

    if (role === RoleEnum.ADMIN) {
      return true
    }

    if (role === RoleEnum.VIP) {
      return true
    }

    if (role === RoleEnum.USER) {
      switch (permission) {
        case 'knowledgeBaseUpload':
          return false
        case 'emailTool':
          return false
        case 'externalMcp':
          return false
        default:
          return true
      }
    }

    return false
  }

  const hasAnyPermission = (permissions) => {
    return permissions.some(permission => hasPermission(permission))
  }

  const hasAllPermissions = (permissions) => {
    return permissions.every(permission => hasPermission(permission))
  }

  const hasAnyRole = (roles) => {
    return roles.includes(userRole.value)
  }

  const hasAllRoles = (roles) => {
    return roles.every(role => userRole.value === role)
  }

  const getRoleName = (role) => {
    const roleNames = {
      [RoleEnum.ADMIN]: '管理员',
      [RoleEnum.VIP]: 'VIP用户',
      [RoleEnum.USER]: '普通用户'
    }
    return roleNames[role] || '未知'
  }

  return {
    RoleEnum,
    userRole,
    isAdmin,
    isVip,
    isNormalUser,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    hasAnyRole,
    hasAllRoles,
    getRoleName
  }
}
