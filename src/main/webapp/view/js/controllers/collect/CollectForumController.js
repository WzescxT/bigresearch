angular.module('MetronicApp').controller('CollectForumController', function($rootScope, $scope, CollectCusTempService) {

    $scope.pageTitle="博客论坛";





//天涯论坛采集配置

    $scope.searchContent = "";


    //初始化选择 新浪博客
    $scope.collect_forum={type:1};

//录入配置
    $scope.collectConfig={
        name:'',
        description:'',
        type:'',
        searchContent:''
    }








//采集模板
    var tempTianya=
        {
            "name":$scope.collectConfig.name,
            "description":$scope.collectConfig.description,
            "type":"user-listpage",
            "interval":0,
            "entryUrls":[
                // "http://search.tianya.cn/bbs?q="+$scope.collectConfig.searchContent+"&pn=1"
            ],
            "nextUrlXpath":"",
            "contentUrlXpath":"//li//div//h3//a/@href",
            "listUrlRegex":".*search.tianya.cn/bbs.*",
            "contentUrlRegex":".*html",
            "fields":[{"name":"title","contentXpath":"//h1[@class='atl-title']/allText()","require":true},{"name":"author","contentXpath":"//div[@class\u003d\u0027atl-info\u0027]//span[1]/a/text()","require":false},{"name":"publishTime","contentXpath":"//div[@class\u003d\u0027atl-info\u0027]//span[2]/substring(text(),4)","require":false},{"name":"content","contentXpath":"//div[@class='atl-main']/allText()","require":true}]
        }

    var tempSinaBlog=
        {
            "name":$scope.collectConfig.name,
            "description":$scope.collectConfig.description,
            "type":"user-listpage",
            "interval":3000,
            // "entryUrls":["http://search.sina.com.cn/?by=all&ie=utf-8&q="+$scope.collectConfig.searchContent+"&c=blog&range=article"],
            "nextUrlXpath":"//a[@title='下一页']/@href",
            "contentUrlXpath":"//div[@class='box-result clearfix']//h2//a/@href",
            "listUrlRegex":".*search.sina.com.cn.*",
            "contentUrlRegex":".*blog.sina.com.cn/.*",
            "fields":[{"name":"title","contentXpath":"//div[@class\u003d\u0027articalTitle\u0027]//h2/text()","require":true},{"name":"author","contentXpath":"//span[@class='SG_txtb']//strong/text()","require":false},{"name":"publishtTime","contentXpath":"//span[@class\u003d\u0027time SG_txtc\u0027]/substring(text(),2,19)","require":true},{"name":"content","contentXpath":"//div[@class\u003d\u0027articalContent   newfont_family\u0027]/allText()","require":true}]
        }
















    $scope.createTemplate=function () {

        //表单验证
        if($scope.collectConfig.name==""){
            swal("模板名称没有填写！");
            return;
        }
        if($scope.collectConfig.description==""){
            swal("模板描述没有填写！");
            return;

        }
        if($scope.collectConfig.searchContent==""){
            swal("搜索内容没有填写！");
            return;

        }

        var param;
        if($scope.collect_forum.type==1){

            tempSinaBlog.name=$scope.collectConfig.name;

            tempSinaBlog.description=$scope.collectConfig.description;

            tempSinaBlog.entryUrls=["http://search.sina.com.cn/?by=all&ie=utf-8&q="+$scope.collectConfig.searchContent+"&c=blog&range=article"];

            param = tempSinaBlog;




        }else if($scope.collect_forum.type==2){

            tempTianya.name=$scope.collectConfig.name;

            tempTianya.description=$scope.collectConfig.description;

            for(var i=1;i<76;i++){
                tempTianya.entryUrls.push("http://search.tianya.cn/bbs?q="+$scope.collectConfig.searchContent+"&pn="+i);
            }
            param=tempTianya;
        }



        CollectCusTempService.createCusTemp(param).then(function (resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function (resp) {

        })



    }







});
