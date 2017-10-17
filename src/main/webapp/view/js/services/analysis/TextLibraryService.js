
MetronicApp.factory('TextLibraryService', function ($http, $q, $rootScope) {
    return {
        createTextLibrary: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "analysis/createTextLibrary", data).success(function (resp) {
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
        deleteText: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "analysis/deleteText", data).success(function (resp) {
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
        deleteTextLibrary: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "analysis/deleteTextLibrary", data).success(function (resp) {
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

        //获取当前用户的所有文本库
        getTextLibraries: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "analysis/getTextLibraries", data).success(function (resp) {
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
        //获取当前用户的所有文本库
        getUserAllTextLibraries: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "analysis/getUserAllTextLibraries", data).success(function (resp) {
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
        //根據id
        getTextlibrary: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "analysis/getTextlibrary", data).success(function (resp) {
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


