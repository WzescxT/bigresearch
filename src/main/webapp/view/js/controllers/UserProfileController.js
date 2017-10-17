angular.module('MetronicApp').controller('UserProfileController', function(anchorScroll,$cookieStore,uibPaginationConfig,FileUploader,UserService,$rootScope, $scope, $state) {


    // set sidebar closed and body solid layout mode
    $rootScope.settings.layout.pageBodySolid = true;
    $rootScope.settings.layout.pageSidebarClosed = true;


    $scope.user = angular.copy($rootScope.AuthSetting.LoginUser);

    $scope.resetPassword = function(){
        $scope.password = {
            oldPassword:'',
            newPassword:'',
            rePassword:''
        }
    }
    $scope.resetBasic = function(){
        $scope.user = angular.copy($rootScope.AuthSetting.LoginUser);
    }



    $scope.updateBasic = function(){
        var param = $scope.user;
        UserService.updateBasic(param).then(function (resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");
                $scope.user = resp.rt_info;
                $rootScope.AuthSetting.LoginUser = $scope.user;

            } else {
                swal("error", resp.error_msg, "error");
            }
        }, function (resp) {

        })


    }

    $scope.password = {
        oldPassword:'',
        newPassword:'',
        rePassword:''
    }

    $scope.updatePassword = function(){
        if($scope.password.oldPassword==''){
            swal("旧密码不能为空!");
            return;
        }
        if($scope.password.newPassword==''){
            swal("新密码不能为空!");
            return;
        }else{
            if($scope.password.rePassword!=$scope.password.newPassword){
                swal("两次新密码输入不一致！");
                return;
            }
        }
        if($scope.password.rePassword==''){
            swal("必须再次输入新密码！");
            return;
        }
        var param = $scope.password;
        console.log(param);
        UserService.updatePassword(param).then(function (resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");
                $scope.user = resp.rt_info;
                $rootScope.AuthSetting.LoginUser = $scope.user;

            } else {
                swal("error", resp.error_msg, "error");
            }
        }, function (resp) {

        })


    }








//上传文件

    var uploader = $scope.uploader = new FileUploader({
        formData : [ {
            'token' : $cookieStore.get('token')
        } ],
        url : $rootScope.ServerUrl + 'uploadAvatr'

    });



    // FILTERS

    uploader.filters
        .push({
            name : 'customFilter',
            fn : function(item /* {File|FileLikeObject} */,
                          options) {
                return this.queue.length >=0;
            }
        });

    // CALLBACKS

    uploader.onWhenAddingFileFailed = function(
        item /* {File|FileLikeObject} */, filter, options) {
    };

    uploader.onSuccessItem = function(fileItem, response,
                                      status, headers) {
            swal("完成", '上传成功', "success");
        }

    uploader.onErrorItem = function(fileItem, response, status,
                                    headers) {
        swal("失败","头像上传失败，请重试！","error");

    };














//用户管理  dashboard界面





//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":1,"bigCurrentPage":1};







    $scope.users=[];


    $scope.condition={name:""};




    $scope.getUserList=function(){
        var queryParam={"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage,"name":$scope.condition.name};
        UserService.getUserList(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                console.log(resp);
                $scope.users=resp.rt_mapinfo.users;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
                $scope.thisMonthItems = resp.rt_mapinfo.thisMonthItems;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }


    $scope.delete=function(id){

        var queryParam={"id":id};

        getSelectedUser(id);

        swal({
                title: "确定要删除账号'"+$scope.selectedUser.username+"'吗?",
                text: "删除账号后将无法恢复!",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "删除",
                cancelButtonText:"取消",
                showLoaderOnConfirm: true,
                closeOnConfirm: false
            },
            function(){
                UserService.deleteUser(queryParam).then(function(resp) {
                    if (resp.error_code == 0) {
                        swal("成功","删除用户成功","success");
                        $scope.getUserList();
                    } else {
                        sweetAlert("error", resp.error_msg, "error");
                    }
                }, function(resp) {
                })


            });

    }






    var getSelectedUser = function(id) {
        angular.forEach($scope.users, function(data){
            if(data.id==id){
                $scope.selectedUser=data;
                return;
            }
        });

    }


    $scope.tempUser={
        username:'',
        password:'monetware',
        gender:"male",
        endTime:new Date(),
        tel:'',
        email:'',
        comments:''
    }


    $scope.user_create=false;

    $scope.goCreateUser = function(){
        $scope.user_create=true;
        anchorScroll.toView('#user_create', true);
    }
    $scope.cancelCreateUser = function () {
        $scope.user_create=false;
    }

    $scope.createUser = function(){
        var queryParam = $scope.tempUser;

        console.log(queryParam);
        UserService.createUser(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                swal("成功","用户创建成功","success");
                $scope.user_create=false;
                $scope.tempUser={
                    username:'',
                    password:'monetware',
                    gender:"male",
                    endTime:new Date(),
                    tel:'',
                    email:'',
                    comments:''
                },
                $scope.getUserList();
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })
    }




















    $scope.$on('$viewContentLoaded', function() {
        $scope.getUserList();
    });




}); 
