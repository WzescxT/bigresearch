angular.module('MetronicApp').controller('AnalysisClusteInfoController', function($cookieStore,uibPaginationConfig,ClusteService,$stateParams,$rootScope, $scope, $http, $timeout) {


    $scope.pageTitle="数据展示";

    $scope.clusteInfos={};

    uibPaginationConfig.firstText="< <";
    uibPaginationConfig.lastText="> >";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







//获取json中的列名
    var getColumnNames=function(jsonObjs){
        var res=[];
        angular.forEach(jsonObjs, function(obj,index,array){
            if (obj==null){
                return false;
            }
            for(var key in obj){
                var bol = false;
                angular.forEach(res, function(obj,index,array){
                    if(obj.name==key){
                        //已经有这一列
                        bol = true;
                        return true;
                    }

                })
                if (!bol){

                    res.push({name:key,value:true});
                }


            }

        })

        return res;
    }






    $scope.getClusteInfo=function(){
        $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId,"clusteId":$cookieStore.get('refreshPageParam').clusteId,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        ClusteService.getClusteInfo($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.clusteInfos=resp.rt_mapinfo.analysisProjectClusteInfo;

                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;

                $scope.columns=getColumnNames($scope.clusteInfos)
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
        if($stateParams.analysisProjectId>0&&$stateParams.clusteId>0){
            //console.log("初始化方法")
            $cookieStore.put('refreshPageParam',$stateParams);
            console.log("初始化====>"+$stateParams);
        }
        //console.log("初始化完毕");



        //$rootScope.refreshPageParam=$stateParams.projectId;
        $scope.getClusteInfo();







    });



});