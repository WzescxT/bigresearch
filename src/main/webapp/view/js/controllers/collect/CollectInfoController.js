angular.module('MetronicApp').controller('CollectInfoController', function($cookieStore,uibPaginationConfig,CollectProjectService,$stateParams,$rootScope, $scope, $http, $timeout) {


    $scope.pageTitle="数据展示";

    $scope.collectInfos={};

    //uibPaginationConfig={itemsPerPage:10,boundaryLinks:!1,directionLinks:!0,firstText:"第一页",previousText:"« ",nextText:" »",lastText:"最后一页",rotate:!0};
    //初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};






/*

//获取json中的列名
    var getColumnNames=function(fields){

        var res=[];
        for(var i in fields){

            res.push({'name':fields[i].name,'value':true});
        }
        console.log(res);
        return res;

    }
*/






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
                if (!bol&&key!="id"){

                    res.push({name:key,value:true});
                }


            }

        })

        return res;
    }



    $scope.getCollectInfo=function(){
        var queryParam={"projectId":$cookieStore.get('refreshPageParam').projectId,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        CollectProjectService.getCollectInfo(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.collectInfos=resp.rt_info.collectInfos;
                $scope.pageConfig.bigTotalItems=resp.rt_info.bigTotalItems;
                /*if($scope.pageConfig.bigCurrentPage==1){

                    $scope.columns = getColumnNames($scope.collectInfos);
                }*/
                $scope.columns = getColumnNames($scope.collectInfos);
                if(!$scope.collectInfos.length>0){
                    swal("暂时没有数据！", "请先采集！")
                }
            } else {
                sweetAlert("error", resp.error_msg, "error");

            }
        }, function(resp) {
        })

    }



    $scope.getProject = function(){
        var queryParam = {"projectId":$cookieStore.get('refreshPageParam').projectId};
        CollectProjectService.getProject(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.project = resp.rt_info.project;
                $scope.template = resp.rt_info.template;

            } else {


            }
        }, function(resp) {
        })
    }














    $scope.$on('$viewContentLoaded', function() {

        if($stateParams.projectId>0){
            //console.log("初始化方法")
            $cookieStore.put('refreshPageParam',$stateParams);

        }
        //console.log("初始化完毕");



        //$rootScope.refreshPageParam=$stateParams.projectId;
        $scope.getCollectInfo();

        $scope.getProject();





    });



});