angular.module('MetronicApp').controller('AnalysisClassifyInfoController', function($cookieStore,uibPaginationConfig,ClassifyService,$stateParams,$rootScope, $scope) {


    $scope.pageTitle="数据展示";

    $scope.classifyInfos={};

    //uibPaginationConfig={itemsPerPage:10,boundaryLinks:!1,directionLinks:!0,firstText:"第一页",previousText:"« ",nextText:" »",lastText:"最后一页",rotate:!0};
    //初始化uibpaginationConfig，汉化
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



    $scope.getClassifyInfo=function(){
        $scope.categoryName = $cookieStore.get('refreshPageParam').categoryName;
        console.log("进入方法");
        $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId,"categoryName":$cookieStore.get('refreshPageParam').categoryName,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        console.log($scope.queryParam);
        ClassifyService.getClassifyInfo($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.classifyInfos=resp.rt_mapinfo.classifyInfos;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
                $scope.columns=getColumnNames($scope.classifyInfos)
            } else {
                sweetAlert("error", resp.error_msg, "error");

            }
        }, function(resp) {
        })

    }











    $scope.getNewPage=function(){
        $scope.getClassifyInfo();
    }





    $scope.$on('$viewContentLoaded', function() {
        if($stateParams.analysisProjectId>0&&$stateParams.categoryName!=null){
            $cookieStore.put('refreshPageParam',$stateParams);
        }
        $scope.getClassifyInfo();







    });



});