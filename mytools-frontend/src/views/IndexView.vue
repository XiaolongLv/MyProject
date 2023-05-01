<template>
    <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);text-align: center">
        <div v-if="store.auth.user" style="font-size: 24px;font-weight: bold">欢迎{{store.auth.user.username}}进入网站主页</div>
        <div v-else style="font-size: 24px;font-weight: bold">网站主页</div>
        <div v-if="store.auth.user">
            <el-button @click="logout()" type="danger">退出登录</el-button>
        </div>
    </div>

</template>

<script setup>
import {useStore} from "@/stores";
import {ElMessage} from "element-plus";
import {get} from "@/net";
import router from "@/router";

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