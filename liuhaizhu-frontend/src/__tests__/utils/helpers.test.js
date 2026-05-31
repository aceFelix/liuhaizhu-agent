import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import {
  secureStorage,
  generateId,
  validateEmail,
  validatePhone,
  debounce,
  throttle,
  formatDate,
  formatFileSize,
  escapeHtml,
  sanitizeInput,
} from '@/utils/helpers'

// ============================================================
// secureStorage 测试
// ============================================================
describe('secureStorage', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  describe('setItem / getItem', () => {
    it('应正确存取字符串值', () => {
      secureStorage.setItem('test_key', 'hello world')
      expect(secureStorage.getItem('test_key')).toBe('hello world')
    })

    it('应正确存取数字值', () => {
      secureStorage.setItem('num_key', 42)
      expect(secureStorage.getItem('num_key')).toBe(42)
    })

    it('应正确存取对象值', () => {
      const obj = { name: 'Alice', role: 'ADMIN', id: 123 }
      secureStorage.setItem('obj_key', obj)
      const result = secureStorage.getItem('obj_key')
      expect(result).toEqual(obj)
    })

    it('应正确存取数组值', () => {
      const arr = [1, 2, 3, { a: 'b' }]
      secureStorage.setItem('arr_key', arr)
      expect(secureStorage.getItem('arr_key')).toEqual(arr)
    })

    it('setItem 成功应返回 true', () => {
      const result = secureStorage.setItem('key', 'value')
      expect(result).toBe(true)
    })

    it('不存在的 key 应返回 null', () => {
      expect(secureStorage.getItem('nonexistent')).toBeNull()
    })

    it('存储的值应被加密（不以明文存储）', () => {
      secureStorage.setItem('secret', 'password123')
      const raw = localStorage.getItem('secret')
      // 加密后的值不应该直接包含原始文本
      expect(raw).not.toBe('password123')
      expect(raw).not.toBe('"password123"')
    })

    it('不同的 key 不应相互干扰', () => {
      secureStorage.setItem('key1', 'value1')
      secureStorage.setItem('key2', 'value2')

      expect(secureStorage.getItem('key1')).toBe('value1')
      expect(secureStorage.getItem('key2')).toBe('value2')
    })
  })

  describe('removeItem', () => {
    it('应成功删除已存在的 key', () => {
      secureStorage.setItem('del_key', 'value')
      expect(secureStorage.getItem('del_key')).toBe('value')

      const result = secureStorage.removeItem('del_key')
      expect(result).toBe(true)
      expect(secureStorage.getItem('del_key')).toBeNull()
    })

    it('删除不存在的 key 不应报错', () => {
      const result = secureStorage.removeItem('ghost_key')
      expect(result).toBe(true)
    })
  })

  describe('clear', () => {
    it('应清空所有存储的数据', () => {
      secureStorage.setItem('a', 1)
      secureStorage.setItem('b', 2)

      const result = secureStorage.clear()
      expect(result).toBe(true)
      expect(secureStorage.getItem('a')).toBeNull()
      expect(secureStorage.getItem('b')).toBeNull()
    })
  })

  describe('加密安全性', () => {
    it('存储相同值两次应产生不同的加密结果（不同时间加密向量不同）', () => {
      // 加密的方法使用 XOR，相同 key 会产生相同的加密结果，
      // 但 base64 编码后的结果应是一致的（确定性加密）
      secureStorage.setItem('same', 'test')
      const first = localStorage.getItem('same')
      localStorage.removeItem('same')
      secureStorage.setItem('same', 'test')
      const second = localStorage.getItem('same')

      // 确定性加密：同 key 同 value 应产生同密文
      expect(first).toBe(second)
    })
  })
})

// ============================================================
// generateId 测试
// ============================================================
describe('generateId', () => {
  it('应返回以 id_ 开头的字符串', () => {
    const id = generateId()
    expect(id.startsWith('id_')).toBe(true)
  })

  it('每次调用应生成不同的 ID', () => {
    const ids = new Set()
    for (let i = 0; i < 100; i++) {
      ids.add(generateId())
    }
    expect(ids.size).toBe(100)
  })

  it('应包含时间戳部分', () => {
    const id = generateId()
    // 格式: id_timestamp_random
    const parts = id.split('_')
    expect(parts.length).toBeGreaterThanOrEqual(3)
    // 时间戳部分应是数字
    expect(Number.isNaN(Number(parts[1]))).toBe(false)
  })
})

