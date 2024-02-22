<script setup>
import {ref} from "vue";
import request from "../plugin/request.ts";
import {Toast} from "vant";
import router from "../router";
import {useRoute} from "vue-router";

const userAccount = ref('');
const userPassword = ref('');
const route = useRoute();

const onSubmit = async (values) => {
  // console.log('submit', values);
  const res  = await request.post('/user/login', {
   ...values
  })
  console.log(res)
  if(res.code === 0) {
    Toast('登录成功！')
    const urlHistory = route.query.redirect
    return window.location.replace(urlHistory)
  }else {
    return Toast("登录失败！")
  }

};
</script>

<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="userAccount"
          name="userAccount"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请填写用户名' }]"
      />
      <van-field
          v-model="userPassword"
          type="password"
          name="userPassword"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
</template>

<style scoped>

</style>