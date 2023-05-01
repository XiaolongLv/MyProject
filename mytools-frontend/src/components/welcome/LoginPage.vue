<template>
    <div style="text-align: center;">
        <div>
            <div style="font-size: 25px;font-weight: bold">用户登录</div>
        </div>
        <div style="margin-top: 25px">
            <el-input v-model="form.username" type="text" placeholder="请输入用户名或邮箱">
                <template #prefix>
                    <el-icon><User /></el-icon>
                </template>
            </el-input>
            <el-input v-model="form.password" type="password" style="margin-top: 15px" placeholder="请输入密码">
                <template #prefix>
                    <el-icon><Lock /></el-icon>
                </template>
            </el-input>
        </div>
        <el-row style="margin-top: 5px;">
            <el-col :span="12" style="text-align: left">
                <el-checkbox v-model="form.remember" label="记住我"/>
            </el-col>
            <el-col :span="12" style="text-align: right;margin-top: 3px">
                <el-link @click="router.push('/forget')" :underline="false">忘记密码？</el-link>
            </el-col>
        </el-row>
        <div style="margin-top: 15px">
            <el-button @click="login()" style="width: 48%" type="primary">登录</el-button>
            <el-button @click="router.push('/register')" style="width: 48%" plain>注册</el-button>
        </div>
    </div>
</template>

<script setup>
import {User, Lock} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";
import router from "@/router";

const form = reactive({
    username: '',
    password: '',
    remember: false
})

const login = () => {
    if(!form.username || !form.password) {
        ElMessage.warning('请填写用户名和密码')
    } else {
        post('/api/auth/login', {
            username: form.username,
            password: form.password,
            remember: form.remember
        }, (message) => {
            ElMessage.success(message)
            window.removeEventListener('keydown',keydown,false)
            router.push('/index')
        })
    }
}

const keydown = (e) => {
    if (e.key === "Enter")
        login()
}

window.addEventListener('keydown', keydown)

</script>

<style scoped>

</style>