<template>
  <div class="quiz-page" :class="{ 'light-theme': settingsStore.theme === 'light' }">
    <CyberBackground />

    <nav class="nav-bar">
      <div class="nav-content">
        <div class="nav-left">
          <Logo :clickable="true" @click="handleBackToVip" />
          <div class="nav-links-left">
            <span class="quiz-title-nav">答题挑战</span>
          </div>
        </div>
        <div class="nav-links">
          <ThemeToggle />
          <button @click="handleBackToVip" class="btn-back">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
              <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
            </svg>
            <span>返回</span>
          </button>
        </div>
      </div>
    </nav>

    <div class="quiz-content">
      <div v-if="currentStep === 'select'" class="step-select">
        <div class="quiz-header">
          <h1 class="main-title">
            <GlitchText text="答题挑战" class="title-gradient" />
          </h1>
          <p class="subtitle">{{ isVIP ? '挑战自我，看看你能得多少分！' : '选择题目类型，满分通关即可升级VIP' }}</p>
          <div class="rules-card">
            <div class="rule-item">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
              <span>{{ isVIP ? '满分100分，挑战自我！' : '满分100分即可升级VIP' }}</span>
            </div>
            <div class="rule-item">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
              </svg>
              <span>不限答题时间</span>
            </div>
            <div class="rule-item">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
              <span>多次答题机会</span>
            </div>
          </div>
        </div>

        <div class="categories-grid">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-card"
            :class="{ locked: category.locked }"
            @click="selectCategory(category)"
          >
            <div class="card-glow"></div>
            <div class="card-icon" :style="{ background: category.gradient }">
              <component :is="category.icon" />
            </div>
            <h3 class="card-title">{{ category.name }}</h3>
            <p class="card-desc">{{ category.description }}</p>
            <div class="card-meta">
              <span class="question-count">{{ category.questionCount }} 题</span>
              <span v-if="category.locked" class="lock-badge">
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                  <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
                </svg>
                即将开放
              </span>
              <span v-else class="start-hint">点击开始</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="currentStep === 'quiz'" class="step-quiz">
        <div class="quiz-container">
          <div class="quiz-info-bar">
            <div class="quiz-category">
              <span class="category-badge" :style="{ background: currentCategory?.gradient }">
                {{ currentCategory?.name }}
              </span>
            </div>
            <div class="quiz-progress">
              <div class="progress-text">
                <span>答题进度</span>
                <span class="progress-num">{{ currentQuestionIndex + 1 }} / {{ totalQuestions }}</span>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
              </div>
            </div>
            <div class="quiz-score">
              <span>当前得分</span>
              <span class="score-num">{{ currentScore }}</span>
            </div>
          </div>

          <div class="question-card">
            <div class="question-header">
              <h2 class="question-title">{{ currentQuestion?.title }}</h2>
              <p class="question-desc">{{ currentQuestion?.description }}</p>
            </div>

            <div class="question-content">
              <div class="passage-container">
                <div class="passage-text" v-html="formattedPassage"></div>
              </div>

              <div class="answers-section">
                <div class="answers-header">
                  <h3>填空答案</h3>
                  <span class="answers-hint">前20空每空3分，后10空每空4分</span>
                </div>
                <div class="answers-grid">
                  <div
                    v-for="(answer, index) in userAnswers"
                    :key="index"
                    class="answer-item"
                    :class="{
                      correct: answerStates[index] === true,
                      wrong: answerStates[index] === false,
                      current: currentIndex === index
                    }"
                  >
                    <span class="answer-num">{{ index + 1 }}</span>
                    <input
                      v-model="userAnswers[index]"
                      type="text"
                      class="answer-input"
                      :placeholder="`答案 ${index + 1}`"
                      :disabled="isSubmitted"
                      @focus="currentIndex = index"
                    />
                    <svg v-if="answerStates[index] === true" viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="status-icon correct-icon">
                      <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                    </svg>
                    <svg v-else-if="answerStates[index] === false" viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="status-icon wrong-icon">
                      <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                    </svg>
                  </div>
                </div>
              </div>
            </div>

            <div class="question-actions">
              <button v-if="!isSubmitted" @click="submitAnswers" class="btn-submit" :disabled="!allAnswersFilled">
                <span>提交答案</span>
                <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
              </button>
              <div v-else class="submitted-actions">
                <button @click="retryQuiz" class="btn-retry">
                  <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                    <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
                  </svg>
                  <span>重新答题</span>
                </button>
                <button v-if="currentScore >= 100 && !isVIP" @click="claimVip" class="btn-claim">
                  <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                  <span>领取VIP</span>
                </button>
                <button v-if="currentScore >= 100 && isVIP" @click="backToSelect" class="btn-claim">
                  <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                  <span>继续挑战</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="currentStep === 'result'" class="step-result">
        <div class="result-card" :class="{ success: currentScore >= 100 }">
          <div class="result-icon">
            <svg v-if="currentScore >= 100" viewBox="0 0 24 24" width="80" height="80" fill="currentColor">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="80" height="80" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
            </svg>
          </div>
          <h2 class="result-title">{{ currentScore >= 100 ? '恭喜通关！' : '再接再厉！' }}</h2>
          <div class="result-score">
            <span class="score-label">最终得分</span>
            <span class="score-value">{{ currentScore }}</span>
            <span class="score-unit">分</span>
          </div>
          <p class="result-desc">
            <template v-if="currentScore >= 100">
              {{ isVIP ? '太厉害了！满分通关！' : '您已达成满分，可以领取VIP权限！' }}
            </template>
            <template v-else>
              还差 {{ 100 - currentScore }} 分即可通关，继续努力！
            </template>
          </p>
          <div class="result-actions">
            <button v-if="currentScore >= 100 && !isVIP" @click="claimVip" class="btn-claim-vip">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
              <span>领取VIP权限</span>
            </button>
            <button v-if="currentScore >= 100 && isVIP" @click="backToSelect" class="btn-claim-vip">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
              <span>继续挑战</span>
            </button>
            <button v-if="currentScore < 100" @click="retryQuiz" class="btn-retry-quiz">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
              </svg>
              <span>重新答题</span>
            </button>
            <button @click="backToSelect" class="btn-back-select">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
              </svg>
              <span>选择其他题目</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showVipSuccess" class="vip-success-modal">
      <div class="modal-content">
        <div class="success-icon">
          <svg viewBox="0 0 24 24" width="64" height="64" fill="currentColor">
            <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
          </svg>
        </div>
        <h3>VIP升级成功！</h3>
        <p>恭喜您成为VIP会员，享受所有高级功能</p>
        <button @click="goToChat" class="btn-go-chat">开始体验</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, h } from 'vue'
