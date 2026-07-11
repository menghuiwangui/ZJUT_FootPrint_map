import axios from 'axios';

const instance = axios.create({
    baseURL: '/api'
    timeout: 5000,
})


// 添加请求拦截器
instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token){
            config.headers.Authorization = `Bearer ${token}`; // 设置请求头中的token 转化为Bearer类型
        }
        return config;
    },
    error => {
        console.error('Request error: ' + error.message);
        return Promise.reject(error);
    }
)

// 添加响应拦截器
instance.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        if (error.response && error.response.status === 401){
            alert('登录过期, 请重新登录');
            localStorage.removeItem('token');
            window.Location.href = '/login';
        }
        return Promise.reject(error);
    } 
)

export default instance;