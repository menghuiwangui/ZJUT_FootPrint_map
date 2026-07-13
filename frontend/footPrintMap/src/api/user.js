import request from '../utils/request'

export function loginApi(data){
    return request({
        url: '/user/login', 
        method: 'post',
        data: data
    })
}

export function logoutApi(){
    return request({
        url: '/user/logout', 
        method: 'post'
    })
}

export function registerApi(data){
    return request({
        url: '/user/register',
        method: 'post',
        data: data
    })
}

//个人信息查询
export function getPersonalInfoApi(){
    return request({
        url: '/user/currentUser',
        method: 'get'
    })
}

//个人信息修改
export function updatePersonalInfoApi(data){
    return request({
        url: 'user/currentUser',
        method: 'put',
        data: data
    })
}

//个人信息删除
export function deletePersonalInfoApi(){
    return request({
        url: 'user/currentUser',
        method: 'delete',
    })
}

//修改密码
export function updatePasswordApi(data){
    return request({
        url: '/user/password',
        method: 'put',
        data: data
    })
}