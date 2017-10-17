angular.module('MetronicApp').controller('SentimeWordController',function(SentimeWordService,factory_passParam,$stateParams,$cookieStore,uibPaginationConfig,$rootScope, $scope,$state,$modal) {
    $scope.pageTitle="情感词汇";


//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=20;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};












    $scope.sentimeWords={};




    $scope.name="";

    $scope.getSentimeWords =function(){
        var queryParam={"dictionaryId":$cookieStore.get('refreshPageParam').sentimeDictionaryId,"name":$scope.name,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        SentimeWordService.getSentimeWords(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.sentimeWords=resp.rt_mapinfo.words;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }






    $scope.updateWordScore =function(id,word){
        swal({
                title: "修改词汇 ‘"+word+"’ 分数",
                text: "请输入词汇分数：",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                animation: "slide-from-top",
                inputPlaceholder: "词汇分数"
            },
            function(inputValue){
                if (inputValue === false) return false;

                if (inputValue === "") {
                    swal.showInputError("输入不能为空!");
                    return false
                }

                $scope.param={"id":id,'score':inputValue};

                SentimeWordService.updateWord($scope.param).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("成功", resp.rt_msg, "success");
                        $scope.getSentimeWords();
                    } else {
                        sweetAlert("error", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });


    }






















    $scope.addWord=function(){  //打开模态
        factory_passParam.setter({"dictionaryId":$cookieStore.get('refreshPageParam').sentimeDictionaryId});
        var addWordModal = $modal.open({
            templateUrl : 'views/analysis/modal_addSentimeWord.html',  //指向上面创建的视图
            controller : 'ModalTemplateCtrl',// 初始化模态范围
            size : 'md' //大小配置,
        })
        addWordModal.result.then(function(resp){
            $scope.getSentimeWords();

        },function(){
        })

    }



















    $scope.deleteWord=function(id){
        var param={"id":id};
        SentimeWordService.deleteWord(param).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("已删除", resp.rt_msg, "success");
                        $scope.getSentimeWords();
                    } else {
                        sweetAlert("未删除", resp.error_msg, "error");
                    }
                })


    }








    $scope.$on('$viewContentLoaded', function() {
        if($stateParams.sentimeDictionaryId>0){
            //console.log("初始化方法")
            $cookieStore.put('refreshPageParam',$stateParams);
        }
        $scope.getSentimeWords();
        $scope.dictionaryName = $cookieStore.get('refreshPageParam').sentimeDictionaryName;
        //console.log("初始化完毕");
    });



});













/*TemplateModal 模态框控制器*/
MetronicApp.controller('ModalTemplateCtrl',function($scope,factory_passParam,$modalInstance,SentimeWordService) { //依赖于modalInstance

    $scope.term={'dictionaryId':factory_passParam.getter().dictionaryId,'word':'','score':''};
    $scope.add=function(){
        console.log($scope.term);
        SentimeWordService.insertWord($scope.term).then(function(resp) {
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



















