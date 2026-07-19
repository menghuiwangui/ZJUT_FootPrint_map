import request from '../utils/request'

// 发送好友请求
export function sendFriendRequestApi(data){
    return request({
        url: '/friend/request',
        method: 'post',
        params: data
    })
}

// 处理好友请求（同意/拒绝/删除）
export function handleFriendRequestApi(data){
    return request({
        url: '/friend/handle',
        method: 'put',
        data: data
    })
}

// 获取好友列表（含用户信息）
export function getFriendListApi(){
    return request({
        url: '/friend/friendList',
        method: 'get'
    })
}

// 获取待处理的好友请求
export function getPendingRequestsApi(){
    return request({
        url: '/friend/pending',
        method: 'get'
    })
}

// 获取指定用户的基本信息
export function getFriendInfoApi(userId){
    return request({
        url: '/friend/userInfo',
        method: 'get',
        params: { userId }
    })
}

// 获取指定用户的公开游记
export function getFriendPostsApi(userId){
    return request({
        url: '/travel/userPosts',
        method: 'get',
        params: { userId }
    })
}
