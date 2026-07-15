import request from '../utils/request'

// 上传头像图片
function uploadAvatorImageApi(file){
    const formData = new FormData()
    formData.append('file',file)
    return request({
        url: '/upload/avatarImage',
        method: 'post',
        data: formData
    })
}

// 上传通用图片
function uploadImageApi(file){
    const formData = new FormData()
    formData.append('file',file)
    return request({
        url: '/upload/image',
        method: 'post',
        data: formData
    })
}