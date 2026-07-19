import request from '../utils/request'

// 上传头像图片
export function uploadAvatarImageApi(file){
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/upload/avatarImage',
        method: 'post',
        data: formData,
        timeout: 30000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 上传通用图片
export function uploadImageApi(file){
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/upload/image',
        method: 'post',
        data: formData,
        timeout: 30000,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
