angular.module('MetronicApp').controller('AnalysisClassifyController',function($cookieStore,$stateParams,AnalysisProjectService,ClassifyService,ClassifyTrainingService,uibPaginationConfig,$rootScope, $scope,$state,$modal,$timeout) {
    $scope.pageTitle = "分类分析";

    $scope.classifyFields={};

    $scope.classifyCategories=[];
    
    $scope.startClassify=function(){
        $scope.analysisProject.classifyStatus='正在分类';
        //获取分类列表
        var categoriesStr = $scope.getCategoriesStr();


        $scope.queryParam={"id":$cookieStore.get('refreshPageParam').analysisProjectId,"classifyCategories":categoriesStr,"classifyFields":angular.toJson($scope.classifyFields)};
        ClassifyService.startClassify($scope.queryParam).then(function(resp) {
                if (resp.error_code == 0) {
                    $scope.getProject();
                    //$scope.getWordsFrequency();
                    sweetAlert("success", resp.rt_msg, "success");
                } else {
                    sweetAlert("error", resp.error_msg, "error");
                }
            }, function(resp) {
            })
    }



    $scope.getProject=function(){
            $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId};
            AnalysisProjectService.getProject($scope.queryParam).then(function(resp) {
                if (resp.error_code == 0) {
                    $scope.analysisProject=resp.rt_info;
                    $scope.getCategories($scope.analysisProject.id);
                    if($scope.analysisProject.classifyStatus==0){
                        $scope.analysisProject.classifyStatus='尚未分类';
                    }else if($scope.analysisProject.classifyStatus==1){
                        $scope.analysisProject.classifyStatus='正在分类';
                    }else{
                        $scope.analysisProject.classifyStatus='分类结束';
                        $scope.getClassifyResult();
                    }




                    $timeout(jQuery.uniform.update, 0);


                    console.log($scope.analysisProject.classifyFields);
                    $scope.classifyFields=angular.fromJson($scope.analysisProject.classifyFields);

                } else {
                    sweetAlert("error", '异常错误', "error");
                }
            }, function(resp) {
            })


    }





    $scope.getCategories=function(projectId){
        $scope.queryParam={"id":projectId};
        ClassifyService.getCategories($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.classifyCategories=resp.rt_info;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })
    }


    $scope.getCategoriesStr=function(){
        var categoriesStr="";
        for(var key in $scope.classifyCategories){
            if($scope.classifyCategories[key]==true){
                categoriesStr=categoriesStr+" "+key;
            }
        }
        return categoriesStr;

    }








//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText="< <";
    uibPaginationConfig.lastText="> >";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







    $scope.projectClustes={};



    $scope.getClassifyResult =function(){

        $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};

        ClassifyService.getClassifyResult($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.classifyResult=resp.rt_mapinfo.classifyResult;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }


    $scope.showClassifyInfo = function(categoryName) {


        $state.go("analysis_classifyInfo", {analysisProjectId :$scope.analysisProject.id,categoryName:categoryName});
    }





















    $scope.$on('$viewContentLoaded', function() {
        if($stateParams.analysisProjectId>0){
            $cookieStore.put('refreshPageParam',$stateParams);
        }
        $scope.getProject();


    });

//metronic bug 无法初始化checkbox和radio的初始值样式，通过一下这句初始化。只通过双向绑定无法解决。
    $timeout(jQuery.uniform.update, 0);


})