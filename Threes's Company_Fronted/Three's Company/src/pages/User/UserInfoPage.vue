<script setup lang="ts">
import {useRouter} from "vue-router";
import {nextTick, onMounted, ref} from "vue";
import request from "../../plugin/request.ts";
import {getCurrentUserInfo} from "../../service/user.ts";
import {getLoginUserInfo} from "../../state/user.ts";

const userInfo = ref();
const router = useRouter();


// 点击跳转到修改个人信息页面
const toEdit = (keyVal: string, keyStr: string, keyEnglish: string) => {
  router.push({
    path: '/edit',
    query: {
      keyVal: keyVal,
      keyStr: keyStr,
      keyEnglish: keyEnglish
    }
  })
}



onMounted(async () => {
  const res = await getCurrentUserInfo();
  // console.log(res, '获取到了用户信息')
  if (res.code === 0) {
    userInfo.value = res.data;
  }
})

</script>

<template>
    <div style="margin-top: 20px;"></div>
    <template v-if="userInfo">
      <van-cell title="头像" to="/edit" is-link @click="toEdit(userInfo.avatarUrl, '头像', 'avatarUrl')"
                :value="userInfo.avatarUrl">
        <img style="height: 50px" :src="userInfo.avatarUrl"/>
      </van-cell>
      <van-cell title="昵称" to="/edit" @click="toEdit(userInfo.username, '昵称', 'username')" is-link
                :value="userInfo.username"/>
      <van-cell title="账户名" is-link :value="userInfo.userAccount"/>
      <van-cell title="性别" to="/edit" is-link @click="toEdit(userInfo.gender, '性别', 'gender')"
                :value="userInfo.gender ? '男' : '女'"/>
      <van-cell title="电话" to="/edit" is-link @click="toEdit(userInfo.phone, '电话', 'phone')"
                :value="userInfo.phone"/>
      <van-cell title="邮箱" to="/edit" is-link @click="toEdit(userInfo.email, '邮箱', 'email')"
                :value="userInfo.email"/>
      <van-cell title="创建时间" is-link :value="userInfo.createTime"/>
    </template>
</template>
<style scoped>

</style>