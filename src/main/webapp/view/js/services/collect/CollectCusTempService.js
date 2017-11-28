
MetronicApp.factory('CollectCusTempService', function ($http, $q, $rootScope) {
    return {
        createCusTemp: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "template/createCustom", data).success(function (resp) {
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
        getUserTemplates: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "template/getUserTemplates", data).success(function (resp) {
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
        crawltest: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "collect/CrawledData", data).success(function (resp) {
                var testresult = resp["testResult"];
                return testresult;
            });
        },
        getTemplateById: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "template/getTemplateById", data).success(function (resp) {
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
        deleteTemplate: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "template/deleteOne", data).success(function (resp) {
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


