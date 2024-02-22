<script setup lang="ts">
import {ref} from "vue";
import {closeDialog} from "vant";
import router from "../router";

// 搜索框的关键词
const tagKey = ref('');

const onCancel = () => {
  console.log("点击了")
  tagKey.value = '';
  tagList.value = initTagList;

};

const onClickButtonRight = () => {
  console.log("点击了")
};

// 模拟后台的数据
const activeId = ref([]);  // 是组件选择器右侧选中的值  默认使用字符串存储   可以传递一个数组进行数据的存储
const activeIndex = ref(0);  // 左侧的一级标签的id
const initTagList = [
    {
      text: '性别',
      children: [
        { text: '男', id: '男' },
        { text: '女', id: '女' },
      ],
    },
    {
      text: '学业',
      children: [
        { text: '大一', id: '大一' },
        { text: '大二', id: '大二' },
        { text: '大三', id: '大三' },
      ],
    }
]

// 标签点击删除之后的对应的函数
// 使用foreach进行遍历  只要找到了index相等的数组元素  就使用splice分割掉
const close = (inx) => {
  activeId.value.forEach((item, index) => {
    if(index === inx) {
      activeId.value.splice(inx, 1)
    }
  })
}

let tagList = ref(initTagList)

// 搜索用户信息

/*
* 01. 首先创建一个原始的数据对象
* 02. 里面是对象 + 数组类型进行存储
* 03. 然后展示的数据是使用响应式的方式进行展示 重新使用tagList变量进行存储数据展示
* 04. 接着将原始的数据进行map先拷贝一份children的数据和整个数据，然后将整个数据返回出去即可 【返回时需要注意过滤刚才的临时children里面text符合输入框输入的数据才要】
* */
const onSearch = (val) => {
  tagList.value = initTagList.map(tag => {
    // console.log(tag.children)
    const tempTagChildren = [...tag.children];
    // console.log(tempTagChildren)
    const tempTagParent = {...tag};
    tempTagParent.children = tempTagChildren.filter(item => item.text.includes(tagKey.value));
    return tempTagParent;
  })
};


// 点击搜索按钮跳转到搜索结果页面
const onClickButton = () => {
  router.push({
    path: '/user/list',
    query: {
      tags: activeId.value
    }
  })
}
</script>


<template>
  <!--搜索框-->
  <form action="/">
    <van-search
        @blur="onSearch"
        v-model="tagKey"
        clearable
        show-action
        placeholder="请输入搜索关键词"
        @cancel="onCancel"
        @click-right-icon="onClickButtonRight"
    >
      <template #action>
        <div @click="onClickButton">搜索</div>
      </template>
    </van-search>
  </form>

  <!-- 分割线 -->
  <van-divider content-position="left">已选标签</van-divider>

  <!-- 占位 -->
  <div v-if="activeId.length === 0" style="padding-left: 60px; font-size: 14px;" >请选择您要匹配的标签</div>

  <!-- 标签组件 -->
  <van-tag v-for="(item, index) in activeId" :key="index" :show="true" closeable size="small" type="primary" @close="close(index)" style="margin: 7px 10px;">
    {{item}}
  </van-tag>

  <!-- 占位 -->
  <van-divider content-position="left">可选标签</van-divider>

  <!-- tree选框 -->
  <van-tree-select
      v-model:active-id="activeId"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />
</template>


<style scoped>

</style>