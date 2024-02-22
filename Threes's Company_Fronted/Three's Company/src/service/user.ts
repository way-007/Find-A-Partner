import {getLoginUserInfo, setLoginUserInfo} from "../state/user.ts";
import request from "../plugin/request.ts";

export const getCurrentUserInfo = async () => {
    // 存在就保存为一个常量进行存储，就避免了多次从远程获取减少网络请求
    const loginUserInfo = await getLoginUserInfo();
    if(loginUserInfo) {
        return loginUserInfo;
    }

    // 不存在从远程获取
    const result = await request.get("/user/current");
    if(result.code === 0) {
        setLoginUserInfo(result);
        // 可以再次获取里面存储的用户信息  getLoginUserInfo() 方法进行调用
        return await getLoginUserInfo();
    }


    // 没有获取到的情况
    return null;

}