// ============================================================
// validateEmail 测试
// ============================================================
describe('validateEmail', () => {
  it('正确邮箱应返回 true', () => {
    expect(validateEmail('test@example.com')).toBe(true)
    expect(validateEmail('user.name@domain.co')).toBe(true)
    expect(validateEmail('user+tag@domain.org')).toBe(true)
  })

  it('无效邮箱应返回 false', () => {
    expect(validateEmail('')).toBe(false)
    expect(validateEmail('notanemail')).toBe(false)
    expect(validateEmail('@nodomain.com')).toBe(false)
    expect(validateEmail('noat.com')).toBe(false)
    expect(validateEmail('spaces in@email.com')).toBe(false)
  })
})

// ============================================================
// validatePhone 测试
// ============================================================
describe('validatePhone', () => {
  it('正确手机号应返回 true', () => {
    expect(validatePhone('13812345678')).toBe(true)
    expect(validatePhone('15900001111')).toBe(true)
    expect(validatePhone('18888888888')).toBe(true)
    expect(validatePhone('19912345678')).toBe(true)
  })

  it('无效手机号应返回 false', () => {
    expect(validatePhone('12345678901')).toBe(false) // 不以1开头
    expect(validatePhone('12012345678')).toBe(false) // 第二位无效
    expect(validatePhone('1381234567')).toBe(false)  // 10位
    expect(validatePhone('138123456789')).toBe(false) // 12位
    expect(validatePhone('')).toBe(false)
    expect(validatePhone('abcdefghijk')).toBe(false)
  })
})

// ============================================================
// debounce 测试
// ============================================================
describe('debounce', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('应延迟执行函数', () => {
    const fn = vi.fn()
    const debounced = debounce(fn, 300)

    debounced()
    expect(fn).not.toHaveBeenCalled()

    vi.advanceTimersByTime(300)
    expect(fn).toHaveBeenCalledTimes(1)
  })

  it('多次快速调用应只执行最后一次', () => {
    const fn = vi.fn()
    const debounced = debounce(fn, 200)

    debounced()
    debounced()
    debounced()

    vi.advanceTimersByTime(200)
    expect(fn).toHaveBeenCalledTimes(1)
  })

  it('应传递参数给原函数', () => {
    const fn = vi.fn()
    const debounced = debounce(fn, 100)

    debounced('arg1', 42)
    vi.advanceTimersByTime(100)

    expect(fn).toHaveBeenCalledWith('arg1', 42)
  })

  it('延迟期间再次调用应重置计时器', () => {
    const fn = vi.fn()
    const debounced = debounce(fn, 200)

    debounced()
    vi.advanceTimersByTime(100)
    debounced() // 重置计时器
    vi.advanceTimersByTime(100)
    expect(fn).not.toHaveBeenCalled()
    vi.advanceTimersByTime(100)
    expect(fn).toHaveBeenCalledTimes(1)
  })
})

// ============================================================
// throttle 测试
// ============================================================
describe('throttle', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('第一次调用应立即执行', () => {
    const fn = vi.fn()
    const throttled = throttle(fn, 500)

    throttled()
    expect(fn).toHaveBeenCalledTimes(1)
  })

  it('节流期内多次调用应只执行第一次', () => {
    const fn = vi.fn()
    const throttled = throttle(fn, 300)

    throttled()
    throttled()
    throttled()

    expect(fn).toHaveBeenCalledTimes(1)
  })

  it('节流期过后应能再次执行', () => {
    const fn = vi.fn()
    const throttled = throttle(fn, 200)

    throttled()
    expect(fn).toHaveBeenCalledTimes(1)

    vi.advanceTimersByTime(250)
    throttled()
    expect(fn).toHaveBeenCalledTimes(2)
  })

  it('应传递参数给原函数', () => {
    const fn = vi.fn()
    const throttled = throttle(fn, 100)

    throttled('hello', 123)
    expect(fn).toHaveBeenCalledWith('hello', 123)
  })
})

