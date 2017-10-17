angular.module('MetronicApp').controller('TextLibraryImportController', function(TextLibraryService,$rootScope,FileUploader, $scope,$stateParams,$cookieStore) {

    $scope.pageTitle="文本库管理";


    $scope.$on('$viewContentLoaded', function() {
        console.log($stateParams.textLibraryId);
        if($stateParams.textLibraryId>0){
            //console.log("初始化方法")
            $cookieStore.put('refreshPageParam',$stateParams);

        }
        //console.log("初始化完毕");



        //$rootScope.refreshPageParam=$stateParams.projectId;
        $scope.textLibraryId=$cookieStore.get('refreshPageParam').textLibraryId;

        console.log( $scope.textLibraryId);




    });




    $scope.textlibrary={
        "id":$scope.textLibraryId,
        "fields":"*"
    }






//上传文件

    var uploader = $scope.uploader = new FileUploader({
        url : $rootScope.ServerUrl + 'analysis/uploadTextLibrary',
        formData : [ {
            'textLibraryId' : $cookieStore.get('refreshPageParam').textLibraryId,
            'columnNames':$scope.textlibrary.fields
        } ]

    });




    $scope.updateUploader=function(){
        uploader.formData = [ {
                'textLibraryId' : $cookieStore.get('refreshPageParam').textLibraryId,
                'fileType':$scope.textlibrary.type,
                'columnNames':$scope.textlibrary.fields
            } ]





    }
    $scope.importSuccess=true;
    $scope.fileStatus = new Array();
    var i = 0;

    // FILTERS

    uploader.filters
        .push({
            name : 'customFilter',
            fn : function(item /* {File|FileLikeObject} */,
                          options) {
                return this.queue.length < 10;
            }
        });

    // CALLBACKS

    uploader.onWhenAddingFileFailed = function(
        item /* {File|FileLikeObject} */, filter, options) {
        console.info('onWhenAddingFileFailed', item, filter,
            options);
    };
    uploader.onAfterAddingFile = function(fileItem) {
        console.info('onAfterAddingFile', fileItem);
        console.log(uploader.formData);
    };
    uploader.onAfterAddingAll = function(addedFileItems) {
        console.info('onAfterAddingAll', addedFileItems);
    };
    uploader.onBeforeUploadItem = function(item) {

        console.info('onBeforeUploadItem', item);
    };
    uploader.onProgressItem = function(fileItem, progress) {
        console.info('onProgressItem', fileItem, progress);
    };
    uploader.onProgressAll = function(progress) {
        console.info('onProgressAll', progress);
    };
    uploader.onSuccessItem = function(fileItem, response,
                                      status, headers) {
        console.info('onSuccessItem', fileItem, response,
            status, headers);
        $scope.fileStatus[i] = (i+1)+".文件" + fileItem._file.name
            + "上传成功";
        i++;
        console.log($scope.fileStatus);
    };
    uploader.onErrorItem = function(fileItem, response, status,
                                    headers) {
        console.info('onErrorItem', fileItem, response, status,
            headers);

        $scope.importSuccess=false;


        $scope.fileStatus[i] = (i+1)+".文件" + fileItem._file.name
            + "上传失败";
        i++;
    };
    uploader.onCancelItem = function(fileItem, response,
                                     status, headers) {
        console.info('onCancelItem', fileItem, response,
            status, headers);
    };
    uploader.onCompleteItem = function(fileItem, response,
                                       status, headers) {
        console.info('onCompleteItem', fileItem, response,
            status, headers);

    };
    uploader.onCompleteAll = function() {
        console.info('onCompleteAll');

        if($scope.importSuccess==true){
            swal("完成", '上传成功', "success");
        }else{
            swal("错误", '上传失败', "error");
        }


    };

    console.info('uploader', uploader);




});
