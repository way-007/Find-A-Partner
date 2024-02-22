<script setup>
import {useRoute} from "vue-router";
import {onMounted, ref} from "vue";
import request from '../plugin/request.ts';
import qs from 'qs';
import {Toast} from "vant";
import UserList from "../components/UserList.vue";

const route = useRoute();
const {tags} = route.query;

const teamInfoList = ref([])
  // {
  //   id: 1,
  //   username: 'way',
  //   userAccount: 'wayadmin',
  //   profile: '大家好，我是小吴。大家好，我是小吴。大家好，我是小吴。大家好，我是小吴。大家好，我是小吴。',
  //   gender: '0',
  //   avatarUrl: 'https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png',
  //   phone: '12345689',
  //   email: '123123@qq.com',
  //   createTime: new Date(),
  //   tags: ["java", "python", "mysql", "php", "idea", "javascript"]
  // }


// 页面一挂载就调用钩子 onMunted

onMounted(async () => {
  const result = await request.get('/user/search/tags', {
    params: {
      tagNameList: tags     // 这里的tags不能使用对象类型的数据  必须将其取出来使用解构赋值的方式取出来使用  否则传递时就会传递一个对象造成参数传递的错误
    },
    paramsSerializer: function (params) {
      return qs.stringify(params, { arrayFormat: "repeat" });
    }
  })
  if(result.code !== 0) {
    return Toast("获取数据失败")
  }

  if(result.data) {
    // 需要将tags里面的json转为字符串的格式才能正常的进行展示标签
    result.data.forEach(item => {
      if(item.tags) {
        item.tags = JSON.parse(item.tags)
      }
    })
  }
  teamInfoList.value = result.data;

})
</script>


<template>

<!--  <van-card-->
<!--      v-for="team in teamInfoList"-->
<!--      :desc="team.profile ? team.profile : '这个人很神秘~'"-->
<!--      :title="team.userAccount"-->
<!--      :thumb="team.avatarUrl"-->
<!--  >-->
<!--    <template #tags>-->
<!--      <van-tag v-for="(user, index) in team.tags" plain type="primary" style="margin: 0 7px 7px 0">{{ user }}</van-tag>-->
<!--    </template>-->
<!--    <template #footer>-->
<!--      <van-button size="small">联系我</van-button>-->
<!--    </template>-->
<!--  </van-card>-->

  <UserList :team-info-list="teamInfoList"/>

</template>


<style scoped>

</style>