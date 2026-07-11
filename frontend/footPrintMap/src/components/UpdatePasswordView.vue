<script setup>
import { ref } from 'vue'
import { updatePasswordApi } from '../api/user'

const formdata = ref({
    oldPassword: '',
    newPassword: '',
})

const emit = defineEmits(['back-login'])

async function handleUpdatePassword() {
    // 1. 前端基础校验
    if (!formdata.value.oldPassword || !formdata.value.newPassword) {
        alert('旧密码和新密码不能为空！')
        return
    }
    if (formdata.value.newPassword.length < 6) {
        alert('新密码长度不能少于 6 位！')
        return
    }
    if (formdata.value.oldPassword === formdata.value.newPassword) {
        alert('新密码不能和旧密码相同！')
        return
    }

    try {
        const res = await updatePasswordApi(formdata.value)
        if (res.code === 200) {
            alert('密码修改成功! 请重新登录')
            
            // 2. 清除本地存储的旧 Token (重要！)
            localStorage.removeItem('token')
            
            // 3. 跳转到登录页面
            emit('back-login')
        } else {
            // 如果后端返回了具体的错误信息（比如旧密码错误），优先显示后端的
            alert(res.message || '密码修改失败! 请检查旧密码是否正确!')
        }
    } catch (error) {
        console.error('网络或服务器异常:', error)
        alert('网络异常，请稍后重试')
    }
}
</script>

<template>
  <div class="auth-container">
    <div class="bg-bubble bubble-1"></div>
    <div class="bg-bubble bubble-2"></div>

    <div class="auth-card">
      <div class="auth-header">
        <div class="logo">🔑</div>
        <h2>修改密码</h2>
        <p>请验证旧密码并设置新密码</p>
      </div>

      <form @submit.prevent="handleUpdatePassword" class="auth-form">
        <div class="input-group">
          <span class="icon">🔒</span>
          <input type="password" v-model="formdata.oldPassword" placeholder="请输入旧密码" required />
        </div>
        <div class="input-group">
          <span class="icon">🔑</span>
          <input type="password" v-model="formdata.newPassword" placeholder="请输入新密码 (至少6位)" required />
        </div>

        <button type="submit" class="btn-auth">确认修改</button>
      </form>

      <div class="auth-footer">
        想起来了？ <a @click="emit('back-login')">返回登录</a>
      </div>
    </div>
  </div>
</template>
