<template>
  <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);text-align: center">
      <div v-if="store.auth.user" style="font-size: 24px;font-weight: bold">欢迎{{store.auth.user.username}}进入后台管理系统</div>
      <div>
          <el-button @click="logout()" type="danger">退出登录</el-button>
      </div>
  </div>
</template>

<script setup>
import {get} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";
import {useStore} from "@/stores";

const store = useStore()

const logout = () => {
    get("/api/auth/logout", (message) => {
        ElMessage.success(message)
        store.auth.user = null
        router.push('/login')
    })
}
</script>

<style scoped>

</style>