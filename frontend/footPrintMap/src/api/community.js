import request from '../utils/request';

// 创建游记
export function createPostApi(data){
    return request({
        url: '/travel/addTravel',
        method: 'post',
        data: data
    })
}

export function getPostApi(postId){
    return request({
        url: '/travel/getTravel',
        method: 'get',
        params: {
            travelId: postId
        }
    })
}

// 点赞帖子,更新点赞数使用这个接口
export function likePostApi(data){
    return request({
        url: '/travel/updateTravel',
        method: 'put',
        data: data
    })
}

export function getPostListApi(){
    return request({
        url: '/travel/travelList',
        method: 'get',
    })
}

export function pagePostApi(params){
    return request({
        url: '/travel/page',
        method: 'get',
        params: {
            current: params.current || 1,
            size: params.size || 10,
            locationId: params.locationId
        }
    })
}

export function updatePostApi(data){
    return request({
        url: '/travel/updateTravel',
        method: 'put',
        data: data
    })
}

export function deletePostApi(postId){
    return request({
        url: '/travel/deleteTravel',
        method: 'delete',
        params: {
            travelId: postId
        }
    })
}