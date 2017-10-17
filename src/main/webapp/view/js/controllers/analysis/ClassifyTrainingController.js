angular.module('MetronicApp').controller('ClassifyTrainingController',function(ClassifyTrainingService,uibPaginationConfig,$rootScope, $scope,$state,$modal) {
    $scope.pageTitle="分类训练集";



//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" > > ";
    uibPaginationConfig.lastText=" < < ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







    $scope.classifyTrainings={};




    $scope.name="";
    $scope.getClassifyTraining =function(){
        var queryParam={"name":$scope.name,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        ClassifyTrainingService.getClassifyTraining(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.classifyTrainings=resp.rt_mapinfo.analysisProjectClassifyTraining;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
                $scope.thisMonthItems = resp.rt_mapinfo.thisMonthItems

            }
        }, function(resp) {
        })

    }



    $scope.updateCategoryName =function(categoryName){
        swal({
                title: "修改分类 ‘"+categoryName+"’ 名称",
                text: "请输入新分类的名称：",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                animation: "slide-from-top",
                inputPlaceholder: "新分类名称"
            },
            function(inputValue){
                if (inputValue === false) return false;

                if (inputValue === "") {
                    swal.showInputError("You need to write something!");
                    return false
                }

                $scope.queryParam={"oldCategoryName":categoryName,"newCategoryName":inputValue};

                ClassifyTrainingService.updateCategoryName($scope.queryParam).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("成功", resp.rt_msg, "success");
                        $scope.getClassifyTraining();
                    } else {
                        sweetAlert("error", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });


    }




















    $scope.addTrainingText=function(categoryName){  //打开模态
        console.log(categoryName);
        var addTrainingTextModal = $modal.open({
            templateUrl : 'views/analysis/modal_addTrainingText.html',  //指向上面创建的视图
            controller : 'ModalTemplateCtrl',// 初始化模态范围
            size : 'lg' ,//大小配置,
            resolve:{categoryName:function() { return angular.copy(categoryName); }}//传参
        })
        addTrainingTextModal.result.then(function(resp){
            $scope.getClassifyTraining();

        },function(){
        })

    }


















    $scope.delTrainingCategory=function(categoryName){
        $scope.param={"categoryName":categoryName};
        ClassifyTrainingService.delTrainingCategory($scope.param).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("已删除", resp.rt_msg, "success");
                        $scope.getClassifyTraining();
                    } else {
                        sweetAlert("未删除", resp.error_msg, "error");
                    }
                })


    }



    $scope.showText = function(categoryName) {
        $state.go("analysis_training_text", {categoryName :categoryName});

    }









    $scope.addTrainingCategory =function(categoryName){
        swal({
                title: "添加分类",
                text: "请输入分类名称：",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                animation: "slide-from-top",
                inputPlaceholder: "分类名称"
            },
            function(inputValue){
                if (inputValue === false) return false;

                if (inputValue === "") {
                    swal.showInputError("需要输入名称！");
                    return false
                }
                var param = {'categoryName':inputValue,'text':''};
                ClassifyTrainingService.addCategory(param).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("success!", resp.rt_msg, "success");
                        $scope.getClassifyTraining();
                    } else {
                        sweetAlert("error", resp.error_msg, "error");

                    }
                }, function(resp) {
                })


            });


    }
















    $scope.$on('$viewContentLoaded', function() {
        //console.log("初始化方法");
        $scope.getClassifyTraining();
        //console.log("初始化完毕");
    });


});














/*TemplateModal 模态框控制器*/
MetronicApp.controller('ModalTemplateCtrl',function($scope,$modalInstance,ClassifyTrainingService,categoryName) { //依赖于modalInstance


    //获取参数
    $scope.categoryName = categoryName;
    $scope.training={'categoryName':$scope.categoryName,'text':''};
    $scope.add=function(){
        if($scope.training.text==''){
            swal("请先输入文本内容！");
            return ;
        }
        ClassifyTrainingService.addClassifyTraining($scope.training).then(function(resp) {
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



