import { useRouter } from 'vue-router'
import { useSettingsStore } from '@/stores/settings'
import { useAuthStore } from '@/stores/auth'
import { useTransitionStore } from '@/stores/transition'
import { ElMessage } from 'element-plus'
import CyberBackground from '@/components/common/CyberBackground.vue'
import Logo from '@/components/common/Logo.vue'
import ThemeToggle from '@/components/common/ThemeToggle.vue'
import GlitchText from '@/components/common/GlitchText.vue'

const settingsStore = useSettingsStore()
const authStore = useAuthStore()
const transitionStore = useTransitionStore()
const router = useRouter()

const currentStep = ref('select')
const currentCategory = ref(null)
const currentQuestionIndex = ref(0)
const currentScore = ref(0)
const userAnswers = ref([])
const answerStates = ref([])
const isSubmitted = ref(false)
const currentIndex = ref(0)
const showVipSuccess = ref(false)

const isVIP = computed(() => {
  return authStore.user?.role === 'VIP' || authStore.user?.role === 'ADMIN'
})

const InternetMemeIcon = {
  render() {
    return h('svg', { viewBox: '0 0 24 24', width: 32, height: 32, fill: 'currentColor' }, [
      h('path', { d: 'M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z' })
    ])
  }
}

const ChildhoodMemoryIcon = {
  render() {
    return h('svg', { viewBox: '0 0 24 24', width: 32, height: 32, fill: 'currentColor' }, [
      h('path', { d: 'M12 3c-4.97 0-9 4.03-9 9s4.03 9 9 9 9-4.03 9-9c0-.46-.04-.92-.1-1.36-.98 1.37-2.58 2.26-4.4 2.26-2.98 0-5.4-2.42-5.4-5.4 0-1.81.89-3.42 2.26-4.4-.44-.06-.9-.1-1.36-.1z' })
    ])
  }
}

