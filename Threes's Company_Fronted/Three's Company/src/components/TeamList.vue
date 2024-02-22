<template>
  <div id="content" style="margin-bottom: 50px">
    <van-card
        class="vcd"
        v-for="team in props.teamsInfoList"
        :desc="team.description ? team.description : '这个人很神秘~'"
        :title="team.name"
        :thumb="kun"
    >

      <template #tags>
        <van-tag plain type="primary" style="margin: 7px 0;">
            {{statusEnums[team.status]}}
        </van-tag>
      </template>

      <template #footer>
        <div style="margin-top:25px;">
          <van-button size="small" type="primary" plain v-if="team?.userId === userInfo?.id" @click="goEditTeam(team.id)">编辑队伍</van-button>
          <van-button size="small" type="primary" plain v-if="team?.userId != userInfo?.id && !team.hasJoin"  @click="doJoinTeam(team)">加入队伍</van-button>
          <van-button size="small" type="primary" plain v-if="team?.userId != userInfo?.id && team.hasJoin" @click="doQuitTeam(team.id)">退出队伍</van-button>
          <van-button size="small" type="primary" plain v-if="team?.userId == userInfo?.id" @click="doDelTeam(team.id)">解散队伍</van-button>
        </div>
      </template>



      <template #bottom>
        <div>
          {{`最大人数: ${team.maxNum}`}}
        </div>
        <div>
          {{`已经加入的人数: ${team.hasJoinNum}`}}
        </div>
        <div>
          {{`过期时间: ${team.expireTime.substring(0, 10)}`}}
        </div>
      </template>
    </van-card>
<!--    <van-dialog v-model:show="isShowModle" title="请输入密码" show-cancel-button>-->
<!--      <div>加入队伍</div>-->
<!--    </van-dialog>-->
    <van-popup
        v-model:show="isShowModle"
        round
        position="bottom"
        :style="{ height: '30%' }"
    >
      <template #default>
        <van-field style="padding: 15px;" v-model="password" label="密码" placeholder="请输入密码" />
        <van-button style="margin: 10px auto; margin-left: 70px; width: 60%;" @click="confirm" round type="primary">确认</van-button>
      </template>
    </van-popup>


  </div>
</template>

<script setup lang="ts">
import {statusEnums} from '../common/StatusEnums.ts'
import kun from '../assets/kunkun.png'
import request from "../plugin/request.ts";
import {Toast} from "vant";
import {onMounted, ref} from "vue";
import {getCurrentUserInfo} from "../service/user.ts";
import {useRouter} from "vue-router";


const props = defineProps({
  teamsInfoList: {
    type: [],
    default: []
  }
})


// 01. 加入队伍
const password = ref('');
const isShowModle = ref(false);

// 02. 输入框密码回调  加入队伍需要输入密码
const temp_team = ref();
const confirm = async() => {
  isShowModle.value = false
  const result = await request.post("/team/join", {
    id: temp_team.value.id,
    password: password.value
  })
  if(result?.code === 0 && result.data) {
    Toast.success("加入队伍成功")
  }else {
    Toast.fail(result.description)
  }
  password.value = '';
}

const doJoinTeam = async (team) => {
  temp_team.value = team;
  // isShowModle.value = true;
  if(team.status == 2) {
    return isShowModle.value = true;
  }else {
    const result = await request.post("/team/join", {
      id: temp_team.value.id,
    })
    if(result?.code === 0 && result.data) {
      Toast.success("加入队伍成功")
    }else {
      Toast.fail(result.description)
    }
  }
}

// 02. 获取用户信息
const userInfo = ref();
onMounted(async () => {
  const res = await getCurrentUserInfo();
  // console.log(res, '获取到了用户信息')
  if (res.code === 0) {
    userInfo.value = res.data;
  }
})

// 03. 跳转到队伍编辑页面
const router = useRouter();
const goEditTeam = (val) => {
  router.push({path: "/team/update", query: {
      id: val
    }})
}

// 04. 退出队伍
const doQuitTeam = async (val) => {
  const result = await request.post("/team/live", {
    id:val
  })
  if(result?.code === 0 && result.data) {
    Toast.success("退出队伍成功")
  }else {
    Toast.fail(result.description ? result.description : '退出队伍失败')
  }
}

// 05. 解散队伍~~
const doDelTeam = async (val) => {
  const result = await request.post("/team/delete", {
    id:val
  })
  if(result?.code === 0 && result.data) {
    Toast.success("解散队伍成功")
  }else {
    Toast.fail(result.description ? result.description : '解散队伍失败')
  }
}

</script>

<style scoped>
#content {
  .vcd :deep(.van-card__thumb img) {
    height: 120px;
  }
  .vcd :deep(.van-tag--danger) {
    background: #1989fa;
  }
}
</style>