import request from '../utils/request'

export function addLocationApi(data){
    return request({
        url: '/location/addLocation',
        method: 'post',
        data: data
    })
}

export function getLocationApi(data){
    return request({
        url: '/location/getLocation',
        method: 'get',
        params: data
    })
}

export function searchLocationApi(data){
    return request({
        url: '/location/search',
        method: 'post',
        data: JSON.stringify(keyword), 
        headers: { 'Content-Type': 'application/json' }
    })
}

export function getUserLocationsApi(){
    return request({
        url: '/location/locationList',
        method: 'get'
    })
}