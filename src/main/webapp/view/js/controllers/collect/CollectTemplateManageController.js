angular.module('MetronicApp').controller('CollectTemplateManageController',function(CollectCusTempService,uibPaginationConfig,$rootScope, $scope,$state,$modal,factory_passParam) {
    $scope.pageTitle="项目管理";



//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};




    $scope.collectTemplates={};

    $scope.name="";



    $scope.getUserTemplates=function(){
        $scope.queryParam={"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage,"name":$scope.name};
        CollectCusTempService.getUserTemplates($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.collectTemplates=resp.rt_info.templates;


                $scope.pageConfig.bigTotalItems=resp.rt_info.bigTotalItems;
                $scope.thisMonthItems=resp.rt_info.thisMonthItems;
            } else {
                alert(resp.error_msg);
            }
        }, function(resp) {
        })

    }




    var getCurrentTemplate = function(id) {

        angular.forEach($scope.collectTemplates, function(data){
            if(data.id==id){
                $scope.currentTemplate = angular.copy(data);


            }
        });

    }
    $scope.showDetail=function(id){
        getCurrentTemplate(id);
        //模态展示具体信息
        var templateDetailModal = $modal.open({
            templateUrl : 'views/collect/template_detail.html',  //指向上面创建的视图
            controller : 'ModalTemplateCtrl',// 初始化模态范围
            size : 'lg' ,//大小配置,
        })
        factory_passParam.setter($scope.currentTemplate);


    }













    $scope.goCreateTemplate=function(){
        $state.go("collect_listpage",1);

    }






    $scope.deleteTemplate=function(id){
        $scope.queryParam={"templateId":id};
        console.log($scope.queryParam);
        CollectCusTempService.deleteTemplate($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                swal("Deleted!", resp.rt_msg, "success");
                //当前页面只有一个模板，删除后查询为空
                if($scope.collectTemplates.length==1){
                    $scope.pageConfig.bigCurrentPage=$scope.pageConfig.bigCurrentPage-1;
                }
                $scope.getUserTemplates();
            } else {
                sweetAlert("异常", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }



    $scope.alertDelete=function(id) {
        getCurrentTemplate(id);
        swal({
                title: "确定要删除模板'"+$scope.currentTemplate.name+"'吗?",
                text: "删除模板后将无法恢复!",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "删除",
                cancelButtonText:"取消",
                showLoaderOnConfirm: true,
                closeOnConfirm: false
            },
            function(){
                $scope.deleteTemplate(id);


            });

    };









    $scope.$on('$viewContentLoaded', function() {
        $scope.getUserTemplates();
    })



});





/*TemplateModal*/
MetronicApp.controller('ModalTemplateCtrl',function($scope,$modalInstance,factory_passParam){ //依赖于modalInstance



    $scope.currentTemplate=factory_passParam.getter();








    $scope.currentTemplate.entryUrls=angular.fromJson($scope.currentTemplate.entryUrlsStr);
        $scope.currentTemplate.fields=angular.fromJson($scope.currentTemplate.fieldsStr);



    //初始化模板数据

    try {
        //删除不可视信息
        delete $scope.currentTemplate.entryUrlsStr;
        delete $scope.currentTemplate.fieldsStr;
        delete $scope.currentTemplate.createTime;
        delete $scope.currentTemplate.createUser;
        delete $scope.currentTemplate.id;

        //删除为空的信息
        for(var key in $scope.currentTemplate){
            if ($scope.currentTemplate[key]==null){
                delete $scope.currentTemplate[key];
            }
        }



    } catch (e) {
    }







        var content = angular.toJson($scope.currentTemplate);
        var result = '';
        if (content!='') {

                current_json = jsonlint.parse(content);
                current_json_str = JSON.stringify(current_json);
                //current_json = JSON.parse(content);
                result = new JSONFormat(content,4).toString();
            $scope.jsonResult=result;
        }else{
            $('#json-target').html('');
        }




    $scope.closeModal = function(){
        $modalInstance.dismiss('cancel'); // 退出
    }




    //解析json(等时间充裕改写成angularjs语法)

    $('.shown').click(function(){
        if (!shown_flag) {
            renderLine();
            $('#json-target').attr("style","padding:0px 50px;");
            $('#line-num').show();
            $('.numberedtextarea-line-numbers').show();
            shown_flag = true;
            $(this).attr('style','color:#15b374;');
        }else{
            $('#json-target').attr("style","padding:0px 20px;");
            $('#line-num').hide();
            $('.numberedtextarea-line-numbers').hide();
            shown_flag = false;
            $(this).attr('style','color:#999;');
        }
    });
    function renderLine(){
        var line_num = $('#json-target').height()/20;
        $('#line-num').html("");
        var line_num_html = "";
        for (var i = 1; i < line_num+1; i++) {
            line_num_html += "<div>"+i+"<div>";
        }
        $('#line-num').html(line_num_html);
    }


});
