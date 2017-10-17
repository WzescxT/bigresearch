angular.module('MetronicApp').controller('CollectNewsController', function($rootScope, $scope, $http, $timeout,CollectNewsService,anchorScroll) {
    $scope.$on('$viewContentLoaded', function() {
        // initialize core components
        App.initAjax();

        // set default layout mode
        $rootScope.settings.layout.pageContentWhite = true;
        $rootScope.settings.layout.pageBodySolid = false;
        $rootScope.settings.layout.pageSidebarClosed = false;
    });
    $scope.pageTitle="新闻采集模块";





//新浪新闻配置
    $scope.sinaNewsConfig={
        "name":"",
        "describle":"",
        "keyword":"",
        "channel":"",
        "no":"",
        "time":"",
        "stime":"",
        "etime":"",
        "range":"all",
        "source":"",
        "mideatype":""
    }






    $scope.createSinaNewsConfig=function(){
        //表单验证
        if($scope.sinaNewsConfig.name==""){
            swal("模板名称没有填写！");
            return;
        }
        if($scope.sinaNewsConfig.describle==""){
            swal("模板描述没有填写！");
            return;

        }
        if($scope.sinaNewsConfig.keyword==""){
            swal("新闻搜索内容没有填写！");
            return;

        }

        CollectNewsService.createSinaNewsConfig($scope.sinaNewsConfig).then(function(resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");

            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }



    //
    $scope.collectProject={"templateId":1};



    //百度新闻配置
    $scope.baiduNewsConfig={

        "name":"",
        "describle":"",
        "mustKeyword":"",
        "anyoneWord":"",
        "no":0,
        "bt":"",
        "et":"",
        "tn":"newstitledy"
    }


//创建百度模板配置
    $scope.createBaiduNewsConfig=function(){
        //表单验证
        if($scope.baiduNewsConfig.name==""){
            swal("模板名称没有填写！");
            return;
        }
        if($scope.baiduNewsConfig.describle==""){
            swal("模板描述没有填写！");
            return;
        }
        if($scope.baiduNewsConfig.mustKeyword==""&&$scope.baiduNewsConfig.anyoneWord==""){
            swal("新闻搜索内容没有填写！");
            return;
        }
        if($scope.baiduNewsConfig.bt==""){
            swal("搜索开始时间没有填写！");
            return;
        }
        if($scope.baiduNewsConfig.et==""){
            swal("搜索结束时间没有填写！");
            return;
        }
        CollectNewsService.createBaiduNewsConfig($scope.baiduNewsConfig).then(function(resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }



















});
