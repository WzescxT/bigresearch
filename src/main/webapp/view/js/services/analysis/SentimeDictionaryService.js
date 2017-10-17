MetronicApp.factory('SentimeDictionaryService', function ($http, $q, $rootScope) {
    return {

        getUserDictionary: function (data) {

            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "analysis/getUserDictionary", data).success(function (resp) {
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
        addSentimeDictionary: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "analysis/addSentimeDictionary", data).success(function (resp) {
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
        updateSentimeDictionary: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "analysis/updateSentimeDictionary", data).success(function (resp) {
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
        deleteSentimeDictionary: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "analysis/deleteSentimeDictionary", data).success(function (resp) {
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