<template>
  <div id="content">
    <van-search v-model="searchKey" placeholder="请输入搜索队伍的关键词" @search="onSearch" />
<!--    <van-button class="vbt" icon="plus" round  @click="goFamily"  type="primary">创建队伍</van-button>-->
    <team-list :teams-info-list="teamsInfoList"/>
    <van-empty description="数据为空" v-if="teamsInfoList?.length < 1" />
  </div>
</template>

<script setup>
import {useRouter} from "vue-router";
import TeamList from "../../components/TeamList.vue";
import {onMounted, ref} from "vue";
import request from "../../plugin/request.ts";

const router = useRouter();
const teamsInfoList = ref([]);


const goFamily = () => {
  router.push({
    path: '/team/add'
  })
}
// 公共函数
async function getList(val=''){
  const result = await request.get("/team/list/my/create", {
    params: {
      searchKey: val
    }
  })
  if(result?.code === 0 && result?.data) {
    teamsInfoList.value = result.data;
  }
}

// 02. 页面加载获取所有的队伍信息
onMounted( () => {
  getList()
})

// 03. 搜索队伍
const searchKey = ref('')
const onSearch = () => {
  getList(searchKey.value);
}
</script>

<style scoped>

</style>