const MusicArcheologyIcon = {
  render() {
    return h('svg', { viewBox: '0 0 24 24', width: 32, height: 32, fill: 'currentColor' }, [
      h('path', { d: 'M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z' })
    ])
  }
}

const ClassicMovieIcon = {
  render() {
    return h('svg', { viewBox: '0 0 24 24', width: 32, height: 32, fill: 'currentColor' }, [
      h('path', { d: 'M18 4l2 4h-3l-2-4h-2l2 4h-3l-2-4H8l2 4H7L5 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4h-4z' })
    ])
  }
}

const categories = ref([
  {
    id: 'internet-meme',
    name: '网络热梗',
    description: '考验你对网络流行梗的了解程度',
    icon: InternetMemeIcon,
    gradient: 'linear-gradient(135deg, #f59e0b 0%, #ef4444 100%)',
    questionCount: 1,
    locked: false
  },
  {
    id: 'childhood-memory',
    name: '童年记忆',
    description: '重温那些年我们一起追过的动画和游戏',
    icon: ChildhoodMemoryIcon,
    gradient: 'linear-gradient(135deg, #8b5cf6 0%, #ec4899 100%)',
    questionCount: 0,
    locked: true
  },
  {
    id: 'music-archeology',
    name: '乐坛考古',
    description: '挖掘那些经典歌曲背后的故事',
    icon: MusicArcheologyIcon,
    gradient: 'linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%)',
    questionCount: 0,
    locked: true
  },
  {
    id: 'classic-movie',
    name: '经典影视',
    description: '回顾那些年感动过我们的经典影视',
    icon: ClassicMovieIcon,
    gradient: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
    questionCount: 0,
    locked: true
  }
])

const questions = {
  'internet-meme': [
    {
      id: 1,
      title: '卢食传说',
      description: '填空题：根据经典网络热梗"卢食传说"填空',
      passage: `你们可能不知道只用___1___赢到___2___是什么概念，我们一般只会用___3___形容这种人，___4___。
我经常说一句话，当年___5___他能用___6___赢到___7___，我卢本伟用___8___赢到___9___，不是问题。
___10___，这个牌不能捡，这个牌不用捡，___11___。反手给一个超级加倍，___12___。
他也___13___，但是不用怕，他的牌赢不了我。五六七八九十，两炸，___14___这个牌，
如果能把这个___15___换成___16___，我这个牌将___17___，可是换不得。
单走一个6——___18___，直接把k走了，走他一张2，顶他。
阿姨快点，___19___，阿姨你___20___都不要吗，阿姨你快点啊阿姨，别___21___的。
五六七八九十，打错了应该打___22___的。给阿姨倒杯茶好吧，给阿姨，给你倒一杯___23___，给阿姨倒一杯___24___。
开始你的___25___，炸他炸他，___26___~
___27___你能秒我，你能秒杀我，你今天能___28___，我！___29___！___30___！
（飞机~~~~）
...................（非禁止画面）`,
      answers: [
        '20万', '578万', '两个字', '赌怪', '陈刀仔', '20块', '3700万', '20万', '500万',
        '埋伏他一手', '他死定了', '闷声发大财', '超级加倍', '很牛逼', 'k', 'j', '绝杀',
        '傻逼', '阿姨', 'k', '磨磨蹭蹭', '2', '卡布奇诺', '卡布奇诺', '炸弹秀', '漂亮',
        '十七张牌', '十七张牌把卢本伟秒了', '当场', '就把这个电脑屏幕吃掉'
      ]
    }
  ]
}

