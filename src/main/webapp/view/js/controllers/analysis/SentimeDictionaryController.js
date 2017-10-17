angular.module('MetronicApp').controller('SentimeDictionaryController',function(SentimeDictionaryService,uibPaginationConfig,$rootScope, $scope,$state,$modal) {
    $scope.pageTitle="情感词典";

//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







    $scope.sentimeWords={};




    $scope.name="";

    $scope.getSentimeDictionary =function(){
        var queryParam={"name":$scope.name,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        console.log(queryParam);
        SentimeDictionaryService.getUserDictionary(queryParam).then(function(resp) {
            console.log(resp);
            if (resp.error_code == 0) {
                $scope.dictionary=resp.rt_mapinfo.dictionary;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
                $scope.thisMonthItems=resp.rt_mapinfo.thisMonthItems;


            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }






    $scope.updateDictionaryName =function(id,name){
        swal({
                title: "修改情感词汇库‘"+name+"’的名称",
                text: "请输入新名称：",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                animation: "slide-from-top",
                inputPlaceholder: ""
            },
            function(inputValue){
                if (inputValue === false) return false;

                if (inputValue === "") {
                    swal.showInputError("输入不能为空!");
                    return false
                }

                if (inputValue === name) {
                    swal.showInputError("输入和之前名称相同!");
                    return false
                }

                var param={"id":id,'name':inputValue};

                SentimeDictionaryService.updateSentimeDictionary(param).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("成功", resp.rt_msg, "success");
                        $scope.getSentimeDictionary();
                    } else {
                        sweetAlert("error", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });


    }












    $scope.addDictionary =function(){
        swal({
                title: "添加情感词汇库",
                text: "请输入词汇库名称：",
                type: "input",
                showCancelButton: true,
                closeOnConfirm: false,
                animation: "slide-from-top",
                inputPlaceholder: ""
            },
            function(inputValue){
                if (inputValue === false) return false;

                if (inputValue === "") {
                    swal.showInputError("输入不能为空!");
                    return false
                }

                var param={'name':inputValue};

                SentimeDictionaryService.addSentimeDictionary(param).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("成功", resp.rt_msg, "success");
                        $scope.getSentimeDictionary();
                    } else {
                        sweetAlert("error", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });


    }

















    $scope.deleteSentimeDictionary=function(id){
        var param={"id":id};
        SentimeDictionaryService.deleteSentimeDictionary(param).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("已删除", resp.rt_msg, "success");
                        $scope.getSentimeDictionary();
                    } else {
                        sweetAlert("未删除", resp.error_msg, "error");
                    }
                })


    }









    $scope.showWords = function(dictionaryId,dictionaryName) {
        $state.go("analysis_sentime_word", {sentimeDictionaryId :dictionaryId,sentimeDictionaryName:dictionaryName});

    }











    $scope.$on('$viewContentLoaded', function() {
        $scope.getSentimeDictionary();
    });


});


















