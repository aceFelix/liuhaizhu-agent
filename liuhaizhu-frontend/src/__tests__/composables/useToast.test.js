import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { createApp, ref } from 'vue'
import { provideToast, useToast } from '@/composables/useToast'

// ============================================================
// useToast 测试
// ============================================================
describe('useToast', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  /**
   * 创建一个 Vue 应用并注入 toast，便于测试
   */
  function setupToast() {
    const app = createApp({
      setup() {
        const toast = provideToast()
        return { toast }
      },
      template: '<div></div>',
    })
    const vm = app.mount(document.createElement('div'))
    return vm.toast
  }

  describe('addToast', () => {
    it('应添加 toast 到列表', () => {
      const toast = setupToast()

      toast.addToast({
        type: 'success',
        title: '操作成功',
        message: '数据已保存',
      })

      expect(toast.toasts.value).toHaveLength(1)
      expect(toast.toasts.value[0].type).toBe('success')
      expect(toast.toasts.value[0].title).toBe('操作成功')
    })

    it('应返回唯一的 toast ID', () => {
      const toast = setupToast()

      const id1 = toast.addToast({ message: 'first' })
      const id2 = toast.addToast({ message: 'second' })

      expect(id1).not.toBe(id2)
    })

    it('默认类型应为 info', () => {
      const toast = setupToast()
      toast.addToast({ message: 'info msg' })

      expect(toast.toasts.value[0].type).toBe('info')
    })

    it('默认 duration 应为 3000ms', () => {
      const toast = setupToast()
      toast.addToast({ message: 'msg' })

      expect(toast.toasts.value[0].duration).toBe(3000)
    })

    it('支持自定义 duration', () => {
      const toast = setupToast()
      toast.addToast({ message: 'msg', duration: 5000 })

      expect(toast.toasts.value[0].duration).toBe(5000)
    })
  })

  describe('removeToast', () => {
    it('应通过 ID 移除指定 toast', () => {
      const toast = setupToast()
      const id = toast.addToast({ message: 'to remove' })
      toast.addToast({ message: 'keep' })

      expect(toast.toasts.value).toHaveLength(2)

      toast.removeToast(id)
      expect(toast.toasts.value).toHaveLength(1)
      expect(toast.toasts.value[0].message).toBe('keep')
    })

    it('移除不存在的 ID 不应报错', () => {
      const toast = setupToast()
      toast.addToast({ message: 'msg' })

      expect(() => toast.removeToast(999)).not.toThrow()
      expect(toast.toasts.value).toHaveLength(1)
    })
  })

  describe('自动过期', () => {
    it('duration 过后应自动移除 toast', () => {
      const toast = setupToast()
      toast.addToast({ message: 'temp', duration: 1000 })

      expect(toast.toasts.value).toHaveLength(1)

      vi.advanceTimersByTime(1001)

      expect(toast.toasts.value).toHaveLength(0)
    })

    it('不同 duration 的 toast 应在不同时机移除', () => {
      const toast = setupToast()
      toast.addToast({ message: 'fast', duration: 500 })
      toast.addToast({ message: 'slow', duration: 2000 })

      vi.advanceTimersByTime(501)
      expect(toast.toasts.value).toHaveLength(1)
      expect(toast.toasts.value[0].message).toBe('slow')

      vi.advanceTimersByTime(1501)
      expect(toast.toasts.value).toHaveLength(0)
    })
  })

  describe('便捷方法', () => {
    it('success 方法应创建 success 类型 toast', () => {
      const toast = setupToast()
      toast.success('成功', '操作完成')
      expect(toast.toasts.value[0].type).toBe('success')
    })

    it('error 方法应创建 error 类型 toast', () => {
      const toast = setupToast()
      toast.error('错误', '操作失败')
      expect(toast.toasts.value[0].type).toBe('error')
    })

    it('warning 方法应创建 warning 类型 toast', () => {
      const toast = setupToast()
      toast.warning('警告', '请注意')
      expect(toast.toasts.value[0].type).toBe('warning')
    })

    it('info 方法应创建 info 类型 toast', () => {
      const toast = setupToast()
      toast.info('提示', '这是信息')
      expect(toast.toasts.value[0].type).toBe('info')
    })
  })

  describe('useToast / provideToast 配对', () => {
    it('未 provide 时调用 useToast 应抛出异常', () => {
      expect(() => useToast()).toThrow('useToast must be used after provideToast')
    })
  })
})
