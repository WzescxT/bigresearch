
MetronicApp.factory('CollectNewsService', function ($http, $q, $rootScope) {
    return {
        createProject: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "collect/createProject", data).success(function (resp) {
                console.log(resp);
                if (resp) {
                    deferred.resolve(resp);
                } else {
                    deferred.reject(resp);
                }
            });

            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }
            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }
            return promise;
        },
        createSinaNewsConfig: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "template/sinaNewsConfig", data).success(function (resp) {
                console.log(resp);
                if (resp) {
                    deferred.resolve(resp);
                } else {
                    deferred.reject(resp);
                }
            });

            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }
            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }
            return promise;
        },
        createBaiduNewsConfig: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "template/baiduNewsConfig", data).success(function (resp) {
                console.log(resp);
                if (resp) {
                    deferred.resolve(resp);
                } else {
                    deferred.reject(resp);
                }
            });

            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }
            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }
            return promise;
        },
        startCollect: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "collect/startCollect", data).success(function (resp) {
                console.log(resp);
                if (resp) {
                    deferred.resolve(resp);
                } else {
                    deferred.reject(resp);
                }
            });

            promise.success = function (fn) {
                promise.then(fn);
                return promise;
            }
            promise.error = function (fn) {
                promise.then(null, fn);
                return promise;
            }
            return promise;
        }
    }

})


