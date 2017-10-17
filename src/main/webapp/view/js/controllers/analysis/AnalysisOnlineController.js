angular.module('MetronicApp').controller('AnalysisOnlineController',function($rootScope,anchorScroll, $document,$scope,$state,$modal,AnalysisOnlineService) {
    $scope.pageTitle="在线分析";
    $scope.content = "  海外网2月22日电22日下午，朝鲜驻马来西亚大使馆向守候在使馆外的记者们递交纸质的声明，回应马来西亚警方今天上午举行的新闻发布会。朝鲜使馆在声明中说，事件发生已过去10天，但马来西亚警方未从嫌疑人身上发现任何证据。马来西亚警方若不是受到韩国或者其他外媒毫无根据说法的影响，就该在调查中尊重朝鲜。"+
   "   “如果两位女嫌犯将有毒的液体用手抹在受害者脸上，她们怎么还能活着？”朝鲜使馆在声明中说道。他们还表示，女嫌犯们未使用毒物，金正男之死有其他原因，并要求大马尽快释放“无辜的”越南籍、印尼籍及朝鲜籍嫌犯。"+
"而对于马方在发布会中对数名朝鲜人的指控，朝鲜使馆在声明中没有任何回应。朝方坚持认为，液体无毒，两名女嫌犯向死者泼洒液体的行为只是一场玩笑。"+
"今天上午，马来西亚警方再次就朝鲜籍男子身亡事件最新进展举行发布会。马来西亚警察总长哈立德表示，马来西亚警方逮捕了4名嫌疑人，其中3女1男，男性嫌疑人据称是其中一名女性嫌疑人的男友，经过调查后，这名男子将会被释放。"+
"马来西亚警方怀疑另有5名朝鲜籍人士与朝鲜籍男子死亡事件有关联，警方怀疑5人中的4人已离开马来西亚，警方怀疑第5名人士人停留在马来西亚，并向媒体提供了该人的照片和相关信息。此外监控录象中有两名嫌疑人警方此前未确认其身份，目前警方对这两名嫌疑人的身份已经确认，其中一人据称是朝鲜大使馆工作人员，另一人据称是鲜航空工作人员。现在马来西亚警方要求朝鲜使馆与警方合作，对这两名嫌疑人进一步调查。"+
"另外有媒体称，死者家属已到达吉隆坡，马来西亚警方对此予以否认，称目前仍未有死者亲属认领死者遗体并提供DNA样本。"+
  "  海外网2月22日电马来西亚国家警察总部22日上午在吉隆坡警察厅召开记者招待会，就朝鲜男子在马遇害一事公布案件调查最新进展。"+
"马来西亚警方总长表示，4名嫌疑人已离开马来西亚，第5名嫌疑人还在马来西亚，目前已确定两名嫌疑人身份，一名为朝鲜大使馆工作人员，一名朝鲜高丽航空工作人员，马来西亚方面要求朝鲜使馆和朝鲜高丽航空公司与马警方合作，展开进一步调查。马来西亚警方否认该朝鲜男子的家人已经达到吉隆坡，并请求其家属前来认领尸体并提供DNA。"
"大马警方表示，马方一直使用“金哲”这个护照上的名字，我们需要DNA来验证他是不是金正男。"+
"大马警方表示，两位女嫌疑犯下手后，举手走向洗手间，她知道要洗手，相信是受过训练，有过谋划，不是一场闹剧。"+
"大马警方表示，案件在马来西亚的司法管辖内，不会与朝鲜进行联合调查。";


//初始化    tabName和选取方式对应
    $scope.segmentType=3;
    $scope.exchangeType=1;




    $scope.data = [
        {text: "中国", weight: 50, link: "https://google.com"},
        {text: "台湾", weight: 30},
        {text: "美国", weight: 27},
        {text: "航母", weight: 7},
        {text: "东亚", weight: 46},
        {text: "上海", weight: 69},
        {text: "萌泰", weight: 9},
        {text: "网络科技", weight: 38},
        {text: "中东", weight: 79},
        {text: "大数据", weight: 10}

        // ...as many words as you want
    ];


//获取关键词
    $scope.keyWords=[];
    $scope.getKeyWords=function(){

        $scope.queryParam={"content":$scope.content};

        AnalysisOnlineService.getKeyWords($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.keyWords=resp.rt_info;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }


//获取分词
    $scope.getSegmentContent=function(segmentType){
        $scope.segmentType=segmentType;
        $scope.queryParam={"content":$scope.content,"segmentType":segmentType};

        AnalysisOnlineService.getSegmentContent($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.terms=resp.rt_info;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })



    }


//获取摘要
$scope.getAbstract=function(){

    $scope.queryParam={"content":$scope.content};

    AnalysisOnlineService.getAbstract($scope.queryParam).then(function(resp) {
        if (resp.error_code == 0) {
            $scope.words=resp.rt_info;
        } else {
            sweetAlert("error", resp.error_msg, "error");
        }
    }, function(resp) {
    })

}

//繁简切换
    $scope.ZHConvert=function(toTraditional){
        if(toTraditional==true){
            $scope.exchangeType=1;
        }else{
            $scope.exchangeType=2;

        }
        $scope.queryParam={"content":$scope.content,"toTraditional":toTraditional};

        AnalysisOnlineService.ZHConvert($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.exchangeContent=resp.rt_info;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }


    //获取拼音
    $scope.getPinyin=function(){
        $scope.exchangeType=3;
        $scope.queryParam={"content":$scope.content};

        AnalysisOnlineService.getPinyin($scope.queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.exchangeContent=resp.rt_info;
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })




    }




$scope.refresh=function(){

    $scope.getKeyWords();
    $scope.getSegmentContent($scope.segmentType);
    $scope.getAbstract();
    if($scope.exchangeType==1){
        $scope.ZHConvert(true);

    }if ($scope.exchangeType==2){

        $scope.ZHConvert(false);
    }if ($scope.exchangeType==3){
        $scope.getPinyin();
    }

}



$scope.toTab=function(tabId){
    anchorScroll.toView('#tab'+tabId, true);


}









    $scope.$on('$viewContentLoaded', function() {
        $scope.refresh();


        //悬浮导航栏
        $("#myNav").affix({
            offset: {
                top: 125
            }
        });





    });








})



















