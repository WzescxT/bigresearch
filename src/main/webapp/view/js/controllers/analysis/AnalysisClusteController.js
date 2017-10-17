angular.module('MetronicApp').controller('AnalysisClusteController',function($cookieStore,$stateParams,AnalysisProjectService,ClusteService,uibPaginationConfig,$rootScope, $scope,$state,$modal,$timeout) {
    $scope.pageTitle = "聚类分析";


    $scope.clusteFields={};


    $scope.startCluste=function(){
        $scope.analysisProject.clusteStatus='正在聚类';
        console.log($scope.analysisProject);
            $scope.queryParam={"id":$cookieStore.get('refreshPageParam').analysisProjectId,"clusteAlgorithm":$scope.analysisProject.clusteAlgorithm,"clusteFields":angular.toJson($scope.clusteFields),"clusteCount":$scope.analysisProject.clusteCount};
            AnalysisProjectService.startCluste($scope.queryParam).then(function(resp) {
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
                    console.log($scope.analysisProject);
                    if($scope.analysisProject.clusteStatus==0){
                        $scope.analysisProject.clusteStatus='尚未聚类';
                    }else if($scope.analysisProject.clusteStatus==1){
                        $scope.analysisProject.clusteStatus='正在聚类';
                    }else{
                        $scope.analysisProject.clusteStatus='聚类结束';
                        $scope.getClusteResult();
                    }




                    $timeout(jQuery.uniform.update, 0);



                    $scope.clusteFields=angular.fromJson($scope.analysisProject.clusteFields);






                } else {
                    sweetAlert("error", 异常错误, "error");
                }
            }, function(resp) {
            })


    }







//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText="< <";
    uibPaginationConfig.lastText="> >";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







    $scope.projectClustes={};



    $scope.getClusteResult =function(){

        $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};

        ClusteService.getClusteResult($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.projectClustes=resp.rt_mapinfo.analysisProjectCluste;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }


    $scope.showClusteInfo = function(clusteId) {


        $state.go("analysis_clusteInfo", {analysisProjectId :$scope.analysisProject.id,clusteId:clusteId});
    }





















    $scope.$on('$viewContentLoaded', function() {
        if($stateParams.analysisProjectId>0){
            //console.log("初始化方法")
            $cookieStore.put('refreshPageParam',$stateParams);
        }
        console.log($cookieStore.get('refreshPageParam').analysisProjectId);
        $scope.getProject();


    });

//metronic bug 无法初始化checkbox和radio的初始值样式，通过一下这句初始化。只通过双向绑定无法解决。
    $timeout(jQuery.uniform.update, 0);


})