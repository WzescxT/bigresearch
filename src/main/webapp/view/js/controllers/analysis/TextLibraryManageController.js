angular.module('MetronicApp').controller('TextLibraryManageController',function(TextLibraryService,uibPaginationConfig,$rootScope, $scope,$state,$modal) {
    $scope.pageTitle="文本库管理";



//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







    $scope.textLibraries={};



    $scope.name="";




    $scope.getTextLibraries =function(){
        $scope.queryParam={"name":$scope.name,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};

        TextLibraryService.getTextLibraries($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.textLibraries=resp.rt_info.textLibraries;
                $scope.pageConfig.bigTotalItems=resp.rt_info.bigTotalItems;
                $scope.thisMonthItems = resp.rt_info.thisMonthItems;
            } else {
                sweetAlert("error", resp.error_msg, "error");

            }
        }, function(resp) {
        })

    }


    $scope.importTextLibrary = function(id) {
        $state.go("textlibrary_import", {
            textLibraryId : id
        });
    }



    $scope.goDBView=function () {
        $state.go("analysis_db");
    }



    $scope.goCreateTextLibrary=function(){  //打开模态
        var createTextLibiaryModal = $modal.open({
            templateUrl : 'views/analysis/modal_createTextLibrary.html',  //指向上面创建的视图
            controller : 'ModalTemplateCtrl',// 初始化模态范围
            size : 'lg' ,//大小配置,
        })
        createTextLibiaryModal.result.then(function(resp){
            $scope.getTextLibraries();

        },function(){
        })

    }





    var getCurrentTextLibrary = function(id) {
        angular.forEach($scope.textLibraries, function(data){
            if(data.id==id){
                $scope.currentTextLibrary=data;
                return;
            }
        });

    }



    $scope.deleteText=function(id){
        $scope.queryParam={"textLibraryId":id};
        getCurrentTextLibrary(id);
        console.log($scope.queryParam);
        swal({
                title: "确定要删除文本库'"+$scope.currentTextLibrary.name+"'里面的文本吗?",
                text: "删除文本后将无法恢复!",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "删除",
                cancelButtonText:"取消",
                showLoaderOnConfirm: true,
                closeOnConfirm: false
            },
            function(){
                TextLibraryService.deleteText($scope.queryParam).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("已删除", resp.rt_msg, "success");
                        $scope.getTextLibraries();
                    } else {
                        sweetAlert("未删除", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });


    }



    $scope.deleteTextLibrary=function(id){
        $scope.queryParam={"textLibraryId":id};
        getCurrentTextLibrary(id);
        console.log($scope.queryParam);
        swal({
                title: "确定要删除文本库'"+$scope.currentTextLibrary.name+"'吗?",
                text: "删除文本库后将无法恢复!",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "删除",
                cancelButtonText:"取消",
                closeOnConfirm: false,
                showLoaderOnConfirm: true
            },
            function(){
                TextLibraryService.deleteTextLibrary($scope.queryParam).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("已删除", resp.rt_msg, "success");
                        $scope.getTextLibraries();
                    } else {
                        sweetAlert("未删除", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });


    }








    $scope.$on('$viewContentLoaded', function() {
        //console.log("初始化方法");
        $scope.getTextLibraries();
        //console.log("初始化完毕");
    });


});













/*TemplateModal 模态框控制器*/
MetronicApp.controller('ModalTemplateCtrl',function($scope,$modalInstance,TextLibraryService) { //依赖于modalInstance


    //模态创建文本库
    $scope.textLibrary={
        "name":"",
        "describle":""
    }





    $scope.createTextLibrary=function(){
        if($scope.textLibrary.name==""){
            swal("请先输入文本库名称");
            return;
        }
        if($scope.textLibrary.describle==""){
            swal("请先输入文本库描述信息");
            return;
        }



        TextLibraryService.createTextLibrary($scope.textLibrary).then(function(resp) {
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



