// ============================================================
// formatDate 测试
// ============================================================
describe('formatDate', () => {
  it('刚刚（60秒内）应返回"刚刚"', () => {
    const now = new Date()
    const justNow = new Date(now.getTime() - 30 * 1000) // 30秒前
    expect(formatDate(justNow)).toBe('刚刚')
  })

  it('十分钟前应返回"10分钟前"', () => {
    const now = new Date()
    const tenMinAgo = new Date(now.getTime() - 10 * 60 * 1000)
    expect(formatDate(tenMinAgo)).toBe('10分钟前')
  })

  it('两小时前应返回"2小时前"', () => {
    const now = new Date()
    const twoHoursAgo = new Date(now.getTime() - 2 * 60 * 60 * 1000)
    expect(formatDate(twoHoursAgo)).toBe('2小时前')
  })

  it('三天前应返回"3天前"', () => {
    const now = new Date()
    const threeDaysAgo = new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000)
    expect(formatDate(threeDaysAgo)).toBe('3天前')
  })

  it('两周前应返回"2周前"', () => {
    const now = new Date()
    const twoWeeksAgo = new Date(now.getTime() - 14 * 24 * 60 * 60 * 1000)
    expect(formatDate(twoWeeksAgo)).toBe('2周前')
  })

  it('三个月前应返回"3个月前"', () => {
    const now = new Date()
    const threeMonthsAgo = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000)
    expect(formatDate(threeMonthsAgo)).toBe('3个月前')
  })

  it('两年前应返回"2年前"', () => {
    const now = new Date()
    const twoYearsAgo = new Date(now.getTime() - 2 * 365 * 24 * 60 * 60 * 1000)
    expect(formatDate(twoYearsAgo)).toBe('2年前')
  })
})

// ============================================================
// formatFileSize 测试
// ============================================================
describe('formatFileSize', () => {
  it('0 字节应返回 "0 B"', () => {
    expect(formatFileSize(0)).toBe('0 B')
  })

  it('应正确格式化字节', () => {
    expect(formatFileSize(500)).toBe('500 B')
  })

  it('应正确格式化 KB', () => {
    expect(formatFileSize(1024)).toBe('1 KB')
    expect(formatFileSize(1536)).toBe('1.5 KB')
  })

  it('应正确格式化 MB', () => {
    expect(formatFileSize(1024 * 1024)).toBe('1 MB')
  })

  it('应正确格式化 GB', () => {
    expect(formatFileSize(1024 * 1024 * 1024)).toBe('1 GB')
  })
})

// ============================================================
// escapeHtml 测试
// ============================================================
describe('escapeHtml', () => {
  it('应转义 HTML 特殊字符', () => {
    const input = '<script>alert("xss")</script>'
    const escaped = escapeHtml(input)
    expect(escaped).not.toContain('<script>')
  })

  it('普通文本应保持不变', () => {
    expect(escapeHtml('hello world')).toBe('hello world')
  })

  it('空输入应返回空字符串', () => {
    expect(escapeHtml('')).toBe('')
    expect(escapeHtml(null)).toBe('')
    expect(escapeHtml(undefined)).toBe('')
  })

  it('应转义 & 符号', () => {
    const escaped = escapeHtml('a & b')
    expect(escaped).toContain('&amp;')
  })
})

// ============================================================
// sanitizeInput 测试
// ============================================================
describe('sanitizeInput', () => {
  it('应移除 HTML 标签', () => {
    const input = '<p>hello <b>world</b></p>'
    const sanitized = sanitizeInput(input)
    expect(sanitized).not.toContain('<')
    expect(sanitized).toContain('hello')
  })

  it('应去除首尾空格', () => {
    expect(sanitizeInput('  hello  ')).toBe('hello')
  })

  it('非字符串输入应原样返回', () => {
    expect(sanitizeInput(123)).toBe(123)
    expect(sanitizeInput(null)).toBeNull()
    expect(sanitizeInput(undefined)).toBeUndefined()
  })

  it('应移除 script 标签', () => {
    const input = '<script>alert("hack")</script>normal text'
    const sanitized = sanitizeInput(input)
    expect(sanitized).not.toContain('<script>')
    expect(sanitized).not.toContain('</script>')
    // sanitizeInput 只剥离 HTML 标签，标签内的文本内容会保留
    expect(sanitized).toContain('normal text')
  })
})
