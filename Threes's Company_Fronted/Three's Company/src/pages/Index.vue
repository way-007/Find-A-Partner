<script setup>
import {useRoute} from "vue-router";
import {onMounted, ref, watchEffect} from "vue";
import request from '../plugin/request.ts';
import qs from 'qs';
import {Toast} from "vant";
import UserList from "../components/UserList.vue";

const route = useRoute();

const teamInfoList = ref([])

// // 页面一挂载就调用钩子 onMunted
// onMounted(async () => {
//   const result = await request.get('/user/recommend', {
//     params: {
//       pageSize: 5,
//       pageNum: 1
//     }
//   })
//   if (result.code !== 0) {
//     return Toast(result.message)
//   }
//
//   if (result?.data?.records) {
//     // 需要将tags里面的json转为字符串的格式才能正常的进行展示标签
//     result?.data?.records.forEach(item => {
//       if (item.tags) {
//         item.tags = JSON.parse(item.tags)
//       }
//     })
//   }
//   teamInfoList.value = result?.data?.records;
// })


const isMatched = ref(false);
// 骨架屏
const loadingData = ref(true);

// 01. 将数据加载抽离为一个方法
const loadData = async () => {
  loadingData.value = true
  let result;
  // 表示开启心动模式
  if(isMatched.value) {
    result = await request.get('/user/match', {
      params: {
        num: 10
      }
    })
    if (result.code !== 0) {
      return Toast(result.message ? result.message : '获取数据失败！')
    }
    if (result?.data) {
      // 需要将tags里面的json转为字符串的格式才能正常的进行展示标签
      result?.data.forEach(item => {
        if (item.tags) {
          item.tags = JSON.parse(item.tags)
        }
      })
    }
    teamInfoList.value = result?.data;
  }else {
    result = await request.get('/user/recommend', {
      params: {
        pageSize: 5,
        pageNum: 1
      }
    })
    if (result.code !== 0) {
      return Toast(result.message ? result.message : '获取数据失败！')
    }
    if (result?.data?.records) {
      // 需要将tags里面的json转为字符串的格式才能正常的进行展示标签
      result?.data?.records.forEach(item => {
        if (item.tags) {
          item.tags = JSON.parse(item.tags)
        }
      })
    }
    teamInfoList.value = result?.data?.records;
  }
  loadingData.value = false;
}

// 02. 心动模式

watchEffect(() => {
  loadData();
})
</script>


<template>
  <van-cell center title="点击开启心动匹配模式">
    <template #right-icon>
      <van-switch v-model="isMatched" size="24" />
    </template>
  </van-cell>
  <UserList :loading="loadingData" :teamInfoList="teamInfoList"/>

</template>


<style scoped>

</style>