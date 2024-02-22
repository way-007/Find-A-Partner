import {UserInfo} from "../domain/userInfo";

let loginUserInfo:UserInfo;

const setLoginUserInfo = (user: UserInfo)  => {
    loginUserInfo = user;
}

const getLoginUserInfo = (): UserInfo => {
    return loginUserInfo
}

export {
    setLoginUserInfo,
    getLoginUserInfo
}