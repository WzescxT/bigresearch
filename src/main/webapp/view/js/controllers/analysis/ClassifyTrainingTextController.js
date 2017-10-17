angular.module('MetronicApp').controller('ClassifyTrainingTextController', function($cookieStore,uibPaginationConfig,ClassifyTrainingService,$stateParams, $scope) {


    $scope.pageTitle="分类训练文本展示";

    $scope.classifyTrainings={};

    //uibPaginationConfig={itemsPerPage:10,boundaryLinks:!1,directionLinks:!0,firstText:"第一页",previousText:"« ",nextText:" »",lastText:"最后一页",rotate:!0};
    //初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText="< <";
    uibPaginationConfig.lastText="> >";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







    $scope.getPageTrainingText=function(){
        console.log("进入方法");
        $scope.queryParam={"categoryName":$cookieStore.get('refreshPageParam').categoryName,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        console.log($scope.queryParam);
        ClassifyTrainingService.getPageTrainingText($scope.queryParam).then(function(resp) {

            if (resp.error_code == 0) {
                $scope.classifyTrainings=resp.rt_mapinfo.classifyTraining;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
            } else {
                sweetAlert("error", resp.error_msg, "error");

            }
        }, function(resp) {
        })

    }




    $scope.delTraining=function(id){
        console.log("进入方法");
        $scope.queryParam={"id":id};
        ClassifyTrainingService.delTraining($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                sweetAlert("success", resp.rt_msg, "success");
                $scope.getPageTrainingText();

            } else {
                sweetAlert("error", resp.error_msg, "error");

            }
        }, function(resp) {
        })

    }











    $scope.getNewPage=function(){
        $scope.getClusteInfo();
    }





    $scope.$on('$viewContentLoaded', function() {
        console.log($stateParams.categoryName);
        if($stateParams.categoryName!=null&&$stateParams.categoryName!=""){
            //console.log("初始化方法")
            $cookieStore.put('refreshPageParam',$stateParams);
            console.log("初始化====>"+$stateParams);
        }
        //console.log("初始化完毕");



        //$rootScope.refreshPageParam=$stateParams.projectId;
        $scope.getPageTrainingText();







    });



});