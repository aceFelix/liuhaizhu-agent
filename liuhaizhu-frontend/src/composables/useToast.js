import { ref, provide, inject } from 'vue'

const ToastSymbol = Symbol('toast')

export function provideToast() {
  const toasts = ref([])
  let toastId = 0

  const addToast = (options) => {
    const id = ++toastId
    const toast = {
      id,
      type: options.type || 'info',
      title: options.title || '',
      message: options.message || '',
      duration: options.duration || 3000
    }
    
    toasts.value.push(toast)
    
    setTimeout(() => {
      removeToast(id)
    }, toast.duration)
    
    return id
  }

  const removeToast = (id) => {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }

  const toast = {
    toasts,
    addToast,
    removeToast,
    success: (title, message, duration) => addToast({ type: 'success', title, message, duration }),
    error: (title, message, duration) => addToast({ type: 'error', title, message, duration }),
    warning: (title, message, duration) => addToast({ type: 'warning', title, message, duration }),
    info: (title, message, duration) => addToast({ type: 'info', title, message, duration })
  }

  provide(ToastSymbol, toast)
  return toast
}

export function useToast() {
  const toast = inject(ToastSymbol)
  if (!toast) {
    throw new Error('useToast must be used after provideToast')
  }
  return toast
}
