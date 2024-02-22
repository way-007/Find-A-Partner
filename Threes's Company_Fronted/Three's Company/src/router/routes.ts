import Famliy from "../pages/Famliy.vue";
import Index from "../pages/Index.vue";
import SearchPage from "../pages/SearchPage.vue";
import EditePage from "../pages/EditePage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";
import AddPage from "../pages/Team/AddPage.vue";
import UpdatePage from "../pages/Team/UpdatePage.vue";
import UserInfoPage from "../pages/User/UserInfoPage.vue";
import UserPage from "../pages/User/UserPage.vue";
import UserJoinedPage from "../pages/User/UserJoinedPage.vue";
import UserCreatedPage from "../pages/User/UserCreatedPage.vue";

const routes = [
    /*组队*/
    { path: '/family', component: Famliy, meta:{title: "组队"} },
    { path: '/', component: Index, meta:{title: "主页"} },

    /*个人中心*/
    { path: '/user', component: UserPage, meta:{title: "用户"} },
    { path: '/user/info', component: UserInfoPage, meta:{title: "个人信息修改"} },
    { path: '/user/team/add', component: UserJoinedPage, meta:{title: "我加入的队伍"} },
    { path: '/user/team/create', component: UserCreatedPage, meta:{title: "我创建的队伍"} },
    {path: '/edit', component: EditePage, meta:{title: "修改个人信息"}},

    { path: '/user/list', component: SearchResultPage, meta:{title: "修改个人信息"} },
    { path: '/user/login', component: UserLoginPage, meta:{title: "登录"} },
    { path: '/search', component: SearchPage, meta:{title: "搜索"} },
    { path: '/team/add', component: AddPage, meta:{title: "创建队伍"} },
    { path: '/team/update', component: UpdatePage, meta:{title: "修改队伍信息"} },
]

export default routes;