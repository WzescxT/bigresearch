angular.module('MetronicApp').factory('UserService', function ($http, $q, $rootScope) {
    return {
        getUserList: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "getUserList", data).success(function (resp) {
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
        updateBasic: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "updateBasic", data).success(function (resp) {
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
        updatePassword: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "updatePassword", data).success(function (resp) {
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
        createUser: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "createUser", data).success(function (resp) {
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
        deleteUser: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "deleteUser", data).success(function (resp) {
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
