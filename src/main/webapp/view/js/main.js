/***
Metronic AngularJS App Main Script
***/

/* Metronic App */
var MetronicApp = angular.module("MetronicApp", [
    "ngCookies",
    "ui.router",
    "ui.bootstrap",
    "oc.lazyLoad",
    "ngSanitize",
    "ui.bootstrap",
]);

/* Configure ocLazyLoader(refer: https://github.com/ocombe/ocLazyLoad) */
MetronicApp.config(['$ocLazyLoadProvider', function($ocLazyLoadProvider) {
    $ocLazyLoadProvider.config({
        // global configs go here
    });
}]);

/********************************************
 BEGIN: BREAKING CHANGE in AngularJS v1.3.x:
*********************************************/


//AngularJS v1.3.x workaround for old style controller declarition in HTML
MetronicApp.config(['$controllerProvider', function($controllerProvider) {
    // this option might be handy for migrating old apps, but please don't use it
    // in new ones!
    $controllerProvider.allowGlobals();
}]);

/********************************************
 END: BREAKING CHANGE in AngularJS v1.3.x:
*********************************************/

/* Setup global settings */
MetronicApp.factory('settings', ['$rootScope', function($rootScope) {
    // supported languages
    var settings = {
        layout: {
            pageSidebarClosed: false, // sidebar menu state
            pageContentWhite: true, // set page content layout
            pageBodySolid: false, // solid body color state
            pageAutoScrollOnLoad: 1000 // auto scroll to top on page load
        },
        assetsPath: '../assets',
        globalPath: '../assets/global',
        layoutPath: '../assets/layouts/layout3',
    };

    $rootScope.settings = settings;

    return settings;
}]);

/* Setup App Main Controller */
MetronicApp.controller('AppController', ['$scope', '$rootScope', function($scope, $rootScope) {
    $scope.$on('$viewContentLoaded', function() {
        App.initComponents(); // init core components
        //Layout.init(); //  Init entire layout(header, footer, sidebar, etc) on page load if the partials included in server side instead of loading with ng-include directive 
    });
}]);

/***
Layout Partials.
By default the partials are loaded through AngularJS ng-include directive. In case they loaded in server side(e.g: PHP include function) then below partial 
initialization can be disabled and Layout.init() should be called on page load complete as explained above.
***/

/* Setup Layout Part - Header */
MetronicApp.controller('HeaderController',  ['$cookieStore','$scope','$rootScope', '$modal','$state','LoginService',function($cookieStore,$scope,$rootScope,$modal,$state,LoginService) {

 /*   $scope.$on('$includeContentLoaded', function() {
        Layout.initHeader(); // init header
    });
*/
    //切换导航栏样式

/*
//  导航栏被选中时header.html中被选中元素class="active"
    $scope.param.PageTitle={
        "首页":[],
        "文本采集":["新闻媒体","主流论坛","热门博客","搜索引擎","单页采集","分页采集","分层链接采集"],
        "文本搜索":["",""],
        "文本分析":["",""],
        "系统管理":["个人信息","用户管理","项目管理","数据统计"]
    };

*/

    $rootScope.AuthSetting = {"LoginUser": {}};

    //判断登录状态
    $scope.token = $cookieStore.get('token');

    if (angular.isUndefined($scope.token)) {
        //登录状态：没有登录
        $scope.loginStatus = 0;
    } else {
        //刷新时有token
        LoginService.parseToken().then(function(resp) {
            if (resp.error_code == 0) {
                $rootScope.AuthSetting.LoginUser=resp.rt_info;
                $scope.loginStatus=1;
            } else {
                $scope.loginStatus=0;

            }
        }, function(resp) {
        })

        //调用service获取当前用户信息
    }

    //登录跳转
    $rootScope.toLogin = function(){  //打开模态
            var loginModal = $modal.open({
                templateUrl : 'tpl/modal-login.html',  //指向上面创建的视图
                controller : 'ModalLoginCtrl', // 初始化模态范围
                size : 'md' //大小配置,
            });

            loginModal.result.then(function(resp) {
                $scope.loginStatus=1;
                $scope.LoginUser = resp.rt_info.user;
                $cookieStore.put('token',resp.rt_info.token);
                $rootScope.AuthSetting.LoginUser=resp.rt_info.user;

            }, function() {
                //$log.info('Modal dismissed at: ' + new Date())
            })
        };

        //登出
        $scope.loginOut = function() {
            $scope.LoginUser = null;
            $rootScope.AuthSetting.LoginUser = null;
            $cookieStore.remove('token');
            $scope.loginStatus = 0;
            $state.go("dashboard");
        };

    $scope.designCollectTemplate = function(id) {
        $state.go("collect_listpage", {
            tabId : id
        });
    }
}]);

