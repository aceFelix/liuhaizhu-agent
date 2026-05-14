export const imageUtils = {
  getOptimizedImageUrl(url, options = {}) {
    if (!url) return ''

    const {
      width,
      height,
      quality = 80,
      format = 'webp'
    } = options

    if (url.startsWith('data:') || url.startsWith('blob:')) {
      return url
    }

    const params = new URLSearchParams()
    
    if (width) params.append('w', width)
    if (height) params.append('h', height)
    if (quality) params.append('q', quality)
    if (format) params.append('f', format)

    const separator = url.includes('?') ? '&' : '?'
    return `${url}${separator}${params.toString()}`
  },

  getResponsiveImageUrl(url, sizes = [320, 640, 960, 1280]) {
    if (!url) return ''

    return sizes
      .map((size, index) => {
        const optimizedUrl = this.getOptimizedImageUrl(url, { width: size })
        if (index === sizes.length - 1) {
          return optimizedUrl
        }
        return `${optimizedUrl} ${size}w`
      })
      .join(', ')
  },

  preloadImage(url) {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => resolve(url)
      img.onerror = reject
      img.src = url
    })
  },

  preloadImages(urls) {
    return Promise.all(urls.map(url => this.preloadImage(url)))
  },

  isWebPSupported() {
    if (typeof document === 'undefined') return false
    
    const canvas = document.createElement('canvas')
    if (canvas.getContext && canvas.getContext('2d')) {
      return canvas.toDataURL('image/webp').indexOf('data:image/webp') === 0
    }
    return false
  },

  getBestFormat() {
    if (this.isWebPSupported()) {
      return 'webp'
    }
    return 'jpeg'
  },

  generatePlaceholder(width = 100, height = 100, color = '#8b5cf6') {
    const svg = `
      <svg width="${width}" height="${height}" xmlns="http://www.w3.org/2000/svg">
        <rect width="100%" height="100%" fill="${color}"/>
      </svg>
    `
    return `data:image/svg+xml;base64,${btoa(svg)}`
  }
}

export const lazyLoadImages = () => {
  if ('IntersectionObserver' in window) {
    const imageObserver = new IntersectionObserver((entries, observer) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const img = entry.target
          const src = img.dataset.src
          
          if (src) {
            img.src = src
            img.removeAttribute('data-src')
            observer.unobserve(img)
          }
        }
      })
    }, {
      rootMargin: '50px 0px',
      threshold: 0.01
    })

    document.querySelectorAll('img[data-src]').forEach((img) => {
      imageObserver.observe(img)
    })

    return imageObserver
  }

  return null
}
