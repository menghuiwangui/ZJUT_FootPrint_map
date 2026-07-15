import request from '../utils/request'

export function uploadTravelImageApi(data){
    return request({
        url: '/travelImage/uploadTravelImage',
        method: 'post',
        data: data
    })
}

export function getTravelImageListApi(data){
    return request({    
        url: '/travelImage/travelImageList',
        method: 'get',
        params: data
    })
}

export function deleteTravelImageApi(data){
    return request({
        url: '/travelImage/deleteTravelImage',
        method: 'delete',
        params: data
    })
}
