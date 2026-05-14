<template>
  <div :class="['lazy-image-container', props.class]" :style="containerStyle" ref="containerRef">
    <div v-if="loading && !imageSrc" class="image-placeholder">
      <div class="placeholder-shimmer"></div>
    </div>
    <img
      v-show="imageSrc && !error"
      :src="imageSrc"
      :alt="alt"
      :class="['lazy-image', { 'loaded': !loading }]"
      :style="imageStyle"
      @load="handleLoad"
      @error="handleError"
    />
    <div v-if="error" class="image-error">
      <svg viewBox="0 0 24 24" width="32" height="32" fill="currentColor">
        <path d="M21 5v6.59l-3-3.01-4 4.01-4-4-4 4-3-3.01V5c0-1.1.9-2 2-2h14c1.1 0 2 .9 2 2zm-3 6.42l3 3.01V19c0 1.1-.9 2-2 2H5c-1.1 0-2-.9-2-2v-6.58l3 2.99 4-4 4 4 4-3.99z"/>
      </svg>
      <span>加载失败</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: ''
  },
  width: {
    type: [String, Number],
    default: '100%'
  },
  height: {
    type: [String, Number],
    default: 'auto'
  },
  radius: {
    type: String,
    default: '8px'
  },
  objectFit: {
    type: String,
    default: 'cover'
  },
  lazy: {
    type: Boolean,
    default: true
  },
  threshold: {
    type: Number,
    default: 0.01
  },
  rootMargin: {
    type: String,
    default: '100px'
  },
  class: {
    type: String,
    default: ''
  }
})

const containerRef = ref(null)
const loading = ref(true)
const error = ref(false)
const imageSrc = ref('')
const observer = ref(null)

const containerStyle = computed(() => {
  const width = typeof props.width === 'number' ? `${props.width}px` : props.width
  const height = typeof props.height === 'number' ? `${props.height}px` : props.height
  
  return {
    width,
    height: height === 'auto' ? 'auto' : height,
    minHeight: height === 'auto' ? '50px' : height,
    borderRadius: props.radius
  }
})

const imageStyle = computed(() => ({
  objectFit: props.objectFit,
  width: '100%',
  height: '100%'
}))

const handleLoad = () => {
  loading.value = false
  error.value = false
}

const handleError = () => {
  loading.value = false
  error.value = true
  console.error('图片加载失败:', props.src)
}

const loadImage = () => {
  if (imageSrc.value) return
  imageSrc.value = props.src
}

onMounted(() => {
  if (!props.lazy) {
    loadImage()
    return
  }

  if ('IntersectionObserver' in window && containerRef.value) {
    observer.value = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            loadImage()
            if (observer.value) {
              observer.value.disconnect()
            }
          }
        })
      },
      {
        threshold: props.threshold,
        rootMargin: props.rootMargin
      }
    )

    observer.value.observe(containerRef.value)
  } else {
    loadImage()
  }
})

watch(() => props.src, (newSrc) => {
  if (newSrc) {
    loading.value = true
    error.value = false
    imageSrc.value = ''
    loadImage()
  }
})

onBeforeUnmount(() => {
  if (observer.value) {
    observer.value.disconnect()
  }
})
</script>

<style scoped>
.lazy-image-container {
  position: relative;
  overflow: hidden;
  background-color: var(--bg-secondary, #f5f5f5);
  display: inline-block;
}

.lazy-image {
  display: block;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.lazy-image.loaded {
  opacity: 1;
}

.image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

[data-theme='dark'] .image-placeholder {
  background: linear-gradient(90deg, #1a1a2e 25%, #252540 50%, #1a1a2e 75%);
  background-size: 200% 100%;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.image-error {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
  color: var(--text-tertiary, #9ca3af);
  background-color: var(--bg-secondary, #f5f5f5);
}

.image-error svg {
  opacity: 0.5;
}

.image-error span {
  font-size: 12px;
}
</style>
