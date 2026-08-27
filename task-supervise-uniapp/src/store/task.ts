import type { Task, TaskListParams, TaskStatus } from '~/api/task'
import { taskApi } from '~/api/task'

export const useTaskStore = defineStore(
  'task',
  () => {
    // 任务列表
    const taskList = ref<Task[]>([])
    // 当前页码
    const currentPage = ref(1)
    // 是否有更多数据
    const hasMore = ref(true)
    // 加载状态
    const loading = ref(false)
    // 刷新状态
    const refreshing = ref(false)
    // 当前筛选状态
    const currentStatus = ref<TaskStatus | 'all'>('all')
    // 任务统计
    const statistics = ref({
      completed: 0,
      inProgress: 0,
      overdue: 0,
      pending: 0,
      total: 0,
    })

    // 加载任务列表
    async function loadTasks(params?: TaskListParams, isRefresh = false) {
      if (loading.value) return
      loading.value = true

      try {
        if (isRefresh) {
          currentPage.value = 1
          hasMore.value = true
        }

        if (!hasMore.value) return

        const queryParams: TaskListParams = {
          page: currentPage.value,
          pageSize: 10,
          ...params,
        }

        if (currentStatus.value !== 'all') {
          queryParams.status = currentStatus.value
        }

        const res = await taskApi.getList(queryParams)
        const { list = [], total = 0 } = res || {}

        if (isRefresh) {
          taskList.value = list
        } else {
          taskList.value.push(...list)
        }

        hasMore.value = taskList.value.length < total
        currentPage.value++

        return { list: taskList.value, total }
      } catch (error) {
        console.error('加载任务列表失败:', error)
        throw error
      } finally {
        loading.value = false
        refreshing.value = false
      }
    }

    // 刷新任务列表
    async function refreshTasks(params?: TaskListParams) {
      refreshing.value = true
      return loadTasks(params, true)
    }

    // 加载更多
    async function loadMore(params?: TaskListParams) {
      return loadTasks(params)
    }

    // 设置筛选状态
    function setStatus(status: TaskStatus | 'all') {
      currentStatus.value = status
    }

    // 添加任务到列表
    function addTask(task: Task) {
      taskList.value.unshift(task)
    }

    // 更新列表中的任务
    function updateTaskInList(task: Task) {
      const index = taskList.value.findIndex((t) => t.id === task.id)
      if (index !== -1) {
        taskList.value[index] = task
      }
    }

    // 从列表中移除任务
    function removeTaskFromList(taskId: number) {
      taskList.value = taskList.value.filter((t) => t.id !== taskId)
    }

    // 加载任务统计
    async function loadStatistics() {
      try {
        const res = await taskApi.getStatistics()
        statistics.value = res || statistics.value
      } catch (error) {
        console.error('加载任务统计失败:', error)
      }
    }

    // 清空任务列表
    function clearTaskList() {
      taskList.value = []
      currentPage.value = 1
      hasMore.value = true
    }

    return {
      addTask,
      clearTaskList,
      currentPage,
      currentStatus,
      hasMore,
      loading,
      loadMore,
      loadStatistics,

      // 方法
      loadTasks,
      refreshing,
      refreshTasks,
      removeTaskFromList,
      setStatus,
      statistics,
      // 状态
      taskList,
      updateTaskInList,
    }
  },
  {
    persist: {
      key: 'task-key',
      storage: {
        getItem: uni.getStorageSync,
        setItem: uni.setStorageSync,
      },
    },
  }
)

export default useTaskStore
