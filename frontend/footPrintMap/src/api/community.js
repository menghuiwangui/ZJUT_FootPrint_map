import request from '../utils/request';

export function getPostsApi(){
    return request({
        url: '/travel/',
        method: 'post'
    })
}

export function createPostApi(data){
    return request({
        url: '/travel/addtravel',
        method: 'post',
        data: data
    })
}

// 点赞帖子(对应后端 POST /api/posts/:id/like)
export function likePostApi(postId){
    return request({
        url: `/posts/${postId}/like`,
        method: 'post'
    })
}

export function submitCommentApi(data){
    return request({
        url: '/comments',
        method: 'post',
        data: data
    })
}