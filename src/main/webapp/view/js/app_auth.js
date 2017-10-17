
MetronicApp.config(function ($httpProvider) {
    //console.log("appauthIn......");
    $httpProvider.interceptors.push('authInterceptor');

});





//http拦截器
MetronicApp.factory('authInterceptor', function authInterceptor($cookieStore,$rootScope,$q,$window,$location) {
    return {
        request: function (config) {
            var url = config.url;
            config.headers = config.headers || {};
            if($cookieStore.get('token')) {
                config.headers.Authorization = $cookieStore.get('token');
            }else if(url.indexOf("dashboard")==-1 &&url.indexOf("carousel")==-1 && url.indexOf("tpl")==-1&&url.indexOf("modal")==-1&&url.indexOf("login")==-1){
                $location.url('/dashboard.html');
                config.url = "views/dashboard.html";
                window.location.hash="#/views/dashboard.html";

            }


            return config;
        },
        responseError: function (rejection) {
            console.log('authInterceptor responseError ' + rejection.status);
            if (rejection.status === 401) {
                // handle the case where the user is not authenticated
                $location.path("/login");
            }
            return $q.reject(rejection);
        }
    };
})


