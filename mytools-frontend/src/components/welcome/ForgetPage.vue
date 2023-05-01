<template>
    <div style="text-align: center;height: 314px;overflow: hidden">
        <div>
            <div style="font-size: 25px;font-weight: bold">重置密码</div>
        </div>
        <div style="margin: 20px">
            <el-steps :active="active" finish-status="success" align-center>
                <el-step title="验证邮件" />
                <el-step title="重置密码" />
            </el-steps>
        </div>
        <transition name="el-fade-in-linear" mode="out-in">
            <div v-if="active === 0" style="margin-bottom: 45px;">
                <div style="margin-top: 25px">
                    <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                        <el-form-item prop="email">
                            <el-input v-model="form.email" type="email" placeholder="电子邮箱">
                                <template #prefix>
                                    <el-icon><Message /></el-icon>
                                </template>
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="code">
                            <el-row :gutter="6">
                                <el-col :span="16">
                                    <el-input v-model="form.code" :maxlength="4" type="text" placeholder="请输入验证码">
                                        <template #prefix>
                                            <el-icon><EditPen /></el-icon>
                                        </template>
                                    </el-input>
                                </el-col>
                                <el-col :span="8">
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
                    <el-button style="width: 100%;height: 35px" type="primary" @click="startReset()" plain>立即重置</el-button>
                </div>
                <div style="margin-top: 10px;font-size: 14px;">
                    <el-link type="primary" style="translate: 0 -1px" @click="router.push('/login')">返回</el-link>
                </div>
            </div>
        </transition>

        <transition name="el-fade-in-linear" mode="out-in">
            <div v-if="active === 1">
                <div style="margin-top: 25px">
                    <el-form :model="form" :rules="rules" ref="formRef">
                        <el-form-item prop="password">
                            <el-input v-model="form.password" :maxlength="16" type="password" placeholder="新密码">
                                <template #prefix>
                                    <el-icon><Lock /></el-icon>
                                </template>
                            </el-input>
                        </el-form-item>
                        <el-form-item prop="password_repeat">
                            <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="重复新密码">
                                <template #prefix>
                                    <el-icon><Lock /></el-icon>
                                </template>
                            </el-input>
                        </el-form-item>
                    </el-form>
                </div>
                <div style="margin-top: 30px">
                    <el-button style="width: 100%;height: 35px" type="primary" @click="doReset()" plain>确认重置</el-button>
                </div>
                <div style="margin-top: 10px;font-size: 14px;">
                    <el-link type="primary" style="translate: 0 -1px" @click="back()">返回上一步</el-link>
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup>

import {reactive, ref} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import router from "@/router";
import {post} from "@/net";
import {ElMessage} from "element-plus";

const active = ref(0)

const form = reactive({
    email: '',
    code: '',
    password: '',
    password_repeat: ''
})

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
    email: [
        { required: true, message: '请输入邮件地址', trigger: 'blur' },
        {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change'] },
    ],
    code: [
        { required: true, min: 4, message: '请输入验证码', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 16, message: '密码长度必须为6-16个字符', trigger: ['blur', 'change'] },
    ],
    password_repeat: [
        { validator: validatePassword, trigger: ['blur', 'change'] },
    ],
}

const formRef = ref()
const isEmailValid = ref(false)
const coldTime = ref(0)

const onValidate = (prop, isValid) => {
    if(prop === 'email')
        isEmailValid.value = isValid
}

const validateEmail = () => {
    coldTime.value = 60
    setInterval(() => coldTime.value--, 1000)
    post('/api/auth/valid-email', {
        email: form.email,
        used: 'reset-password'
    }, (message) => {
        ElMessage.success(message)
    }, (message) => {
        ElMessage.warning(message)
        coldTime.value = 0
    })
}

const startReset = () => {
    formRef.value.validate((isValid) => {
        if(isValid) {
            post('/api/auth/start-reset', {
                email: form.email,
                code: form.code
            }, () => {
                active.value++
            })
        } else {
            ElMessage.warning('请填写邮件地址和验证码')
        }
    })
}

const back = () => {
    active.value--
    form.email = ''
    form.code = ''
}

const doReset = () => {
    formRef.value.validate((isValid) => {
        if(isValid) {
            post('/api/auth/do-reset', {
                password: form.password
            }, (message) => {
                ElMessage.success(message)
                router.push('/login')
            })
        } else {
            ElMessage.warning('请填写新的密码')
        }
    })
}

</script>

<style scoped>

</style>