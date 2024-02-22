<script setup lang="ts">

const props = defineProps({
  teamInfoList: {
    type: [],
    default: []
  },
  loading: {
    type: Boolean
  }
})

</script>

<template>
  <van-skeleton title avatar :row="5" :loading="props.loading" v-for="team in props.teamInfoList">
    <div id="content" style="margin-bottom: 50px">
      <van-card
          :desc="team.profile ? team.profile : '这个人很神秘~'"
          :title="team.userAccount"
          :thumb="team.avatarUrl"
      >
        <template #tags>
          <van-tag v-for="(user, index) in team.tags" plain type="primary" style="margin: 0 7px 7px 0">{{
              user
            }}
          </van-tag>
        </template>
        <template #footer>
          <van-button size="small">联系我</van-button>
        </template>c
      </van-card>
    </div>
  </van-skeleton>
</template>

<style scoped>

</style>