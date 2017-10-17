
MetronicApp.factory('SentimeService', function ($http, $q, $rootScope) {
    return {
        startSentime: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "analysis/startSentime", data).success(function (resp) {
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
        getSentimeResult: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "analysis/getSentimeResult", data).success(function (resp) {
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
        getSentimeDictionaries: function () {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "analysis/getSentimeDictionaries").success(function (resp) {
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


