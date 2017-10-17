MetronicApp.factory('WordFrequencyService', function ($http, $q, $rootScope) {
    return {

        getWordsFrequency: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "analysis/getWordsFrequency", data).success(function (resp) {
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
        exportExcel: function (data,fileName) {
            $http.post($rootScope.ServerUrl + "analysis/wordFrequency/exportExcel",data,{responseType: 'arraybuffer'}).success(function(data){
                var blob = new Blob([data], {type: "application/vnd.ms-excel"});
                var objectUrl = URL.createObjectURL(blob);

                    if (window.navigator.msSaveOrOpenBlob) {
                        navigator.msSaveBlob(blob, fileName);
                    } else {
                        var link = document.createElement('a');
                        link.href = window.URL.createObjectURL(blob);
                        link.download = fileName;
                        link.click();
                        window.URL.revokeObjectURL(link.href);
                    }


            })
        }

    }
})