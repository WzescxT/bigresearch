angular.module('MetronicApp').controller('CollectProjectCreateController', function(CollectProjectService,$rootScope, $scope,$state, $http, $timeout) {

    $scope.pageTitle="项目管理";




    $scope.collectProject={
        "name":"",
        "group":"新闻资讯",
        "describle":"",
        "templateId":0
    };

    $scope.reset = function(){
        //刷新页面，重新加载
        $state.go('collectproject_create',{},{reload:true});

    }

    $scope.createProject=function(){
        if($scope.collectProject.name==""){
            sweetAlert("请输入项目名称！");
            return;
        }

        if($scope.collectProject.templateId==0){
            sweetAlert("请选择模板！");
            return;
        }

        CollectProjectService.createProject($scope.collectProject).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.currentProject=resp.rt_info;
                swal("success!", resp.rt_msg, "success");
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }
    //$scope.getUserTemplates=function(){
        CollectProjectService.getUserAllTemplates($rootScope.AuthSetting.LoginUser).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.templates=resp.rt_info;
                console.log($scope.templates);
            } else {

            }
        }, function(resp) {
        })



    //}





});