const totalQuestions = computed(() => {
  return currentCategory.value ? questions[currentCategory.value.id]?.length || 0 : 0
})

const progressPercent = computed(() => {
  if (totalQuestions.value === 0) return 0
  return ((currentQuestionIndex.value + 1) / totalQuestions.value) * 100
})

const currentQuestion = computed(() => {
  if (!currentCategory.value) return null
  return questions[currentCategory.value.id]?.[currentQuestionIndex.value] || null
})

const formattedPassage = computed(() => {
  if (!currentQuestion.value) return ''
  let passage = currentQuestion.value.passage
  currentQuestion.value.answers.forEach((_, index) => {
    const placeholder = `___${index + 1}___`
    const filledAnswer = userAnswers.value[index] || `【${index + 1}】`
    const stateClass = answerStates.value[index] === true ? 'filled-correct' :
                       answerStates.value[index] === false ? 'filled-wrong' : 'filled-empty'
    passage = passage.replace(placeholder, `<span class="blank ${stateClass}">${filledAnswer}</span>`)
  })
  return passage.replace(/\n/g, '<br>')
})

const allAnswersFilled = computed(() => {
  return userAnswers.value.every(answer => answer && answer.trim() !== '')
})

const selectCategory = (category) => {
  if (category.locked) {
    ElMessage.info('该题目类型即将开放，敬请期待！')
    return
  }

  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录后再答题')
    router.push('/upgrade-vip')
    return
  }

  currentCategory.value = category
  initQuiz()
  currentStep.value = 'quiz'
}

const initQuiz = () => {
  currentQuestionIndex.value = 0
  currentScore.value = 0
  isSubmitted.value = false
  currentIndex.value = 0

  if (currentQuestion.value) {
    userAnswers.value = new Array(currentQuestion.value.answers.length).fill('')
    answerStates.value = new Array(currentQuestion.value.answers.length).fill(null)
  }
}

const submitAnswers = () => {
  if (!allAnswersFilled.value) {
    ElMessage.warning('请填写所有答案')
    return
  }

  let score = 0
  const correctAnswers = currentQuestion.value.answers

  userAnswers.value.forEach((answer, index) => {
    const isCorrect = answer.trim() === correctAnswers[index]
    answerStates.value[index] = isCorrect

    if (isCorrect) {
      if (index < 20) {
        score += 3
      } else {
        score += 4
      }
    }
  })

  currentScore.value = score
  isSubmitted.value = true

  if (score >= 100) {
    if (isVIP.value) {
      ElMessage.success('恭喜你获得满分！太强了！')
    } else {
      ElMessage.success('恭喜你获得满分！可以领取VIP权限了！')
    }
  } else {
    ElMessage.info(`得分：${score}分，继续努力！`)
  }
}

const retryQuiz = () => {
  initQuiz()
}

const claimVip = async () => {
  try {
    await authStore.upgradeToVip()
    showVipSuccess.value = true
  } catch (error) {
    ElMessage.error('VIP升级失败，请稍后重试')
  }
}

const goToChat = () => {
  showVipSuccess.value = false
  transitionStore.startCardDraw('chat')
  setTimeout(() => {
    router.push('/chat')
  }, 600)
}

const backToSelect = () => {
  currentStep.value = 'select'
  currentCategory.value = null
}

