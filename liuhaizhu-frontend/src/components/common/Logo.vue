<template>
  <div class="logo" :class="{ 'clickable': clickable }" @click="handleClick">
    <img src="/机器人.svg" alt="Logo" class="logo-icon" />
    <span class="logo-text">AI 刘海柱</span>
  </div>
</template>

<script setup>
/**
 * Logo 组件
 * @description 统一的 Logo 组件，包含机器人图标和 "AI 刘海柱" 文字
 */

const props = defineProps({
  /**
   * 是否可点击
   */
  clickable: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  if (props.clickable) {
    emit('click')
  }
}
</script>

<style scoped>
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: default;
}

.logo.clickable {
  cursor: pointer;
}

.logo-icon {
  width: 33px;
  height: 33px;
  object-fit: contain;
  transition: transform 0.5s ease;
}

.logo:hover .logo-icon {
  transform: scaleX(-1);
}

.logo-text {
  font-size: 22px;
  font-weight: 600;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #519cff 0%, #7300f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@media (max-width: 480px) {
  .logo-icon {
    width: 26px;
    height: 26px;
  }

  .logo-text {
    font-size: 16px;
  }
}
</style>
