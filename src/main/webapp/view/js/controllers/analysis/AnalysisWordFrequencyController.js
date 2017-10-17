angular.module('MetronicApp').controller('AnalysisWordFrequencyController',function($cookieStore,$stateParams,AnalysisProjectService,WordFrequencyService,uibPaginationConfig,$rootScope, $scope,$state,$modal,$timeout) {
    $scope.pageTitle = "词频统计";


    $scope.segmentColumn={};


    $scope.wordSegment=function(){
        console.log($scope.segmentColumn);
        $scope.analysisProject.segmentStatus='正在分词';
            $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId,"segmentType":$scope.analysisProject.segmentType,"segmentColumn":$scope.segmentColumn};
            AnalysisProjectService.wordSegment($scope.queryParam).then(function(resp) {
                if (resp.error_code == 0) {
                    $scope.getProject();
                    $scope.getWordsFrequency();
                    sweetAlert("success", resp.rt_msg, "success");
                } else {
                    sweetAlert("error", resp.error_msg, "error");
                }
            }, function(resp) {
            })
    }



    $scope.getProject=function(){
            $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId};
            AnalysisProjectService.getProject($scope.queryParam).then(function(resp) {
                if (resp.error_code == 0) {
                    $scope.analysisProject=resp.rt_info;
                    if($scope.analysisProject.segmentStatus==0){
                        $scope.analysisProject.segmentStatus='尚未分词';
                    }else if($scope.analysisProject.segmentStatus==1){
                        $scope.analysisProject.segmentStatus='正在分词';
                    }else{
                        $scope.analysisProject.segmentStatus='分词结束';
                        $scope.getWordsFrequency();
                    }

                    $timeout(jQuery.uniform.update, 0);



                    $scope.segmentColumn=angular.fromJson($scope.analysisProject.segmentColumn);
                    console.log($scope.segmentColumn);

                } else {
                    sweetAlert("error", "异常错误，请重试！", "error");
                }
            }, function(resp) {
            })


    }







//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":1,"maxSize":8,"bigCurrentPage":1};







    $scope.wordsFrequency={};



    $scope.getWordsFrequency =function(){

        $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId,"natures":$scope.nature,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};

        WordFrequencyService.getWordsFrequency($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.wordsFrequency=resp.rt_mapinfo.WordsFrequency;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }









//默认显示所有词性
$scope.nature={n:true,t:false,s:false,f:false,v:true,a:true,b:false,z:false,r:false,m:false,q:false,d:true,p:false,c:false,u:false,e:false,y:false,o:false,h:false,k:false,x:false,w:false};

//反选
$scope.invertSelection=function(){
    var selection=$scope.nature;
    for(var i in selection) {//不使用过滤
        if(selection[i]==true){
            $scope.nature[i]=false;
        }else{
            $scope.nature[i]=true;
        }
    }
    $timeout(jQuery.uniform.update, 0);
}




    //post导出
    $scope.export =function(){

        $scope.queryParam={"analysisProjectId":$cookieStore.get('refreshPageParam').analysisProjectId,"natures":$scope.nature,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};

        WordFrequencyService.exportExcel($scope.queryParam,$scope.analysisProject.name+"_词频统计.xls");

    }



















    $scope.$on('$viewContentLoaded', function() {
        if($stateParams.analysisProjectId>0){
            //console.log("初始化方法")
            $cookieStore.put('refreshPageParam',$stateParams);
        }
        $scope.getProject();


    });

//metronic bug 无法初始化checkbox和radio的初始值样式，通过一下这句初始化。只通过双向绑定无法解决。
    $timeout(jQuery.uniform.update, 0);


})