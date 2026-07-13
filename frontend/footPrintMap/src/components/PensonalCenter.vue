<script setup>
import { ref, onMounted } from 'vue'
import { getPersonalInfoApi, updatePersonalInfoApi, deletePersonalInfoApi } from '../api/user'

const personalInfo = ref({
    nickname: '',
    avatar: '',
    bio: '',
    email: '',
    phone: '',
})

onMounted(async () => {
    try {
        const res = await getPersonalInfoApi()
        if (res.code === 200){
            personalInfo.value = res.data
        }
        else{
            console.error('获取个人信息失败', res)
        }
    }catch(error){
        console.error('网络请求错误', error)
    }
})

async function handleUpdatePersonalInfo(){
    try {
        const res = await updatePersonalInfoApi(personalInfo.value)
        if (res.code === 200){
            console.log('个人信息修改成功!')
        }
        else{
            alert('修改失败' + res.message)
        }
    }catch(error){
        console.error('网络请求错误', error)
    }
}

async function handleDeletePersonalInfo(){
    if (!confirm('确定要删除个人信息吗??')) return 

    try {
        const res = await deletePersonalInfoApi()
        if (res.code === 200){
            alert('账号已注销')
            localStorage.removeItem('token')
            window.location.href('/login')
        }
    }catch(error){
        console.error('注销失败' + error)
    }
}

</script>

<template>
  <div class="profile-container">
    <h2>个人中心</h2>
    
    <!-- 修改信息表单 -->
    <div>
      <input v-model="userInfo.nickname" placeholder="昵称" />
      <input v-model="userInfo.avatar" placeholder="头像链接" />
      <input v-model="userInfo.bio" placeholder="个人简介" />
      <input v-model="userInfo.email" placeholder="邮箱" />
      <input v-model="userInfo.phone" placeholder="手机号" />
      
      <button @click="handleUpdateUser">保存修改</button>
    </div>

    <hr />

    <!-- 危险操作区 -->
    <button @click="handleDeleteUser" style="color: red;">注销账号</button>
  </div>
</template>
