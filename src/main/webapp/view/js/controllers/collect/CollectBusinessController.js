angular.module('MetronicApp').controller('CollectBusinessController', function($rootScope, $scope, CollectCusTempService) {

    $scope.pageTitle="商业数据";


    //瓜子二手车 https://www.guazi.com/sh/buy/


    //链家  Q房网

    //蘑菇街

    //新浪财经  行情中心


    $scope.collect_business={type:2};

    //初始化选择 瓜子二手车
    $scope.collect_selects=[
        {type:1,name:'瓜子二手车',url:'https://www.guazi.com/sh/buy/h2/'},
        {type:2,name:'链家','url':'http://sh.lianjia.com/ershoufang/d1'},
        {type:3,name:'蘑菇街',url:'http://list.mogujie.com/s?page=1&sort=pop&q=%E5%A4%96%E5%A5%97%E7%94%B7&ppath=&ptp=1.eW5XD.0.0.oFVHB&f=baidusem_4uv5iimn1v#category_all'}
        // {type:4,name:'新浪财经-行情中心'}
        ];



    //打开样例链接
    $scope.openExample=function (url) {
        window.open(url);
    }

//瓜子二手车模板
    var tempGuazi=
        {
            "type":"user-listpage",
            "interval":15000,
            "nextUrlXpath":"//a[@class='next']/@href",
            "contentUrlXpath":"//p[@class='infoBox']/a/@href",
            "listUrlRegex":".*www.guazi.com.*",
            "contentUrlRegex":".*html",
            "fields":[
                {
                    "name":"title",
                    "contentXpath":"//h1[@class='dt-titletype']/text()",
                    "require":true
                },
                {
                    "name":"price",
                    "contentXpath":"//span[@class='fc-org pricestype']/allText()",
                    "require":true
                },
                {
                    "name":"serviceCharge",
                    "contentXpath":"//div[@class='car-fuwu']//span/text()",
                    "require":true
                },
                {
                    "name":"useTime",
                    "contentXpath":"//url[@class='assort clearfix']/li[1]/b/text()",
                    "require":true
                },
                {
                    "name":"chainage",
                    "contentXpath":"//url[@class='assort clearfix']/li[2]/b/text()",
                    "require":true
                },
                {
                    "name":"gearbox",
                    "contentXpath":"//url[@class='assort clearfix']/li[3]/b/text()",
                    "require":true
                },
                {
                    "name":"emissionStandard",
                    "contentXpath":"//li[@class='em-sta detailHoverTips']//b[1]/text()",
                    "require":true
                },
                {
                    "name":"useCity",
                    "contentXpath":"//li[@class='em-sta detailHoverTips']//b[2]/text()",
                    "require":true
                },
                {
                    "name":"tel",
                    "contentXpath":"//b[@class='teltype']/text()",
                    "require":true
                }
            ]
        }




//链家
    var tempLianjia=
        {
            "type":"user-listpage",
            // "nextUrlXpath":"//a[text()='下一页']/@href",
            "interval":3000,
            "nextUrlXpath":"//a[@gahref='results_next_page']/@href",
            "contentUrlXpath":"//div[@class='info-panel']//h2//a/@href",
            "listUrlRegex":"  .*[^html]$",
            "contentUrlRegex":".*html",
            "fields":[
                {
                    "name":"title",
                    "contentXpath":"//h1[@class='main']/text()",
                    "require":true
                },
                {
                    "name":"location",
                    "contentXpath":"//p[@class='addrEllipsis fl ml_5']/text()",
                    "require":true
                },
                {
                    "name":"area",
                    "contentXpath":"//div[@class='area']/allText()",
                    "require":true
                },
                {
                    "name":"price",
                    "contentXpath":"//div[@class='mainInfo bold']/allText()",
                    "require":true
                },
                {
                    "name":"tel",
                    "contentXpath":"//div[@class='phone']//div[1]/text()",
                    "require":true
                },
                {
                    "name":"description",
                    "contentXpath":"//div[@class='introContent']/allText()",
                    "require":true
                }
            ]
        }





//蘑菇街
    var tempMogujie=
        {
            "type":"user-listpage",
            "interval":2000,
            "nextUrlXpath":"//div[@class='pagination pd_tb']//a[contains(text(),'下一页')]/@href",
            "contentUrlXpath":"//div[@class='iwf goods_item']//a[1]/@href",
            "listUrlRegex":".*list.mogujie.com.*",
            "contentUrlRegex":".*.*",
            "fields":[
                {
                    "name":"title",
                    "contentXpath":"//h1[@class='goods-title']/allText()",
                    "require":true
                },
                {
                    "name":"price",
                    "contentXpath":"//span[@class='price']/text()",
                    "require":true
                },
                {
                    "name":"saleNo",
                    "contentXpath":"//span[@class='num']/text()",
                    "require":true
                },
                {
                    "name":"commentNo",
                    "contentXpath":"//span[@class='num J_SaleNum']/text()",
                    "require":true
                }
            ]
        }













//初始化表单区域
    $scope.collectConfig={
        name:'',
        description:'',
        entryUrl:''
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
        if($scope.collectConfig.entryUrl==""){
            swal("入口列表页url没有填写！");
            return;

        }

        var param;
        if($scope.collect_business.type==1){


            param = tempGuazi;





        }else if($scope.collect_business.type==2){

            param=tempLianjia;


        }else if($scope.collect_business.type==3){
            param=tempMogujie;

        }else{
            swal("error","没有选择模板","error");
        }
        param.name=$scope.collectConfig.name;

        param.description=$scope.collectConfig.description;

        param.entryUrls=[$scope.collectConfig.entryUrl];


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
