import axios from 'axios'


// create an axios instance
const service = axios.create({
    baseURL: 'http://localhost:8080/api', // url = base url + request url
    timeout: 100000,  // request timeout
})
service.defaults.withCredentials = true

// Add a request interceptor
service.interceptors.request.use(function (config) {
    // Do something before request is sent
    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
});

// Add a response interceptor
service.interceptors.response.use(function (response) {
    if(response?.data?.code == 401001) {
        const redirectUrl = window.location.href;
        window.location.href = `/user/login?redirect=${redirectUrl}`
    }
    // Do something with response data
    return response.data;
}, function (error) {
    // Do something with response error
    return Promise.reject(error);
});

export default service