const handleBackToVip = () => {
  transitionStore.startReverseCardDraw('upgrade-vip')
  setTimeout(() => {
    router.push('/upgrade-vip')
  }, 600)
}
</script>

<style scoped>
.quiz-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a0a0f 0%, #12121c 50%, #0d0d15 100%);
  color: #e0e0e0;
  position: relative;
  overflow-x: hidden;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(10, 10, 15, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.quiz-title-nav {
  font-size: 16px;
  font-weight: 600;
  color: #a78bfa;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  color: #a78bfa;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-back:hover {
  background: rgba(139, 92, 246, 0.2);
  border-color: rgba(139, 92, 246, 0.5);
}

.quiz-content {
  padding-top: 100px;
  padding-bottom: 60px;
  max-width: 1200px;
  margin: 0 auto;
  padding-left: 24px;
  padding-right: 24px;
}

.step-select {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.quiz-header {
  text-align: center;
  margin-bottom: 48px;
}

.main-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
}

.title-gradient {
  background: linear-gradient(135deg, #bb00ff 0%, #9900ff 50%, #006eff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 16px;
  color: #9ca3af;
  margin-bottom: 32px;
}

.rules-card {
  display: inline-flex;
  gap: 32px;
  padding: 16px 32px;
  background: rgba(139, 92, 246, 0.08);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 16px;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #d1d5db;
  font-size: 14px;
}

.rule-item svg {
  color: #01d676;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.category-card {
  position: relative;
  padding: 32px;
  background: rgba(30, 30, 45, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.category-card:hover:not(.locked) {
  transform: translateY(-8px);
  border-color: rgba(139, 92, 246, 0.5);
  box-shadow: 0 20px 40px rgba(124, 58, 237, 0.2);
}

.category-card.locked {
  opacity: 0.6;
  cursor: not-allowed;
}

.card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.4s;
}

.category-card:hover:not(.locked) .card-glow {
  opacity: 1;
}

.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 20px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.card-desc {
  color: #9ca3af;
  font-size: 14px;
  margin-bottom: 20px;
  line-height: 1.5;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.question-count {
  font-size: 13px;
  color: #6b7280;
}

.lock-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: rgba(107, 114, 128, 0.2);
  border-radius: 12px;
  font-size: 12px;
  color: #9ca3af;
}

.start-hint {
  font-size: 13px;
  color: #a78bfa;
}

.step-quiz {
  animation: slideIn 0.5s ease;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateX(20px); }
  to { opacity: 1; transform: translateX(0); }
}

.quiz-container {
  max-width: 1000px;
  margin: 0 auto;
}

.quiz-info-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 16px 24px;
  background: rgba(30, 30, 45, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.15);
  border-radius: 16px;
  margin-bottom: 24px;
}

.category-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: white;
}

.quiz-progress {
  flex: 1;
  max-width: 300px;
}

.progress-text {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
  color: #9ca3af;
}

.progress-num {
  color: #a78bfa;
  font-weight: 600;
}

.progress-bar {
  height: 6px;
  background: rgba(139, 92, 246, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #7c3aed, #a855f7);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.quiz-score {
  text-align: right;
}

.quiz-score span:first-child {
  display: block;
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
}

.score-num {
  font-size: 24px;
  font-weight: 700;
  color: #fbbf24;
}

.question-card {
  padding: 40px;
  background: rgba(30, 30, 45, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 24px;
}

.question-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
}

.question-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #fbbf24;
}

.question-desc {
  font-size: 15px;
  color: #9ca3af;
}

.question-content {
  margin-bottom: 32px;
}

.passage-container {
  padding: 24px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 16px;
  margin-bottom: 32px;
  border: 1px solid rgba(139, 92, 246, 0.1);
}

.passage-text {
  font-size: 15px;
  line-height: 2;
  color: #d1d5db;
}

