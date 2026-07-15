import request from '../utils/request'

export function sendFriendRequestApi(data){
    return request({
        url: '/friend/request',
        method: 'post',
        params: data
    })
}

export function handleFriendRequestApi(data){
    return request({
        url: '/friend/handle',
        method: 'put'
        data: data
    })
}

export function getFriendListApi(){
    return request({
        url: '/friend/friendList',
        method: 'get'
    })
}
