<template>
    <div style="text-align: center;">
        <div>
            <div style="font-size: 25px;font-weight: bold">用户注册</div>
        </div>
        <div style="margin-top: 25px">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                <el-form-item prop="username">
                    <el-input v-model="form.username" :maxlength="16" type="text" placeholder="用户名">
                        <template #prefix>
                            <el-icon><User /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" :maxlength="16" type="password" placeholder="密码">
                        <template #prefix>
                            <el-icon><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password_repeat">
                    <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="确认密码">
                        <template #prefix>
                            <el-icon><Lock /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="email">
                    <el-input v-model="form.email" type="email" placeholder="电子邮箱">
                        <template #prefix>
                            <el-icon><Message /></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="code">
                    <el-row :gutter="6" >
                        <el-col :span="16">
                            <el-input v-model="form.code" :maxlength="4" type="text" placeholder="请输入验证码">
                                <template #prefix>
                                    <el-icon><EditPen /></el-icon>
                                </template>
                            </el-input>
                        </el-col>
                        <el-col :span="8" >
                            <el-tooltip content="请先输入邮件地址" :disabled="isEmailValid" placement="top">
                                <span><el-button :disabled="!isEmailValid || coldTime > 0" @click="validateEmail()">
                                    {{coldTime > 0 ? '重试 (' + coldTime + ' s)' : '获取验证码'}}
                                </el-button></span>
                            </el-tooltip>
                        </el-col>
                    </el-row>
                </el-form-item>
            </el-form>
        </div>

        <div style="margin-top: 30px">
            <el-button style="width: 100%;height: 35px" type="primary" @click="register()" plain>注册</el-button>
        </div>
        <div style="margin-top: 10px;font-size: 14px;">
            <span style="color: gray">已有账号？</span>
            <el-link type="primary" style="translate: 0 -1px" @click="router.push('/login')">立即登录</el-link>
        </div>
    </div>
</template>

<script setup>
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net";

const form = reactive({
    username: '',
    password: '',
    password_repeat: '',
    email: '',
    code: ''
})

const validateUsername = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入用户名'))
    } else if(!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)){
        callback(new Error('用户名不能包含特殊字符，请使用字母和数字'))
    } else {
        callback()
    }
}

const validatePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== form.password) {
        callback(new Error("两次输入的密码不一致"))
    } else {
        callback()
    }
}

const rules = {
    username: [
        { validator: validateUsername, trigger: ['blur', 'change'] },
        { min: 6, max: 16, message: '用户名长度必须为6-16个字符', trigger: ['blur', 'change'] },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 16, message: '密码长度必须为6-16个字符', trigger: ['blur', 'change'] },
    ],
    password_repeat: [
        { validator: validatePassword, trigger: ['blur', 'change'] },
    ],
    email: [
        { required: true, message: '请输入邮件地址', trigger: 'blur' },
        {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change'] },
    ],
    code: [
        { required: true, min: 4, message: '请输入验证码', trigger: 'blur' },
    ]
}

const formRef = ref()
const isEmailValid = ref(false)
const coldTime = ref(0)

const onValidate = (prop, isValid) => {
    if(prop === 'email')
        isEmailValid.value = isValid
}

const register = () => {
    formRef.value.validate((isValid) => {
        if(isValid) {
            post('/api/auth/register', {
                username: form.username,
                password: form.password,
                email: form.email,
                code: form.code
            }, (message) => {
                ElMessage.success(message)
                router.push('/login')
            })
        } else {
            ElMessage.warning('请完整填写注册信息')
        }
    })
}

const validateEmail = () => {
    coldTime.value = 60
    setInterval(() => coldTime.value--, 1000)
    post('/api/auth/valid-register-email', {
      email: form.email
    }, (message) => {
      ElMessage.success(message)
    }, (message) => {
      ElMessage.warning(message)
      coldTime.value = 0
    })
}

</script>

<style scoped>

</style>