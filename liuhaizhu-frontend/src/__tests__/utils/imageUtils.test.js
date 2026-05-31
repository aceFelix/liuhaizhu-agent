import { describe, it, expect } from 'vitest'
import { imageUtils } from '@/utils/imageUtils'

// ============================================================
// imageUtils 测试
// ============================================================
describe('imageUtils', () => {
  describe('getOptimizedImageUrl', () => {
    it('应返回空字符串当 url 为空', () => {
      expect(imageUtils.getOptimizedImageUrl('')).toBe('')
      expect(imageUtils.getOptimizedImageUrl(null)).toBe('')
      expect(imageUtils.getOptimizedImageUrl(undefined)).toBe('')
    })

    it('data: URL 应原样返回', () => {
      const dataUrl = 'data:image/png;base64,abc123'
      expect(imageUtils.getOptimizedImageUrl(dataUrl)).toBe(dataUrl)
    })

    it('blob: URL 应原样返回', () => {
      const blobUrl = 'blob:http://localhost:5173/abc-123'
      expect(imageUtils.getOptimizedImageUrl(blobUrl)).toBe(blobUrl)
    })

    it('应添加默认优化参数', () => {
      const url = 'https://example.com/image.jpg'
      const result = imageUtils.getOptimizedImageUrl(url)

      expect(result).toContain('q=80')
      expect(result).toContain('f=webp')
    })

    it('应支持自定义宽高和质量参数', () => {
      const url = 'https://example.com/photo.png'
      const result = imageUtils.getOptimizedImageUrl(url, {
        width: 400,
        height: 300,
        quality: 90,
        format: 'jpeg',
      })

      expect(result).toContain('w=400')
      expect(result).toContain('h=300')
      expect(result).toContain('q=90')
      expect(result).toContain('f=jpeg')
    })

    it('已有查询参数的 URL 应使用 & 连接', () => {
      const url = 'https://example.com/image.jpg?v=1'
      const result = imageUtils.getOptimizedImageUrl(url, { width: 200 })

      expect(result.startsWith('https://example.com/image.jpg?v=1&')).toBe(true)
    })

    it('无查询参数的 URL 应使用 ? 连接', () => {
      const url = 'https://example.com/image.jpg'
      const result = imageUtils.getOptimizedImageUrl(url, { width: 200 })

      expect(result.startsWith('https://example.com/image.jpg?')).toBe(true)
    })

    it('不传 options 时使用默认值', () => {
      const url = 'https://example.com/img.jpg'
      const result = imageUtils.getOptimizedImageUrl(url)

      // 默认 quality=80, format=webp，无 width/height
      expect(result).toContain('q=80')
      expect(result).toContain('f=webp')
      expect(result).not.toContain('w=')
      expect(result).not.toContain('h=')
    })
  })

  describe('getResponsiveImageUrl', () => {
    it('应返回 srcset 格式的字符串', () => {
      const url = 'https://example.com/image.jpg'
      const result = imageUtils.getResponsiveImageUrl(url)

      // 应包含默认尺寸（最后一项无 size descriptor，仅含 w=1280）
      expect(result).toContain('320w')
      expect(result).toContain('640w')
      expect(result).toContain('960w')
      expect(result).toContain('w=1280')
    })

    it('空 URL 应返回空字符串', () => {
      expect(imageUtils.getResponsiveImageUrl('')).toBe('')
    })

    it('应支持自定义尺寸列表', () => {
      const url = 'https://example.com/photo.jpg'
      const result = imageUtils.getResponsiveImageUrl(url, [200, 400])

      expect(result).toContain('200w')
      expect(result).toContain('w=400')
      expect(result).not.toContain('320w')
    })
  })

  describe('preloadImage', () => {
    it('应返回 Promise', () => {
      const result = imageUtils.preloadImage('https://example.com/img.jpg')
      expect(result).toBeInstanceOf(Promise)
    })
  })

  describe('generatePlaceholder', () => {
    it('应返回 data:image/svg+xml 的 data URL', () => {
      const result = imageUtils.generatePlaceholder()
      expect(result.startsWith('data:image/svg+xml;base64,')).toBe(true)
    })

    it('应支持自定义尺寸和颜色', () => {
      const result = imageUtils.generatePlaceholder(200, 150, '#ff0000')

      // 解码 base64 并检查 SVG 内容
      const base64 = result.replace('data:image/svg+xml;base64,', '')
      const decoded = atob(base64)

      expect(decoded).toContain('width="200"')
      expect(decoded).toContain('height="150"')
      expect(decoded).toContain('#ff0000')
    })

    it('默认颜色应为 #8b5cf6', () => {
      const result = imageUtils.generatePlaceholder()
      const base64 = result.replace('data:image/svg+xml;base64,', '')
      const decoded = atob(base64)
      expect(decoded).toContain('#8b5cf6')
    })

    it('默认尺寸应为 100x100', () => {
      const result = imageUtils.generatePlaceholder()
      const base64 = result.replace('data:image/svg+xml;base64,', '')
      const decoded = atob(base64)
      expect(decoded).toContain('width="100"')
      expect(decoded).toContain('height="100"')
    })
  })

  describe('getBestFormat', () => {
    it('应返回 webp 或 jpeg', () => {
      const format = imageUtils.getBestFormat()
      expect(['webp', 'jpeg']).toContain(format)
    })
  })
})
