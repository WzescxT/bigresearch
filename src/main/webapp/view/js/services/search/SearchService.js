
MetronicApp.factory('SearchService', function ($http, $q, $rootScope) {
    return {

        Search: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "search/listSearchModel", data).success(function (resp) {
                console.log("=====>resp:");
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