/*LoginModal*/
MetronicApp.controller('ModalLoginCtrl',function($scope,$modalInstance,LoginService){ //依赖于modalInstance
    $scope.user={"username":"","password":""};
    $scope.warning="";
    $scope.login = function(){
        console.log($scope.user);
        LoginService.getLoginInfo($scope.user).then(function(resp) {
            console.log(resp);
            if (resp.error_code == 0) {
                $modalInstance.close(resp); //关闭并返回当前选项
            } else {
                $scope.warning='*'+resp.error_msg;
            }
        }, function(resp) {
        })
    };
    $scope.cancel = function(){
        $modalInstance.dismiss('cancel'); // 退出
    }
});

/* Setup Layout Part - Sidebar */
MetronicApp.controller('SidebarController', ['$scope', function($scope) {
    $scope.$on('$includeContentLoaded', function() {
        Layout.initSidebar(); // init sidebar
    });
}]);

/* Setup Layout Part - Quick Sidebar */
MetronicApp.controller('QuickSidebarController', ['$scope', function($scope) {
    $scope.$on('$includeContentLoaded', function() {
       setTimeout(function(){
            QuickSidebar.init(); // init quick sidebar
        }, 2000)
    });
}]);
/*

MetronicApp.controller('PageHeadController', ['$scope', function($scope) {
    $scope.$on('$includeContentLoaded', function() {
        Demo.init(); // init theme panel
    })
}]);
 */
/* Setup Layout Part - Sidebar */

/* Setup Layout Part - Theme Panel */
MetronicApp.controller('ThemePanelController', ['$scope', function($scope) {    
    $scope.$on('$includeContentLoaded', function() {
        Demo.init(); // init theme panel
    });
}]);

/* Setup Layout Part - Footer */
MetronicApp.controller('FooterController', ['$scope', function($scope) {
    $scope.$on('$includeContentLoaded', function() {
        Layout.initFooter(); // init footer
    });
}]);

