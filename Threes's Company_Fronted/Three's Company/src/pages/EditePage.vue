<script setup lang="ts">
import {ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import request from "../plugin/request.ts";
import {Toast} from "vant";
import {getLoginUserInfo, setLoginUserInfo} from "../state/user.ts";
import {getCurrentUserInfo} from "../service/user.ts";



const route = useRoute();
const router = useRouter();
// console.log(route.query)

// 获取路由传递过来的参数
const editParams = ref({
  editName: route.query.keyVal,
  editLabel: route.query.keyStr,
  edit_name: route.query.keyEnglish
})

const onSubmit = async (values) => {
  const currentUser = await getCurrentUserInfo();
  // 校验用户是否登录
  if(!currentUser) {
    Toast("用户未登录")
    return;
  }
  const result = await request.post('/user/update', {
    'id': currentUser.data.id,
    [editParams.value.edit_name]: editParams.value.editName
  })
  if(result.code === 0 && result.data > 0) {
    // 路由返回上一级之后不会获取到最新的用户信息  因为是使用的本地存储的用户信息  这里需要将本地存储的用户信息设置为null    调用set方法
    let user;
    await setLoginUserInfo(user);
    router.back();
    return Toast("修改成功");
  }
  return Toast("修改失败")
};
</script>

<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="editParams.editName"
          :name="editParams.edit_name"
          :label="editParams.editLabel as string"
          :placeholder="`请输入${editParams.editLabel}`"
          :rules="[{ required: true, message: '请填写用户名' }]"
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