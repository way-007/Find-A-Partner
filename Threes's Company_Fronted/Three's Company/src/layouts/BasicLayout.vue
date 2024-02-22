<script setup lang="ts">
import { Toast } from 'vant';
import 'vant/es/toast/style';
import {ref} from "vue";
import Index from "../pages/Index.vue";
import User from "../pages/User/UserPage.vue";
import Famliy from "../pages/Famliy.vue";
import {useRouter} from "vue-router";
import {on} from "vant/es/lazyload/vue-lazyload/util";


const router = useRouter();
const route = useRouter();
const topTile = ref('')
// 01. 使用路由前置守卫 + 路由源信息来修改标题和网页title
router.beforeEach((to, from, next) => {
  document.title = to.meta.title;
  topTile.value = to.meta.title;
  next();
});

const onClickLeft = () => history.back();
const onClickRight = () => router.push('/search');

const active = ref(0);
const onChange = (active) => {
};
</script>

<template >
  <van-nav-bar :title="topTile"  @click-left="onClickLeft" @click-right="onClickRight" left-arrow>
    <template #right>
      <van-icon name="search" size="18" />
    </template>
  </van-nav-bar>

<!--  <slot name="content">-->
<!--    <div>在这里写入内容</div>-->
<!--  </slot>-->

  <div id="tabs">
<!--    <template v-if="active === 0">-->
<!--      <Index></Index>-->
<!--    </template>-->

<!--    <template v-if="active === 1">-->
<!--      <Famliy></Famliy>-->
<!--    </template>-->

<!--    <template v-if="active === 2">-->
<!--      <User></User>-->
<!--    </template>-->

<!-- 这里跟上面的用法其实差不多   router-view用来表示路由切换之后页面展示的位置   to属性用来匹配页面的切换  切换成那个url之后就展示对应的组件页面展示到router-view中  同时url地址里面还带上了to后面的url    -->
    <router-view />
  </div>


  <van-tabbar @change="onChange" route>
    <van-tabbar-item to="/" icon="home-o">主页</van-tabbar-item>
    <van-tabbar-item to="/family" icon="flag-o">组队</van-tabbar-item>
    <van-tabbar-item to="/user" icon="friends-o">用户</van-tabbar-item>
  </van-tabbar>

</template>

<style scoped>

</style>