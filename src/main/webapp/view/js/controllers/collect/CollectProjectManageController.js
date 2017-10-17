angular.module('MetronicApp').controller('CollectProjectManageController',function(CollectProjectService,CollectCusTempService,uibPaginationConfig,$rootScope, $scope,$state) {
    $scope.pageTitle="项目管理";



//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







    $scope.collectProjects={};

    $scope.name="";



    $scope.getUserProjects=function(){
        $scope.queryParam={"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage,"name":$scope.name};

        CollectProjectService.getUserProjects($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.collectProjects=resp.rt_info.projects;

                $scope.pageConfig.bigTotalItems=resp.rt_info.bigTotalItems;
                $scope.thisMonthItems = resp.rt_info.thisMonthItems;

                angular.forEach($scope.collectProjects,function (data,index) {

                    getTemplateById(data.templateId,data);


                });


            } else {
                sweetAlert("error", resp.error_msg, "error");

            }
        }, function(resp) {
        })

    }


    // function f1(attr)

    function getTemplateById(templateId,data){
        var res="";
        var queryParam = {"templateId":templateId};
        CollectCusTempService.getTemplateById(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                res = resp.rt_info.name;
            }else{
                res =  "*模板失效";
            }
            data.templateName = res;
        }, function(resp) {
        })


    }




    $scope.showInfo = function(id) {
        $state.go("showCollectInfo", {
            projectId : id
        });
    }
    $scope.showDetail = function(id) {
        $state.go("collectproject_control", {
            projectId : id
        });
    }





    $scope.goCreateProject=function(){
        $state.go("collectproject_create");
    }






    var getCurrentTemplate = function(id) {
        angular.forEach($scope.collectProjects, function(data){
            if(data.id==id){
                $scope.currentProject=data;
                return;
            }
        });

    }



    $scope.deleteProject=function(id){
        $scope.queryParam={"projectId":id};
        getCurrentTemplate(id);
        swal({
                title: "确定要删除项目'"+$scope.currentProject.name+"'吗?",
                text: "删除项目后将无法恢复!",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "删除",
                cancelButtonText:"取消",
                showLoaderOnConfirm: true,
                closeOnConfirm: false
            },
            function(){
                CollectProjectService.deleteProject($scope.queryParam).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("已删除", resp.rt_msg, "success");
                        //当前页面只有一个项目，返回上页
                        if($scope.collectProjects.length==1){
                            $scope.pageConfig.bigCurrentPage=$scope.pageConfig.bigCurrentPage-1;
                        }
                        $scope.getUserProjects();
                    } else {
                        sweetAlert("未删除", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });


    }








    $scope.alertCollect=function(id) {
            swal({
                    title: "开始采集",
                    text: "请确认进行采集操作!",
                    type: "info",
                    showCancelButton: true,
                    confirmButtonText: "开始采集",
                    cancelButtonText: "暂不采集!",
                    closeOnConfirm: false,
                    closeOnCancel: false
                },
                function(isConfirm){
                    if (isConfirm) {
                        $scope.startCollect(id);
                        swal("执行", "正在采集中。。。", "success");
                    } else {
                        swal("取消", "暂时不执行采集", "error");
                    }
                });

    };






    $scope.startCollect=function(id){
        var queryParam={"id":id};

        angular.forEach($scope.collectProjects,function (data) {
            if(data.id==id){
                data.status="正在采集";
            }
        });
        sweetAlert("开始采集，请稍候！");
        CollectProjectService.startCollect(queryParam).then(function(resp) {

            if (resp.error_code == 0) {
                sweetAlert("success!", resp.rt_msg, "success");
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
            $scope.getUserProjects();
        }, function(resp) {
        })

    }








    $scope.deleteInfo=function(id){
        $scope.queryParam={"projectId":id};


        getCurrentTemplate(id);
        swal({
                title: "确定要删除项目'"+$scope.currentProject.name+"'的采集数据吗?",
                text: "删除该项目的采集数据后将无法恢复!",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "删除",
                cancelButtonText:"取消",
                closeOnConfirm: false
            },
            function(){


                CollectProjectService.deleteCollectInfo($scope.queryParam).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("Deleted!", resp.rt_msg, "success");
                        $scope.getUserProjects();

                    } else {
                        sweetAlert("error", resp.error_msg, "error");
                    }
                }, function(resp) {
                })



            });






    }




//导入搜索库


    $scope.importColumn=function(id){
        $scope.queryPara={"id":id};
        CollectProjectService.importColumn($scope.queryPara).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.tableKey = resp.rt_info.tableKey;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }


    $scope.ImportSearchInfo=function(){
        $scope.queryParam={"id":$scope.queryPara ,"tableKey":$scope.tableKey};
        CollectProjectService.ImportSearchInfo($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }







    
    
    
    
    
    //新做solr
    $scope.importSolr = function (id) {
        var param = {"id":id};
        CollectProjectService.importSolr(param).then(function(resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })



    }







    $scope.$on('$viewContentLoaded', function() {
        $scope.getUserProjects();
    });


});