<template>
  <div id="content">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <!-- 队伍名字 -->
        <van-field
            v-model="formDatas.name"
            name="name"
            label="队伍名"
            placeholder="请输入队伍名"
            :rules="[{ required: true, message: '请填写队伍名' }]"
        />

        <!-- 最大人数 -->
        <van-field name="stepper" label="最大人数">
          <template #input>
            <van-stepper v-model="formDatas.maxNum" :min="3" :max="20"/>
          </template>
        </van-field>

        <!-- 过期时间选择 -->
        <van-field
            is-link
            readonly
            name="datePicker"
            label="过期时间选择"
            placeholder="点击选择过期时间"
            @click="showPicker = true"
            v-model="formDatas.expireTime"
        />
        <van-popup v-model:show="showPicker" position="bottom">
          <van-datetime-picker
              v-model="formDatas.expireTime"
              type="datetime"
              title="选择过期时间"
              :min-date="minDate"
              :max-date="maxDate"
              @confirm="close"
          />
        </van-popup>

        <!-- 单选框选择队伍状态 -->
        <van-field name="radio" label="队伍状态">
          <template #input>
            <van-radio-group v-model="formDatas.status" direction="horizontal">
              <van-radio name="0">公开</van-radio>
              <van-radio name="1">私有</van-radio>
              <van-radio name="2">加密</van-radio>
            </van-radio-group>
          </template>
        </van-field>

        <!-- 队伍加入密码 -->
        <van-field
            v-if="formDatas.status == 2"
            v-model="formDatas.password"
            type="password"
            name="密码"
            label="密码"
            placeholder="请输入队伍密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />

        <!-- 队伍描述 -->
          <van-field
              v-model="formDatas.description"
              rows="1"
              autosize
              size="large"
              label="队伍描述"
              type="textarea"
              placeholder="请输入队伍描述"
              show-word-limit
              maxlength="50"
          />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import request from "../../plugin/request.ts";
import {Toast} from "vant";
import router from "../../router";


const initParams = {
  "description": null,
  "expireTime": null,
  "maxNum": 3,
  "name": null,
  "password": null,
  "status": 0
}
 // 扩展运算发什么意思？  将里面的值依次取出来存储为ref格式的对象数据
const formDatas = ref({...initParams})


// 01. 表单提交
const onSubmit = async () => {
  const requestParmas = {
    ...formDatas.value,
    status: Number(formDatas.value.status),
    expireTime: JSON.stringify(new Date(`${formDatas.value.expireTime}`)).substring(1,25)
  }
  const result = await request.post('/team/add', requestParmas);
  if(result?.code === 0 && result?.data) {
    Toast.success("创建队伍成功")
    router.push({
      path: '/family',
      replace: true
    })
  }else {
    Toast.fail("创建队伍失败")
  }
};

// 01-1 点击选择过期时间
const showPicker = ref(false);
const minDate = new Date();
const maxDate = new Date(2030, 1, 1);
const close = (values) => {
  console.log(values)
  showPicker.value = !showPicker.value
  formDatas.value.expireTime = values
}

</script>

<style scoped>

</style>