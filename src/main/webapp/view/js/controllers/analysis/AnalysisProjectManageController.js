angular.module('MetronicApp').controller('AnalysisProjectManageController',function(AnalysisProjectService,TextLibraryService,uibPaginationConfig,$rootScope, $scope,$state,$modal) {
    $scope.pageTitle="文本库管理";



//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};




    function getTextlibraryById(textlibraryId,data){
        var res="";
        var queryParam = {"textLibraryId":textlibraryId};
        TextLibraryService.getTextlibrary(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                res = resp.rt_info.name;
            }else{
                res =  "*文本库失效*";
            }
            data.textlibraryName = res;
        }, function(resp) {
        })


    }



    $scope.analysisProjects={};



    $scope.name="";

    $scope.getAnalysisProjects =function(){
        var queryParam={"name":$scope.name,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        console.log(queryParam);
        AnalysisProjectService.getUserProjects(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.analysisProjects=resp.rt_info.analysisProjects;

                $scope.pageConfig.bigTotalItems=resp.rt_info.bigTotalItems;
                $scope.thisMonthItems = resp.rt_info.thisMonthItems;


                angular.forEach($scope.analysisProjects,function (data,index) {

                    getTextlibraryById(data.textlibraryId,data);


                });




            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }







    $scope.goCreateProject=function(){  //打开模态
        var createProjectModal = $modal.open({
            templateUrl : 'views/analysis/modal_createAnalysisProject.html',  //指向上面创建的视图
            controller : 'ModalTemplateCtrl',// 初始化模态范围
            size : 'lg' ,//大小配置,
        })
        createProjectModal.result.then(function(resp){
            $scope.getAnalysisProjects();

        },function(){
        })

    }





    var getCurrentProject = function(id) {
        angular.forEach($scope.analysisProjects, function(data){
            if(data.id==id){
                $scope.currentProject=data;
                return;
            }
        });

    }



    $scope.deleteProject=function(id){
        $scope.queryParam={"analysisProjectId":id};
        getCurrentProject(id);
        console.log($scope.queryParam);
        swal({
                title: "确定要删除分析项目'"+$scope.currentProject.name+"'吗?",
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
                AnalysisProjectService.deleteProject($scope.queryParam).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("已删除", resp.rt_msg, "success");
                        $scope.getAnalysisProjects();
                    } else {
                        sweetAlert("未删除", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });


    }






//跳转到词频统计
    $scope.goWordFrequence = function(id) {
        $state.go("analysis_wordfrequency", {
            analysisProjectId : id
        });
    }

    //跳转到聚类分析
    $scope.goCluste = function(id) {
        $state.go("analysis_cluste", {
            analysisProjectId : id
        });
    }

    //跳转到分类分析
    $scope.goClassify = function(id) {
        $state.go("analysis_classify", {
            analysisProjectId : id
        });
    }


    //跳转到情感分析
    $scope.goSentime = function(id) {
        $state.go("analysis_sentime", {
            analysisProjectId : id
        });
    }










    $scope.$on('$viewContentLoaded', function() {
        //console.log("初始化方法");
        $scope.getAnalysisProjects();
        //console.log("初始化完毕");
    });


});













/*TemplateModal 模态框控制器*/
MetronicApp.controller('ModalTemplateCtrl',function($scope,$modalInstance,AnalysisProjectService,TextLibraryService) { //依赖于modalInstance
    TextLibraryService.getUserAllTextLibraries().then(function(resp) {
        if (resp.error_code == 0) {
            $scope.textLibraries=resp.rt_info;
        } else {
            sweetAlert("error", resp.error_msg, "error");
            $modalInstance.dismiss('cancle'); // 退出
        }
    }, function(resp) {
    })


    //模态创建文本库
    $scope.analysisProject={
        "name":"",
        "describle":"",
        "textlibraryId":0
    }




    $scope.createAnalysisProject=function(){

        if($scope.analysisProject.name==""){
            swal("请先输入项目名称！");
            return;
        }
        if($scope.analysisProject.describle==""){
            swal("请先输入项目描述！");
            return;
        }
        if($scope.analysisProject.textlibraryId==""||$scope.analysisProject.textlibraryId<1){
            swal("请先选择文本库！");
            return;
        }

        AnalysisProjectService.createProject($scope.analysisProject).then(function(resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");
                $modalInstance.close(resp);// 退出
            } else {
                sweetAlert("error", resp.error_msg, "error");
                $modalInstance.dismiss('cancle'); // 退出
            }
        }, function(resp) {
        })
    }


    $scope.cancel=function(){
        $modalInstance.dismiss('cancle'); // 退出


    }


})



















