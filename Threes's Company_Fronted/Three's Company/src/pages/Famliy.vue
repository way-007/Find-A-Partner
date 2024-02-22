<template>
  <div id="content">
    <div class="top" style="border-bottom: 1px solid #eee; padding-bottom: 10px;">
      <van-search style="width:75%; float: left; vertical-align: top; " v-model="searchKey" placeholder="请输入搜索队伍的关键词" @search="onSearch" />
      <van-button style="height: 40px; margin-top: 8px;" class="vbt"  round  @click="goFamily"  type="primary">创建队伍</van-button>
    </div>
    <van-tabs class="tagsLine" @change="changeTags" v-model:active="activeIndex">
      <van-tab title="公开队伍" name="public"/>
      <van-tab title="加密队伍" name="pwd" />
    </van-tabs>
    <div class="clear" style="clear:both;"></div>
    <team-list :teams-info-list="teamsInfoList"/>
  </div>
</template>

<script setup>
import {useRouter} from "vue-router";
import TeamList from "../components/TeamList.vue";
import {onMounted, ref} from "vue";
import request from "../plugin/request.ts";

const router = useRouter();
const goFamily = () => {
  router.push({
    path: '/team/add'
  })
}
// 公共函数
async function getList(val='', status){
  const result = await request.get("/team/list", {
    params: {
      searchKey: val,
      status
    }
  })
  if(result?.code === 0 && result?.data) {
    teamsInfoList.value = result.data;
  }
}

// 02. 页面加载获取所有的队伍信息
const teamsInfoList = ref([]);
onMounted( () => {
  getList()
})

// 03. 搜索队伍
const searchKey = ref('')
const onSearch = () => {
  getList(searchKey.value);
}

// 04. 标签栏
const activeIndex = ref("public")
const changeTags = (name) => {
  if(name === "public") {
    getList()
  }else {
    getList(searchKey.value, 2)
  }
}
</script>

<style scoped>
.tagsLine :deep(.van-tabs__line) {
  background: #1989fa;
}
</style>