/* Setup Rounting For All Pages */
MetronicApp.config(['$stateProvider', '$urlRouterProvider',function($stateProvider, $urlRouterProvider) {

    // Redirect any unmatched url
    $urlRouterProvider.otherwise("/dashboard.html");
    
    $stateProvider

        // Dashboard
        .state('dashboard', {
            url: "/dashboard.html",
            templateUrl: "views/dashboard.html",            
            data: {pageTitle: 'Admin Dashboard Template'},
            controller: "DashboardController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'MetronicApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            '../assets/global/plugins/morris/morris.css',                            
                            '../assets/global/plugins/morris/morris.min.js',
                            '../assets/global/plugins/morris/raphael-min.js',                            
                            '../assets/global/plugins/jquery.sparkline.min.js',

                            '../assets/pages/scripts/dashboard.min.js',
                            'js/controllers/DashboardController.js',
                        ] 
                    });
                }]
            }
        })

        // AngularJS plugins
        .state('fileupload', {
            url: "/file_upload.html",
            templateUrl: "views/file_upload.html",
            data: {pageTitle: 'AngularJS File Upload'},
            controller: "GeneralPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'angularFileUpload',
                        files: [
                            '../assets/global/plugins/angularjs/plugins/angular-file-upload/angular-file-upload.min.js',
                        ] 
                    }, {
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/GeneralPageController.js'
                        ]
                    }]);
                }]
            }
        })

        // UI Select
        .state('uiselect', {
            url: "/ui_select.html",
            templateUrl: "views/ui_select.html",
            data: {pageTitle: 'AngularJS Ui Select'},
            controller: "UISelectController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'ui.select',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [
                            '../assets/global/plugins/angularjs/plugins/ui-select/select.min.css',
                            '../assets/global/plugins/angularjs/plugins/ui-select/select.min.js'
                        ] 
                    }, {
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/UISelectController.js'
                        ] 
                    }]);
                }]
            }
        })

        // 新闻采集
        .state('collect_news', {
            url: "/collect_news.html",
            templateUrl: "views/collect/collect_news.html",
            cache:false,
            data: {pageTitle: '新闻模板'},
            controller: "CollectNewsController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectNewsController.js',
                            'js/services/collect/CollectNewsService.js',
                            '../assets/global/plugins/bootstrap-select/css/bootstrap-select.min.css',
                            '../assets/global/plugins/select2/css/select2.min.css',
                            '../assets/global/plugins/select2/css/select2-bootstrap.min.css',

                            '../assets/global/plugins/bootstrap-select/js/bootstrap-select.min.js',
                            '../assets/global/plugins/select2/js/select2.full.min.js',

                            '../assets/pages/scripts/components-bootstrap-select.min.js',
                            '../assets/pages/scripts/components-select2.min.js',
                            <!--plugin : bootstrap timepicker-->
                            '../assets/global/plugins//timepicker/css/bootstrap-datetimepicker.min.css',
                            '../assets/global/plugins/timepicker/bootstrap-datetimepicker.js'
                        ]
                    }]);
                }]
            }
        })

        // 博客论坛
        .state('collect_forum', {
            url: "/collect_forum.html",
            templateUrl: "views/collect/collect_forum.html",
            cache:false,
            data: {pageTitle: '博客论坛'},
            controller: "CollectForumController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectForumController.js',
                            'js/services/collect/CollectCusTempService.js',
                            '../assets/global/plugins/bootstrap-select/css/bootstrap-select.min.css',
                            '../assets/global/plugins/select2/css/select2.min.css',
                            '../assets/global/plugins/select2/css/select2-bootstrap.min.css',
                            '../assets/global/plugins/bootstrap-select/js/bootstrap-select.min.js',
                            '../assets/global/plugins/select2/js/select2.full.min.js',

                            '../assets/pages/scripts/components-bootstrap-select.min.js',
                            '../assets/pages/scripts/components-select2.min.js',
                            <!--plugin : bootstrap timepicker-->
                            '../assets/global/plugins//timepicker/css/bootstrap-datetimepicker.min.css',
                            '../assets/global/plugins/timepicker/bootstrap-datetimepicker.js'
                        ]
                    }]);
                }]
            }
        })

        // 商业数据
        .state('collect_business', {
            url: "/collect_business.html",
            templateUrl: "views/collect/collect_business.html",
            cache:false,
            data: {pageTitle: '商业数据'},
            controller: "CollectBusinessController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectBusinessController.js',
                            'js/services/collect/CollectCusTempService.js',
                            '../assets/global/plugins/bootstrap-select/css/bootstrap-select.min.css',
                            '../assets/global/plugins/select2/css/select2.min.css',
                            '../assets/global/plugins/select2/css/select2-bootstrap.min.css',
                            '../assets/global/plugins/bootstrap-select/js/bootstrap-select.min.js',
                            '../assets/global/plugins/select2/js/select2.full.min.js',

                            '../assets/pages/scripts/components-bootstrap-select.min.js',
                            '../assets/pages/scripts/components-select2.min.js'
                        ]
                    }]);
                }]
            }
        })

        // 分页采集
        .state('collect_listpage', {
            params:{"tabId":null},
            cache:false,
            url: "/collect_listpage.html",
            templateUrl: "views/collect/collect_listpage.html",
            data: {pageTitle: '自定义模板'},
            controller: "CollectListPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectListPageController.js',
                            'js/services/collect/CollectCusTempService.js',
                            'js/services/collect/CollectNewsService.js',
                            'js/scripts/angular.min.js',
                            //josn解析使用E:\project\bigresearch\前台页面优化\webapp\view\css\collect_listpage.css
                            'css/collect_listpage.css',
                            '../assets/global/plugins/font-awesome/css/font-awesome.min.css',
                            '../assets/global/plugins/jquery-json/jquery.json.js',
                            '../assets/global/plugins/jquery-json/json2.js',
                            '../assets/global/plugins/jquery-json/jsonlint.js'
                        ]
                    }]);
                }]
            }
        })

        // 搜索模块
        .state('search', {
            url: "/search.html",
            templateUrl: "views/search/search.html",
            data: {pageTitle: '搜索'},
            controller: "SearchController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/search/SearchController.js',
                            'js/services/search/SearchService.js',
                            'js/services/collect/CollectProjectService.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.css'

                        ]
                    }]);
                }]
            }
        })

        //采集项目管理
        .state('collectproject_manage', {
            url: "/collectproject_manage.html",
            templateUrl: "views/collect/project_manage.html",
            data: {pageTitle: '项目管理'},
            controller: "CollectProjectManageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectProjectManageController.js',
                            'js/services/collect/CollectProjectService.js',
                            'js/services/collect/CollectCusTempService.js'
                        ]
                    }]);
                }]
            }
        })

        //采集项目数据展示
        .state('showCollectInfo', {
            params:{"projectId":null},
            data: {pageTitle: '数据展示'},
            url: '/collectInfo.html',
            templateUrl: "views/collect/collect_info.html",
            controller: "CollectInfoController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectInfoController.js',
                            'js/services/collect/CollectProjectService.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.css'
                        ]
                    }]);
                }]
            }
        })
        //项目采集状况展示
        .state('collectproject_control', {
            cache:'false',
            params:{"projectId":null},
            url: '/collectproject_control.html',
            templateUrl: "views/collect/project_control.html",
            controller: "CollectProjectControlController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectProjectControlController.js',
                            'js/services/collect/CollectProjectService.js',
                            'js/services/collect/CollectLogService.js',
                            //josn解析使用E:\project\bigresearch\前台页面优化\webapp\view\css\collect_listpage.css
                            'css/collect_listpage.css',
                            '../assets/global/plugins/font-awesome/css/font-awesome.min.css',
                            '../assets/global/plugins/jquery-json/jquery.json.js',
                            '../assets/global/plugins/jquery-json/json2.js',
                            '../assets/global/plugins/jquery-json/jsonlint.js',
                            //折线图angular-chart.js
                            '../assets/global/plugins/angularjs/plugins/angular-chart/Chart.bundle.min.js',
                            '../assets/global/plugins/angularjs/plugins/angular-chart/angular-chart.js'
                        ]
                    }]);
                }]
            }
        })

        // 项目创建
        .state('collectproject_create', {
            cache:'false',
            url: "/collectproject_create.html",
            templateUrl: "views/collect/project_create.html",
            data: {pageTitle: '项目创建'},
            controller: "CollectProjectCreateController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectProjectCreateController.js',
                            'js/services/collect/CollectProjectService.js',
                            '../assets/global/plugins/bootstrap-select/css/bootstrap-select.min.css',
                            '../assets/global/plugins/select2/css/select2.min.css',
                            '../assets/global/plugins/select2/css/select2-bootstrap.min.css',

                            '../assets/global/plugins/bootstrap-select/js/bootstrap-select.min.js',
                            '../assets/global/plugins/select2/js/select2.full.min.js',

                            '../assets/pages/scripts/components-bootstrap-select.min.js',
                            '../assets/pages/scripts/components-select2.min.js',
                            <!--plugin : bootstrap timepicker-->
                            '../assets/global/plugins//timepicker/css/bootstrap-datetimepicker.min.css',
                            '../assets/global/plugins/timepicker/bootstrap-datetimepicker.js'
                        ]
                    }]);
                }]
            }
        })

        //模板管理
        .state('template_manage', {
            cache:'false',
            url: "/template_manage.html",
            templateUrl: "views/collect/template_manage.html",
            data: {pageTitle: '模板管理'},
            controller: "CollectTemplateManageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/collect/CollectTemplateManageController.js',
                            'js/services/collect/CollectCusTempService.js',
                            //josn解析使用E:\project\bigresearch\前台页面优化\webapp\view\css\collect_listpage.css
                            'css/collect_listpage.css',
                            '../assets/global/plugins/font-awesome/css/font-awesome.min.css',
                            '../assets/global/plugins/jquery-json/jquery.json.js',
                            '../assets/global/plugins/jquery-json/json2.js',
                            '../assets/global/plugins/jquery-json/jsonlint.js'
                        ]
                    }]);
                }]
            }
        })

        // 文本库创建
        .state('textlibrary_manage', {
            url: "/textlibrary_manage.html",
            templateUrl: "views/analysis/textlibrary_manage.html",
            data: {pageTitle: '文本库管理'},
            controller: "TextLibraryManageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/TextLibraryManageController.js',
                            'js/services/analysis/TextLibraryService.js'
                        ]
                    }]);
                }]
            }
        })

        // 文本导入
        .state('textlibrary_import', {
            params:{"textLibraryId":null},
            cache:'false',
            url: "/textlibrary_import.html",
            templateUrl: "views/analysis/textlibrary_import.html",
            data: {pageTitle: '文本导入'},
            controller: "TextLibraryImportController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/TextLibraryImportController.js',
                            'js/services/analysis/TextLibraryService.js',
                            'css/textlibrary_import.css',
                            '../assets/global/plugins/angularjs/plugins/angular-file-upload/angular-file-upload.min.js'
                        ]
                    }]);
                }]
            }
        })

        // 分析项目管理
        .state('analysisproject_manage', {
            url: "/analysisproject_manage.html",
            templateUrl: "views/analysis/project_manage.html",
            data: {pageTitle: '项目管理'},
            controller: "AnalysisProjectManageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/AnalysisProjectManageController.js',
                            'js/services/analysis/AnalysisProjectService.js',
                            'js/services/analysis/TextLibraryService.js'
                        ]
                    }]);
                }]
            }
        })

        //分析项目词频统计
        .state('analysis_wordfrequency', {
            params:{"analysisProjectId":null},
            url: '/analysis_wordfrequency.html',
            data: {pageTitle: '词频统计'},
            templateUrl: "views/analysis/analysis_wordfrequency.html",
            controller: "AnalysisWordFrequencyController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/AnalysisWordFrequencyController.js',
                            'js/services/analysis/WordFrequencyService.js',
                            'js/services/analysis/AnalysisProjectService.js'

                        ]
                    }]);
                }]
            }
        })

        //分析项目聚类分析
        .state('analysis_cluste', {
            params:{"analysisProjectId":null},
            url: '/analysis_cluste.html',
            data: {pageTitle: '聚类分析'},
            templateUrl: "views/analysis/analysis_cluste.html",
            controller: "AnalysisClusteController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/AnalysisClusteController.js',
                            'js/services/analysis/ClusteService.js',
                            'js/services/analysis/AnalysisProjectService.js',


                            //tooltips
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.css'

                        ]
                    }]);
                }]
            }
        })

        //聚类结果具体文本信息展示
        .state('analysis_clusteInfo', {
            params:{"analysisProjectId":null,"clusteId":null},
            url: '/clusteInfo.html',
            templateUrl: "views/analysis/analysis_clusteInfo.html",
            controller: "AnalysisClusteInfoController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/AnalysisClusteInfoController.js',
                            'js/services/analysis/ClusteService.js'

                        ]
                    }]);
                }]
            }
        })

    //分类训练集管理
        .state('analysisproject_training', {
            url: "/analysisproject_training.html",
            templateUrl: "views/analysis/classify_training.html",
            data: {pageTitle: '分类训练集'},
            controller: "ClassifyTrainingController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/ClassifyTrainingController.js',
                            'js/services/analysis/ClassifyTrainingService.js'
                        ]
                    }]);
                }]
            }
        })

    //分类训练集文本查看
        .state('analysis_training_text', {
            params:{"categoryName":null},
            url: "/analysis_training_text.html",
            templateUrl: "views/analysis/classify_trainingText.html",
            data: {pageTitle: '分类训练集文本'},
            controller: "ClassifyTrainingTextController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/ClassifyTrainingTextController.js',
                            'js/services/analysis/ClassifyTrainingService.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.css'

                        ]
                    }]);
                }]
            }
        })

        //分析项目文本分类
        .state('analysis_classify', {
            params:{"analysisProjectId":null},
            url: '/analysis_classify.html',
            data: {pageTitle: '文本分类'},
            templateUrl: "views/analysis/analysis_classify.html",
            controller: "AnalysisClassifyController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/AnalysisClassifyController.js',
                            'js/services/analysis/ClassifyTrainingService.js',
                            'js/services/analysis/ClassifyService.js',
                            'js/services/analysis/AnalysisProjectService.js'
                        ]
                    }]);
                }]
            }
        })

        //文本分类 文本内容查看
        .state('analysis_classifyInfo', {
            params:{"analysisProjectId":null,"categoryName":null},
            url: '/analysis_classifyInfo.html',
            data: {pageTitle: '文本内容'},
            templateUrl: "views/analysis/analysis_classifyInfo.html",
            controller: "AnalysisClassifyInfoController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/AnalysisClassifyInfoController.js',
                            'js/services/analysis/ClassifyService.js',
                            //tooltips
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.css'
                        ]
                    }]);
                }]
            }
        })

        //情感词典
        .state('analysis_sentime_dictionary', {
            url: "/analysis_sentime_dictionary.html",
            templateUrl: "views/analysis/sentime_dictionary.html",
            data: {pageTitle: '情感词典'},
            controller: "SentimeDictionaryController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/SentimeDictionaryController.js',
                            'js/services/analysis/SentimeDictionaryService.js'

                        ]
                    }]);
                }]
            }
        })

        //情感词汇
        .state('analysis_sentime_word', {
            params:{"sentimeDictionaryId":null,"sentimeDictionaryName":null},
            url: '/analysis_sentime_word.html',
            data: {pageTitle: '情感词典'},
            templateUrl: "views/analysis/sentime_word.html",
            controller: "SentimeWordController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/SentimeWordController.js',
                            'js/services/analysis/SentimeWordService.js',
                            'js/services/CommonService.js'
                        ]
                    }]);
                }]
            }
        })

        //情感分析
        .state('analysis_sentime', {
            params:{"analysisProjectId":null},
            url: '/analysis_sentime.html',
            data: {pageTitle: '情感分析'},
            templateUrl: "views/analysis/analysis_sentime.html",
            controller: "AnalysisSentimeController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/AnalysisSentimeController.js',
                            'js/services/analysis/SentimeService.js',
                            'js/services/analysis/AnalysisProjectService.js',
                            //tooltips
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.css'
                        ]
                    }]);
                }]
            }
        })

        //在线分析
        .state('analysis_online', {
            url: '/analysis_online.html',
            data: {pageTitle: '在线分析'},
            cache:false,
            templateUrl: "views/analysis/analysis_online.html",
            controller: "AnalysisOnlineController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            //词性样式
                            '../assets/pages/online_lib/wordstyle.css',
                            'js/controllers/analysis/AnalysisOnlineController.js',
                            'js/services/analysis/AnalysisOnlineService.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tag-cloud/js/ng-tag-cloud.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tag-cloud/css/ng-tag-cloud.css',
                            '../assets/global/plugins/angularjs/plugins/angular-tag-cloud/css/custom.css'

                        ]
                    }]);
                }]
            }
        })

        //数据库可视化工具
        .state('analysis_db', {
            url: '/analysis_db.html',
            cache:false,
            data: {pageTitle: '数据库可视化工具'},
            templateUrl: "views/analysis/analysis_db.html",
            controller: "AnalysisDBController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/analysis/AnalysisDBController.js',
                            'js/services/analysis/AnalysisDBService.js',

                            //angular-tree-control
                            '../assets/global/plugins/angularjs/plugins/angular-tree-control/angular-tree-control.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tree-control/css/tree-control.css',
                            '../assets/global/plugins/angularjs/plugins/angular-tree-control/css/tree-control-attribute.css',

                            //tooltips
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.js',
                            '../assets/global/plugins/angularjs/plugins/angular-tooltips/angular-tooltips.css'
                        ]
                    }]);
                }]
            }
        })

        // UI Bootstrap
        .state('uibootstrap', {
            url: "/ui_bootstrap.html",
            templateUrl: "views/ui_bootstrap.html",
            data: {pageTitle: 'AngularJS UI Bootstrap'},
            controller: "GeneralPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        files: [
                            'js/controllers/GeneralPageController.js'
                        ] 
                    }]);
                }] 
            }
        })

        // Tree View
        .state('tree', {
            url: "/tree",
            templateUrl: "views/tree.html",
            data: {pageTitle: 'jQuery Tree View'},
            controller: "GeneralPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [
                            '../assets/global/plugins/jstree/dist/themes/default/style.min.css',

                            '../assets/global/plugins/jstree/dist/jstree.min.js',
                            '../assets/pages/scripts/ui-tree.min.js',
                            'js/controllers/GeneralPageController.js'
                        ] 
                    }]);
                }] 
            }
        })     

        // Form Tools
        .state('formtools', {
            url: "/form-tools",
            templateUrl: "views/form_tools.html",
            data: {pageTitle: 'Form Tools'},
            controller: "GeneralPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [
                            '../assets/global/plugins/bootstrap-fileinput/bootstrap-fileinput.css',
                            '../assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css',
                            '../assets/global/plugins/bootstrap-markdown/css/bootstrap-markdown.min.css',
                            '../assets/global/plugins/typeahead/typeahead.css',

                            '../assets/global/plugins/fuelux/js/spinner.min.js',
                            '../assets/global/plugins/bootstrap-fileinput/bootstrap-fileinput.js',
                            '../assets/global/plugins/jquery-inputmask/jquery.inputmask.bundle.min.js',
                            '../assets/global/plugins/jquery.input-ip-address-control-1.0.min.js',
                            '../assets/global/plugins/bootstrap-pwstrength/pwstrength-bootstrap.min.js',
                            '../assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js',
                            '../assets/global/plugins/bootstrap-maxlength/bootstrap-maxlength.min.js',
                            '../assets/global/plugins/bootstrap-touchspin/bootstrap.touchspin.js',
                            '../assets/global/plugins/typeahead/handlebars.min.js',
                            '../assets/global/plugins/typeahead/typeahead.bundle.min.js',
                            '../assets/pages/scripts/components-form-tools-2.min.js',
                            'js/controllers/GeneralPageController.js'
                        ] 
                    }]);
                }] 
            }
        })        

        // Date & Time Pickers
        .state('pickers', {
            url: "/pickers",
            templateUrl: "views/pickers.html",
            data: {pageTitle: 'Date & Time Pickers'},
            controller: "GeneralPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [
                            '../assets/global/plugins/clockface/css/clockface.css',
                            '../assets/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css',
                            '../assets/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css',
                            '../assets/global/plugins/bootstrap-colorpicker/css/colorpicker.css',
                            '../assets/global/plugins/bootstrap-daterangepicker/daterangepicker-bs3.css',
                            '../assets/global/plugins/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css',

                            '../assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js',
                            '../assets/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js',
                            '../assets/global/plugins/clockface/js/clockface.js',
                            '../assets/global/plugins/moment.min.js',
                            '../assets/global/plugins/bootstrap-daterangepicker/daterangepicker.js',
                            '../assets/global/plugins/bootstrap-colorpicker/js/bootstrap-colorpicker.js',
                            '../assets/global/plugins/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js',

                            '../assets/pages/scripts/components-date-time-pickers.min.js',

                            'js/controllers/GeneralPageController.js'
                        ] 
                    }]);
                }] 
            }
        })

        // Custom Dropdowns
        .state('dropdowns', {
            url: "/dropdowns",
            templateUrl: "views/dropdowns.html",
            data: {pageTitle: 'Custom Dropdowns'},
            controller: "GeneralPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load([{
                        name: 'MetronicApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [
                            '../assets/global/plugins/bootstrap-select/css/bootstrap-select.min.css',
                            '../assets/global/plugins/select2/css/select2.min.css',
                            '../assets/global/plugins/select2/css/select2-bootstrap.min.css',

                            '../assets/global/plugins/bootstrap-select/js/bootstrap-select.min.js',
                            '../assets/global/plugins/select2/js/select2.full.min.js',

                            '../assets/pages/scripts/components-bootstrap-select.min.js',
                            '../assets/pages/scripts/components-select2.min.js',

                            'js/controllers/GeneralPageController.js'
                        ] 
                    }]);
                }] 
            }
        }) 

        // Advanced Datatables
        .state('datatablesAdvanced', {
            url: "/datatables/managed.html",
            templateUrl: "views/datatables/managed.html",
            data: {pageTitle: 'Advanced Datatables'},
            controller: "GeneralPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'MetronicApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [                             
                            '../assets/global/plugins/datatables/datatables.min.css', 
                            '../assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css',

                            '../assets/global/plugins/datatables/datatables.all.min.js',

                            '../assets/pages/scripts/table-datatables-managed.min.js',

                            'js/controllers/GeneralPageController.js'
                        ]
                    });
                }]
            }
        })

        // Ajax Datetables
        .state('datatablesAjax', {
            url: "/datatables/ajax.html",
            templateUrl: "views/datatables/ajax.html",
            data: {pageTitle: 'Ajax Datatables'},
            controller: "GeneralPageController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'MetronicApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [
                            '../assets/global/plugins/datatables/datatables.min.css', 
                            '../assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css',
                            '../assets/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css',

                            '../assets/global/plugins/datatables/datatables.all.min.js',
                            '../assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js',
                            '../assets/global/scripts/datatable.min.js',

                            'js/scripts/table-ajax.js',
                            'js/controllers/GeneralPageController.js'
                        ]
                    });
                }]
            }
        })

        // User Profile
        .state("profile", {
            url: "/profile",
            templateUrl: "views/profile/main.html",
            data: {pageTitle: 'User Profile'},
            controller: "UserProfileController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'MetronicApp',  
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [
                            '../assets/global/plugins/bootstrap-fileinput/bootstrap-fileinput.css',
                            '../assets/pages/css/profile.css',
                            
                            '../assets/global/plugins/jquery.sparkline.min.js',
                            '../assets/global/plugins/bootstrap-fileinput/bootstrap-fileinput.js',

                            '../assets/pages/scripts/profile.min.js',

                            'js/controllers/UserProfileController.js',

                            'js/services/user/UserService.js',
                            '../assets/global/plugins/angularjs/plugins/angular-file-upload/angular-file-upload.min.js',
                            '../assets/global/plugins//timepicker/css/bootstrap-datetimepicker.min.css',
                            '../assets/global/plugins/timepicker/bootstrap-datetimepicker.js'

                        ]                    
                    });
                }]
            }
        })

        // User Profile Dashboard
        .state("profile.dashboard", {
            url: "/dashboard",
            templateUrl: "views/profile/dashboard.html",
            data: {pageTitle: 'User Profile'}
        })

        // User Profile Account
        .state("profile.account", {
            url: "/account",
            templateUrl: "views/profile/account.html",
            data: {pageTitle: 'User Account'}
        })

        // User Profile Help
        .state("profile.help", {
            url: "/help",
            templateUrl: "views/profile/help.html",
            data: {pageTitle: 'User Help'}      
        })

        // Todo
        .state('todo', {
            url: "/todo",
            templateUrl: "views/todo.html",
            data: {pageTitle: 'Todo'},
            controller: "TodoController",
            resolve: {
                deps: ['$ocLazyLoad', function($ocLazyLoad) {
                    return $ocLazyLoad.load({ 
                        name: 'MetronicApp',  
                        insertBefore: '#ng_load_plugins_before', // load the above css files before '#ng_load_plugins_before'
                        files: [
                            '../assets/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css',
                            '../assets/apps/css/todo-2.css',
                            '../assets/global/plugins/select2/css/select2.min.css',
                            '../assets/global/plugins/select2/css/select2-bootstrap.min.css',

                            '../assets/global/plugins/select2/js/select2.full.min.js',
                            
                            '../assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js',

                            '../assets/apps/scripts/todo-2.min.js',

                            'js/controllers/TodoController.js'  
                        ]                    
                    });
                }]
            }
        })
}]);

/* Init global settings and run the app */
MetronicApp.run(["$rootScope", "settings", "$state", function($rootScope, settings, $state) {
    $rootScope.$state = $state; // state to be accessed from view
    $rootScope.$settings = settings; // state to be accessed from view
    // $rootScope.ServerUrl="http://localhost:8888/";
    $rootScope.ServerUrl="../";
}]);
