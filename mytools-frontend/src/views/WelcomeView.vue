<template>
    <div class="main">
        <div class="panel">
            <router-view v-slot="{ Component }">
                <transition name="el-fade-in-linear" mode="out-in" class="transition">
                    <component :is="Component" />
                </transition>
            </router-view>
        </div>
        <div class="footer1">
            <div class="board" onclick="window.open('https://space.bilibili.com/8339095')">
                <el-image style="width: 60px;height: 60px" src="src/assets/icon/ic_bilibili.png"/>
                <span>冷尐风Jeeson</span>
                <el-divider direction="vertical" style="height: 45px"/>
                <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="board" onclick="window.location.href = 'tencent://AddContact/?fromId=50&fromSubId=1&subcmd=all&uin=442885735&website=www.oicqzone.com'">
                <el-image style="width: 60px;height: 60px" src="src/assets/icon/ic_qq.png"/>
                <span>442885735</span>
                <el-divider direction="vertical" style="height: 45px"/>
                <el-icon><ArrowRight /></el-icon>
            </div>
        </div>
        <div class="footer2">网页设计：冷尐风Jeeson</div>
        <div class="footer3">问题反馈：JeesonTools@qq.com</div>
    </div>
</template>

<script setup>

import router from "@/router";
import {ArrowRight} from "@element-plus/icons-vue";

router.push('/login')

</script>

<style scoped>
.main {
    width: 100%;
    height: 100%;
    position: fixed;
    background: url('/background.jpeg');
    background-size: 100% 100%;
    display: flex;z-index: 1;
    align-items: center;
    justify-content: center;
}

.panel {
    width: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    z-index: 1;
}

.transition {
    border-radius: 20px;
    padding: 40px;
    width: 100%;
    backdrop-filter: blur(15px);
    box-shadow: 0 0 10px white;
}

.footer1 {
    position:absolute;
    bottom: 110px;
    display: -webkit-flex;
    display: flex;
}

.footer1 .board {
    background-color: rgb(255 255 255 / 75%);
    border-radius: 10px;
    padding: 5px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 10px;
}

.footer1 :hover {
    background-color: rgb(255 255 255);
    box-shadow: 0 0 10px white;
    cursor: pointer;
}

.footer1 .board span {
    user-select: none;
}

.footer2 {
    position:absolute;
    bottom: 60px;
    font-weight: bold;
    color: white;
    font-size: 20px;
    text-shadow: 0 0 10px black;
    user-select: none;
}

.footer3 {
    position:absolute;
    bottom: 20px;
    color: white;
    font-size: 14px;
}
</style>