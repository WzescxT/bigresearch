angular.module('MetronicApp').controller('SearchController', function($cookieStore,uibPaginationConfig,$stateParams,CollectProjectService,SearchService,$rootScope, $http, $state,$timeout,$scope ) {
    $scope.pageTitle="文本搜索模块";


    uibPaginationConfig.firstText=" < < ";
    uibPaginationConfig.lastText=" > > ";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={itemsPerPage:10,bigTotalItems:900,maxSize:8,bigCurrentPage:1};

    $scope.searchNews={

        "searchStr":"",
        "act":""

    };



    $scope.getSearchProjects = function () {
        CollectProjectService.getSearchProjects().then(function (resp) {
            if (resp.error_code==0){
                $scope.projects = resp.rt_info;
                console.log(resp);
            }else{
                swal("查询异常，请重试！");
            }
        })


    }





    var getColumns = function (columns) {
        $scope.columns =[];
        for (i in columns){
            $scope.columns.push( {name:columns[i],value:true});
        }
    }


    $scope.Search=function(){
        console.log("进入方法");
        $scope.queryParam={"searchStr":$scope.searchNews.searchStr,"act":$scope.searchNews.act,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        console.log($scope.queryParam);
        SearchService.Search($scope.queryParam).then(function(resp) {
            console.log("====>");
            console.log(resp);
        if (resp.error_code == 0) {
            $scope.searchProject=resp.rt_mapinfo.searchProject;

            getColumns(resp.rt_mapinfo.columns);


            console.log("columns");
            console.log($scope.columns);
            $scope.results=resp.rt_mapinfo.results;
            $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;
        } else {
            swal(resp.error_msg);
        }
    }, function(resp) {
    })

    }


    $scope.getNewPage=function(){
        $scope.Search();
    }

    $scope.$on('$viewContentLoaded', function() {
        //console.log("初始化方法");
        $scope.getSearchProjects();
        // $scope.Search();
        //console.log("初始化完毕");
    });
});