.passage-text :deep(.blank) {
  display: inline-block;
  min-width: 60px;
  padding: 2px 8px;
  margin: 0 2px;
  background: rgba(139, 92, 246, 0.2);
  border-radius: 4px;
  font-weight: 600;
  color: #a78bfa;
}

.passage-text :deep(.filled-correct) {
  background: rgba(16, 185, 129, 0.2);
  color: #34d356;
}

.passage-text :deep(.filled-wrong) {
  background: rgba(239, 68, 68, 0.2);
  color: #f87171;
}

.passage-text :deep(.filled-empty) {
  background: rgba(139, 92, 246, 0.3);
}

.answers-section {
  margin-top: 24px;
}

.answers-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.answers-header h3 {
  font-size: 16px;
  font-weight: 600;
}

.answers-hint {
  font-size: 13px;
  color: #9ca3af;
}

.answers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
}

.answer-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: rgba(30, 30, 45, 0.8);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 10px;
  transition: all 0.3s;
}

.answer-item.current {
  border-color: rgba(139, 92, 246, 0.5);
  box-shadow: 0 0 0 2px rgba(139, 92, 246, 0.1);
}

.answer-item.correct {
  border-color: rgba(16, 185, 129, 0.5);
  background: rgba(16, 185, 129, 0.1);
}

.answer-item.wrong {
  border-color: rgba(239, 68, 68, 0.5);
  background: rgba(239, 68, 68, 0.1);
}

.answer-num {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(139, 92, 246, 0.2);
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #a78bfa;
  flex-shrink: 0;
}

.answer-item.correct .answer-num {
  background: rgba(16, 185, 129, 0.2);
  color: #34d356;
}

.answer-item.wrong .answer-num {
  background: rgba(239, 68, 68, 0.2);
  color: #f87171;
}

.answer-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #e0e0e0;
  font-size: 14px;
}

.answer-input::placeholder {
  color: #6b7280;
}

.answer-input:disabled {
  cursor: not-allowed;
}

.status-icon {
  flex-shrink: 0;
}

.correct-icon {
  color: #34d356;
}

.wrong-icon {
  color: #f87171;
}

.question-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid rgba(139, 92, 246, 0.1);
}

.btn-submit {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #7c3aed 0%, #a855f7 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(124, 58, 237, 0.4);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.submitted-actions {
  display: flex;
  gap: 16px;
}

.btn-retry {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 24px;
  background: rgba(107, 114, 128, 0.2);
  border: 1px solid rgba(107, 114, 128, 0.3);
  border-radius: 12px;
  color: #d1d5db;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-retry:hover {
  background: rgba(107, 114, 128, 0.3);
}

.btn-claim {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 24px;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  border: none;
  border-radius: 12px;
  color: #1f2937;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-claim:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(251, 191, 36, 0.4);
}

.step-result {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  animation: fadeIn 0.5s ease;
}

.result-card {
  text-align: center;
  padding: 48px 64px;
  background: rgba(30, 30, 45, 0.6);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 24px;
  max-width: 500px;
}

.result-card.success {
  border-color: rgba(251, 191, 36, 0.5);
  box-shadow: 0 0 60px rgba(251, 191, 36, 0.2);
}

.result-icon {
  margin-bottom: 24px;
}

.result-icon svg {
  color: #6b7280;
}

.result-card.success .result-icon svg {
  color: #fbbf24;
  animation: starPulse 1s ease infinite;
}

@keyframes starPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.result-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 24px;
}

.result-score {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
}

.score-label {
  font-size: 16px;
  color: #9ca3af;
}

.score-value {
  font-size: 56px;
  font-weight: 700;
  color: #a78bfa;
}

.result-card.success .score-value {
  color: #fbbf24;
}

.score-unit {
  font-size: 20px;
  color: #9ca3af;
}

.result-desc {
  font-size: 15px;
  color: #9ca3af;
  margin-bottom: 32px;
}

.result-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-claim-vip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px 32px;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  border: none;
  border-radius: 12px;
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-claim-vip:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(251, 191, 36, 0.4);
}

