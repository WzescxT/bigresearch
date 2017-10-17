/***
GLobal Directives
***/

// Route State Load Spinner(used on page or content load)
MetronicApp.directive('ngSpinnerBar', ['$rootScope',
    function($rootScope) {
        return {
            link: function(scope, element, attrs) {
                // by defult hide the spinner bar
                element.addClass('hide'); // hide spinner bar by default

                // display the spinner bar whenever the route changes(the content part started loading)
                $rootScope.$on('$stateChangeStart', function() {
                    element.removeClass('hide'); // show spinner bar
                    Layout.closeMainMenu();
                });

                // hide the spinner bar on rounte change success(after the content loaded)
                $rootScope.$on('$stateChangeSuccess', function() {
                    element.addClass('hide'); // hide spinner bar
                    $('body').removeClass('page-on-load'); // remove page loading indicator
                    //Layout.setMainMenuActiveLink('match'); // activate selected link in the sidebar menu

                    // auto scorll to page top
                    setTimeout(function () {
                        App.scrollTop(); // scroll to the top on content load
                    }, $rootScope.settings.layout.pageAutoScrollOnLoad);                    
                });

                // handle errors
                $rootScope.$on('$stateNotFound', function() {
                    element.addClass('hide'); // hide spinner bar
                });

                // handle errors
                $rootScope.$on('$stateChangeError', function() {
                    element.addClass('hide'); // hide spinner bar
                });
            }
        };
    }
])

// Handle global LINK click
MetronicApp.directive('a',
    function() {
        return {
            restrict: 'E',
            link: function(scope, elem, attrs) {
                if (attrs.ngClick || attrs.href === '' || attrs.href === '#') {
                    elem.on('click', function(e) {
                        e.preventDefault(); // prevent link click for above criteria
                    });
                }
            }
        };
    });

// Handle Dropdown Hover Plugin Integration
MetronicApp.directive('dropdownMenuHover', function () {
  return {
    link: function (scope, elem) {
      elem.dropdownHover();
    }
  };  
});

//timepicker
MetronicApp.directive('ngTime', function() {
        return {
            restrict : 'A',
            require : '?ngModel',
            link : function($scope, $element, $attrs, $ngModel) {
                if (!$ngModel) {
                    return;
                }
                $('.form_datetime').datetimepicker({
                    weekStart: 1,
                    todayBtn:  1,
                    autoclose: 1,
                    todayHighlight: 1,
                    startView: 2,
                    forceParse: 0,
                    showMeridian: 1
                });
                $('.form_date').datetimepicker({
                    language:  'fr',
                    weekStart: 1,
                    todayBtn:  1,
                    autoclose: 1,
                    todayHighlight: 1,
                    startView: 2,
                    minView: 2,
                    forceParse: 0
                });
                $('.form_time').datetimepicker({
                    language:  'fr',
                    weekStart: 1,
                    todayBtn:  1,
                    autoclose: 1,
                    todayHighlight: 1,
                    startView: 1,
                    minView: 0,
                    maxView: 1,
                    forceParse: 0
                });
            },
        };
    });


//编译html模板    https://segmentfault.com/q/1010000002593982/a-1020000002601287
MetronicApp.directive('jxbBindCompiledHtml', function ($compile) {
    'use strict';

    return {
        template: '<div></div>',
        scope: {
            rawHtml: '=jxbBindCompiledHtml'
        },
        link: function (scope, elem, attrs) {
            scope.$watch('rawHtml', function (value) {

                if (!value) {
                    return;
                }

                // we want to use the scope OUTSIDE of this directive
                // (which itself is an isolate scope).
                var newElem = $compile($.parseHTML(value))(scope.$parent);
                elem.contents().remove();
                elem.append(newElem);
            });
        }
    };
});

/*

针对textarea双向绑定失效
 <textarea my-textarea={{content}} style="width: 100%;height: 300px;background-color: white; font-size:18px;padding-left: 3%;padding-right: 3% "  contenteditable="true" placeholder="我是placeholder" ng-model="content" ng-change="refresh()"></textarea>

 */
MetronicApp.directive('myTextarea', function() {

    return {

        require: 'ngModel',

        link: function(scope, ele, attrs, modelController) {

            var text = attrs.myTextarea;
            var placeholder = attrs.placeholder;

            var alltext = text + ' ' + placeholder;

            ele.attr('placeholder', alltext);

            ele.on('focus', function () {
                if (!modelController.$modelValue) {
                    setVal(text);
                }

            });

            ele.on('blur', function () {
                console.log("in blur");
                if (modelController.$modelValue === text) {
                    console.log("set value1");
                    console.log(modelController.$modelValue);
                    console.log(text);
                    setVal(text);
                }


            });

            function setVal(v) {
                modelController.$setViewValue(v);
                modelController.$render();
            }
        }}}
        );





