
//servers
angular.module('MetronicApp').factory('LoginService', function ($http, $q, $rootScope) {
    return {
        getLoginInfo: function (data) {
            console.log(data);
            var deferred = $q.defer();
            var promise = deferred.promise;
            console.log(data);
            $http.post($rootScope.ServerUrl + "login", data).success(function (resp) {
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
        parseToken: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "parseToken", data).success(function (resp) {
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
        parseToken: function (data) {
            var deferred = $q.defer();
            var promise = deferred.promise;
            $http.post($rootScope.ServerUrl + "parseToken", data).success(function (resp) {
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





//锚点    //anchorScroll.toView('#newsCollectConfig', true);
//http://www.cnblogs.com/Shirley-He/p/5564178.html
angular.module('MetronicApp').factory('anchorScroll', function () {
        function toView(element, top, height) {
            var winHeight = $(window).height();

            element = $(element);
            height = height > 0 ? height : winHeight / 10;
            $('html, body').animate({
                scrollTop: top ? (element.offset().top - height) : (element.offset().top + element.outerHeight() + height - winHeight)
            }, {
                duration: 200,
                easing: 'linear',
                complete: function () {
                    if (!inView(element)) {
                        element[0].scrollIntoView( !! top);
                    }
                }
            });
        }

        function inView(element) {
            element = $(element);

            var win = $(window),
                winHeight = win.height(),
                eleTop = element.offset().top,
                eleHeight = element.outerHeight(),
                viewTop = win.scrollTop(),
                viewBottom = viewTop + winHeight;

            function isInView(middle) {
                return middle > viewTop && middle < viewBottom;
            }

            if (isInView(eleTop + (eleHeight > winHeight ? winHeight : eleHeight) / 2)) {
                return true;
            } else if (eleHeight > winHeight) {
                return isInView(eleTop + eleHeight - winHeight / 2);
            } else {
                return false;
            }
        }

        return {
            toView: toView,
            inView: inView
        };
    })







angular.module('MetronicApp').factory('factory_passParam', function () {
        var myData = {};

        function _getter() {
            console.log(myData);
            return myData;
        }

        function _setter( a ) {
            myData = a;
        }

        return {
            getter: _getter,
            setter: _setter
        };
    })








