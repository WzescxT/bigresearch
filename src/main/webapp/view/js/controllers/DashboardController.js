angular.module('MetronicApp').controller('DashboardController', function($rootScope, $scope,  $modal,anchorScroll) {
    $scope.$on('$viewContentLoaded', function() {   
        // initialize core components
        App.initAjax();
    });

    // set sidebar closed and body solid layout mode
    $rootScope.settings.layout.pageContentWhite = true;
    $rootScope.settings.layout.pageBodySolid = false;
    $rootScope.settings.layout.pageSidebarClosed = false;




    $scope.myInterval = 3000;
    var slides = $scope.slides = [
        {
            image: '../assets/layouts/layout3/img/index_1.jpg',
            label:'数据时代',
            text: '世界由数据构成'
        },
        {
            image: '../assets/layouts/layout3/img/index_2.jpg',
            label:'采集',
            text: '数据获取与管理'
        },
        {
            image: '../assets/layouts/layout3/img/index_3.jpg',
            label:'搜索',
            text: '海量数据检索'
        },
        {
            image: '../assets/layouts/layout3/img/index_4.jpg',
            label:'分析',
            text: '高效可视化分析方案'
        }



    ];



















    $rootScope.toTop = function(){
        anchorScroll.toView('#top', true);
    }















});