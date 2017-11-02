angular.module('MetronicApp').controller('CollectListPageController', function($rootScope, $scope, $http, $state,$timeout,CollectNewsService,anchorScroll,CollectCusTempService,$stateParams,$cookieStore) {

    $scope.pageTitle = "自定义采集模块";




    $scope.changeTemplate=function(tableId){

        //console.log("1点击事件，切换tab....");
        $scope.tabId=tableId;
        //console.log("2点击事件，切换tab....");
        if(tableId==1){
            $scope.isadvanced = false;
            $scope.usertemplateStr=angular.toJson($scope.temp1);
        }else if(tableId==2){
            $scope.isadvanced = false;
            $scope.usertemplateStr=angular.toJson($scope.temp2);

        }else if(tableId==3){
            $scope.isadvanced = false;
            $scope.usertemplateStr=angular.toJson($scope.temp3);
        }else if(tableId==4){
            $scope.isadvanced = true;
        }

        initTemplate();




    }





    $scope.reset = function(){
        //刷新页面，重新加载
        $state.go('collect_listpage',{},{reload:true});

    }

    $scope.ajaxType=false;
    $scope.ajaxXpath=false;
    $scope.changeifHide=function (x) {
      if(x==0)
      {
          $scope.ajaxType=false;
          $scope.ajaxXpath=false;
      }
      else if(x==1)
      {
          $scope.ajaxType=true;
          $scope.ajaxXpath=true;
      }

    }
    $scope.changeXpath=function (x) {
        if(x==0)
        {
            $scope.ajaxXpath=false;
        }
        else if(x==1)
        {
            $scope.ajaxXpath=true;
        }

    }
    $scope.testCrawler=function () {
       /* var x = $('#crawlrule').serializeArray();
        var m = [], idata;
        $.each(x, function(i, field){
            // 由于会出现"双引号字符会导致接下来的数据打包失败，故此对元素内容进行encodeURI编码
            // 后台PHP采用urldecode()函数还原数据
            m.push('"' + field.name + '":"' + field.value) + '"';
        });
        idata ='{' +  m.join(',') + '}';
        idata = eval('(' +idata+ ')');*/
        var params = $("#crawlrule").serializeArray();
        var values = {};
        for( x in params ){
            values[params[x].name] = params[x].value;
        }
        var idata = JSON.stringify(values)
        alert(idata.toString());
        alert("if excute");
        console.log(idata.toString());
        CollectCusTempService.crawltest(idata);

    }










//单页采集模板

    $scope.temp1=
    {
        "name":"模板名称",
        "description":"描述",
        "type":"user-singlepage",
        "domain":"",
        "cookie":"",
        "interval":3000,
        "entryUrls":["入口链接1","入口链接2"],
        // "depth":1,
        "contentUrlRegex":"详情页校验",
        "fields":[
            {
                "name":"info_1",
                "contentXpath":"xpath获取info_1",
                "require":true
            },
            {
                "name":"info_2",
                "contentXpath":"xpath获取info_2",
                "require":false
            },
            {
                "name":"info_3",
                "contentXpath":"xpath获取info_3",
                "require":true
            }
        ]
    }




//分页采集模板


    $scope.temp2=
    {
        "name":"模板名称",
        "description":"描述",
        "type":"user-listpage",
        "domain":"",
        "cookie":"",
        "interval":3000,
        "entryUrls":["入口链接1","入口链接2"],
        // "depth":2,
        "nextUrlXpath":"xpth获取下一页",
        "contentUrlXpath":"xpth获取详情页",
        "listUrlRegex":"列表页校验",
        "contentUrlRegex":"详情页校验",
        "fields":[
            {
                "name":"info_1",
                "contentXpath":"xpath获取info_1",
                "require":true
            },
            {
                "name":"info_2",
                "contentXpath":"xpath获取info_2",
                "require":false
            },
            {
                "name":"info_3",
                "contentXpath":"xpath获取info_3",
                "require":true
            }
        ]
    }

//分层采集
    $scope.temp3=
    {
        "name":"模板名称",
        "description":"描述",
        "type":"user-multipage",
        "domain":"",
        "cookie":"",
        "interval":3000,
        "entryUrls":["入口链接1","入口链接2"],
        "depth":3,
        "contentUrlRegex":"详情页校验",
        "fields":[
            {
                "name":"info_1",
                "contentXpath":"xpath获取info_1",
                "require":true
            },
            {
                "name":"info_2",
                "contentXpath":"xpath获取info_2",
                "require":false
            },
            {
                "name":"info_3",
                "contentXpath":"xpath获取info_3",
                "require":true
            }
        ]
    }



    $scope.usertemplateStr=angular.toJson($scope.temp1);



    $scope.createTemplate=function(){
        //检测模板是否符合json规范
        try{
            angular.fromJson($scope.usertemplateStr);
        }catch(exception){
            sweetAlert("不符合json规范，请重新填写！");
            return;
        }

        CollectCusTempService.createCusTemp($scope.usertemplateStr).then(function (resp) {
            if (resp.error_code == 0) {
                    swal("success!", resp.rt_msg, "success");

            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function (resp) {
        })
    }







    //初始化模板数据
    var initTemplate=function(){
        var content = $scope.usertemplateStr;
        var result = '';
        if (content!='') {

            try{
                current_json = jsonlint.parse(content);
                current_json_str = JSON.stringify(current_json);
                //current_json = JSON.parse(content);
                result = new JSONFormat(content,4).toString();
            }catch(e){
                result = '<span style="color: #f1592a;font-weight:bold;">' + e + '</span>';
                current_json_str = result;
            }

            $('#json-target').html(result);
        }else{
            $('#json-target').html('');
        }
    }

































//解析json(等时间充裕改写成angularjs语法)
    var current_json = '';
    var current_json_str = '';
    var xml_flag = false;
    var zip_flag = false;
    var shown_flag = false;
    $('.tip').tooltip();
    $('#json-src').keyup(function(){
        initTemplate();

    });

    $('.shown').click(function(){
        if (!shown_flag) {
            readerLine();
            $('#json-src').attr("style","height:553px;padding:0 10px 10px 40px;border:0;border-right:solid 1px #ddd;border-bottom:solid 1px #ddd;border-radius:0;resize: none; outline:none;");
            $('#json-target').attr("style","padding:0px 50px;");
            $('#line-num').show();
            $('.numberedtextarea-line-numbers').show();
            shown_flag = true;
            $(this).attr('style','color:#15b374;');
        }else{
            $('#json-src').attr("style","height:553px;padding:0 10px 10px 20px;border:0;border-right:solid 1px #ddd;border-bottom:solid 1px #ddd;border-radius:0;resize: none; outline:none;");
            $('#json-target').attr("style","padding:0px 20px;");
            $('#line-num').hide();
            $('.numberedtextarea-line-numbers').hide();
            shown_flag = false;
            $(this).attr('style','color:#999;');
        }
    });
    function readerLine(){
        var line_num = $('#json-target').height()/20;
        $('#line-num').html("");
        var line_num_html = "";
        for (var i = 1; i < line_num+1; i++) {
            line_num_html += "<div>"+i+"<div>";
        }
        $('#line-num').html(line_num_html);
    }

    $('#json-src').keyup();













    $scope.$on('$viewContentLoaded', function() {

        if($stateParams.tabId>0){
            //console.log("初始化方法")
            $cookieStore.put('refreshPageParam',$stateParams);

        }
        //console.log("初始化获取tabId");
        $scope.tabId=$cookieStore.get('refreshPageParam').tabId;
        $scope.changeTemplate($scope.tabId);
        //console.log("初始化完毕");
        $scope.active={
            "tab1":$scope.tabId==1?true:false,
            "tab2":$scope.tabId==2?true:false,
            "tab3":$scope.tabId==3?true:false,
            "tab3":$scope.tabId==4?true:false,


        }




    });

});


var ModalDemoCtrl = function ($scope, $modal) {
    $scope.open = function (size) {
        var modalInstance = $modal.open({
            templateUrl: 'myModalContent.html',
            controller: ModalInstanceCtrl,
            size: size
        });

        modalInstance.result.then(function (take_me_outside) {
            $scope.message = take_me_outside;
        });
    };
};

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

var ModalInstanceCtrl = function ($scope, $modalInstance) {
    $scope.take_me_outside = "asdfasdf";

    $scope.ok = function (take_me_outside) {
        console.log($modalInstance);
        $modalInstance.close(take_me_outside);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};