.btn-retry-quiz {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 24px;
  background: rgba(139, 92, 246, 0.2);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 12px;
  color: #a78bfa;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-retry-quiz:hover {
  background: rgba(139, 92, 246, 0.3);
}

.btn-back-select {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 24px;
  background: transparent;
  border: 1px solid rgba(107, 114, 128, 0.3);
  border-radius: 12px;
  color: #9ca3af;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-back-select:hover {
  background: rgba(107, 114, 128, 0.1);
  border-color: rgba(107, 114, 128, 0.5);
}

.vip-success-modal {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  animation: fadeIn 0.3s ease;
}

.modal-content {
  text-align: center;
  padding: 48px 64px;
  background: linear-gradient(135deg, rgba(30, 30, 45, 0.95) 0%, rgba(20, 20, 35, 0.95) 100%);
  border: 1px solid rgba(251, 191, 36, 0.3);
  border-radius: 24px;
  box-shadow: 0 0 80px rgba(251, 191, 36, 0.3);
  animation: scaleIn 0.3s ease;
}

@keyframes scaleIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.success-icon {
  margin-bottom: 24px;
}

.success-icon svg {
  color: #fbbf24;
  animation: starPulse 1s ease infinite;
}

.modal-content h3 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #fbbf24;
}

.modal-content p {
  font-size: 15px;
  color: #9ca3af;
  margin-bottom: 32px;
}

.btn-go-chat {
  padding: 14px 32px;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  border: none;
  border-radius: 12px;
  color: #1f2937;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-go-chat:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(251, 191, 36, 0.4);
}

.quiz-page.light-theme {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 50%, #e2e8f0 100%);
  color: #1f2937;
}

.quiz-page.light-theme .nav-bar {
  background: rgba(255, 255, 255, 0.9);
  border-bottom-color: rgba(124, 58, 237, 0.1);
}

.quiz-page.light-theme .btn-back {
  background: rgba(124, 58, 237, 0.08);
  border-color: rgba(124, 58, 237, 0.2);
  color: #7c3aed;
}

.quiz-page.light-theme .rules-card {
  background: rgba(124, 58, 237, 0.05);
  border-color: rgba(124, 58, 237, 0.15);
}

.quiz-page.light-theme .rule-item {
  color: #4b5563;
}

.quiz-page.light-theme .category-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(124, 58, 237, 0.15);
}

.quiz-page.light-theme .category-card:hover:not(.locked) {
  border-color: rgba(124, 58, 237, 0.4);
  box-shadow: 0 20px 40px rgba(124, 58, 237, 0.15);
}

.quiz-page.light-theme .card-desc,
.quiz-page.light-theme .subtitle,
.quiz-page.light-theme .answers-hint,
.quiz-page.light-theme .result-desc,
.quiz-page.light-theme .modal-content p {
  color: #64748b;
}

.quiz-page.light-theme .quiz-info-bar {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(124, 58, 237, 0.1);
}

.quiz-page.light-theme .question-card {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(124, 58, 237, 0.15);
}

.quiz-page.light-theme .passage-container {
  background: rgba(248, 250, 252, 0.8);
  border-color: rgba(124, 58, 237, 0.1);
}

.quiz-page.light-theme .passage-text {
  color: #374151;
}

.quiz-page.light-theme .answer-item {
  background: rgba(248, 250, 252, 0.8);
  border-color: rgba(124, 58, 237, 0.2);
}

.quiz-page.light-theme .answer-input {
  color: #1f2937;
}

.quiz-page.light-theme .result-card {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(124, 58, 237, 0.2);
}

.quiz-page.light-theme .result-card.success {
  border-color: rgba(251, 191, 36, 0.5);
}

.quiz-page.light-theme .modal-content {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  border-color: rgba(251, 191, 36, 0.3);
}
</style>
