angular.module('MetronicApp').controller('AnalysisSentimeController',function($cookieStore,$stateParams,AnalysisProjectService,SentimeService,uibPaginationConfig,$rootScope, $scope,$state,$modal,$timeout) {
    $scope.pageTitle = "情感分析";

    $scope.sentimeFields={};

    $scope.startSentime=function(){
        $scope.analysisProject.sentimeStatus='正在分析';



        var queryParam={"id":$cookieStore.get('refreshPageParam').analysisProjectId,"sentimeWords":$scope.dictionaryId,"sentimeFields":angular.toJson($scope.sentimeFields)};
        SentimeService.startSentime(queryParam).then(function(resp) {
                if (resp.error_code == 0) {
                    $scope.getProject();
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
                    if($scope.analysisProject.sentimeStatus==0){
                        $scope.analysisProject.sentimeStatus='尚未分析';
                    }else if($scope.analysisProject.sentimeStatus==1){
                        $scope.analysisProject.sentimeStatus='正在分析';
                    }else{
                        $scope.analysisProject.sentimeStatus='分析结束';
                        $scope.getSentimeResult();
                    }
                    $timeout(jQuery.uniform.update, 0);


                    $scope.sentimeFields=angular.fromJson($scope.analysisProject.sentimeFields);
                    $scope.dictionaryId=$scope.analysisProject.sentimeWords;
                    console.log($scope.dictionaryId)
                    getDictionaries();
                } else {
                    sweetAlert("error", '异常错误', "error");
                }
            }, function(resp) {
            })


    }






    var getDictionaries = function(){
        SentimeService.getSentimeDictionaries().then(function(resp) {
            if (resp.error_code == 0) {
                $scope.dictionaries=resp.rt_info;
                console.log( $scope.dictionaries);
            } else {
                sweetAlert("error", resp.error_msg, "error");
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







    $scope.sentimeResult={};



    $scope.getSentimeResult =function(){

        $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};

        SentimeService.getSentimeResult($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.sentimeResult=resp.rt_mapinfo.sentimeResults;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }


$scope.showAll= function (id,text) {
    if(id==$scope.id){
        $scope.leaveText = "";
        $scope.id = 0;
    }else{
        $scope.leaveText = text.substring(100,text.length);
        $scope.id=id;
    }
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