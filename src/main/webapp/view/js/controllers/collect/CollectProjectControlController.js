angular.module('MetronicApp').controller('CollectProjectControlController', function($cookieStore,CollectProjectService,CollectLogService,$stateParams,$rootScope, $scope, $state,$interval) {
    //展示项目信息，控制采集log


    $scope.radioModel = 'project';

    $scope.projectId = $stateParams.projectId==null?$cookieStore.get('refreshPageParam').projectId:$stateParams.projectId;

    $scope.currentTab = 1;
    $scope.changeView = function (tabId) {
        $scope.currentTab = tabId;
    }


//初始化模板数据
    var initTemplate=function(){

        try {
            //删除不可视信息
            delete $scope.template.entryUrlsStr;
            delete $scope.template.fieldsStr;
            delete $scope.template.createTime;
            delete $scope.template.createUser;
            delete $scope.template.id;

            //删除为空的信息
            for(var key in $scope.template){
                if ($scope.template[key]==null){
                    delete $scope.template[key];
                }
            }



            } catch (e) {
        }

        var content = angular.toJson($scope.template);
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






    $scope.getProject=function(){
        var queryParam={"projectId":$scope.projectId};
        CollectProjectService.getProject(queryParam).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.collectProject=resp.rt_info.project;
                $scope.template=resp.rt_info.template;
                initTemplate();
            } else {
                alert(resp.error_msg);
            }
        }, function(resp) {
        })

    }

    //跳转到数据展示页面
    $scope.showInfo = function(id) {
        $state.go("showCollectInfo", {
            projectId : id
        });
    }


    $scope.alertCollect=function() {
        if($scope.collectProject.status=='准备采集'){
        swal({
                title: "尚未采集",
                text: "该项目尚未采集，请确认进行采集操作!",
                type: "info",
                showCancelButton: true,
                confirmButtonText: "开始采集",
                cancelButtonText: "暂不采集!",
                closeOnConfirm: false,
                closeOnCancel: false
            },
            function(isConfirm){
                if (isConfirm) {
                    $scope.startCollect();
                    swal("执行", "正在采集中。。。", "success");
                } else {
                    swal("取消", "暂时不执行采集", "error");
                }
            });

        }
    };






    $scope.startCollect=function(){
        CollectProjectService.startCollect($scope.collectProject).then(function(resp) {
            if (resp.error_code == 0) {
                swal("success!", resp.rt_msg, "success");

            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }











//处理折线图




//动态获取折线图数据


    $scope.labels = ["2015-03-18 9:20", "2015-03-18 9:21", "2015-03-18 9:22", "2015-03-18 9:23", "2015-03-18 9:24", "2015-03-18 9:25", "2015-03-18 9:26", "2015-03-18 9:26", "2015-03-18 9:26", "2015-03-18 9:26", "2015-03-18 9:26"];
    $scope.series = ['collectNo'];
    $scope.data = [];
    $scope.onClick = function (points, evt) {
        console.log(points, evt);
    };
    $scope.datasetOverride = [{ yAxisID: 'y-axis-1' },{ yAxisID: 'y-axis-2' }];
    $scope.options = {
        scales: {
            yAxes: [
                {
                    id: 'y-axis-1',
                    type: 'linear',
                    display: true,
                    position: 'left'
                }
            ]
        },
        colors : [ '#803690']
    };








//加载日志数据



    $scope.pageConfig = {
        "pageNow":0,
        "maxPage":1,
        "pageSize":10
    }


    $scope.selectedItem=1;


    $scope.chooseItem = function(itemId){

        $scope.logs = [];
        $scope.selectedItem=itemId;
        $scope.pageConfig = {
            "pageNow":0,
            "maxPage":0,
            "pageSize":10
        }

        $scope.loadMore();
    }

    $scope.loadMore = function(){
        var level = "";
        if($scope.selectedItem == 2){
            level = "INFO";
        }else if($scope.selectedItem == 3){
            level = "WARN";
        }else if($scope.selectedItem == 4){
            level = "ERROR";
        }


        var param = {"projectId":$scope.projectId,"level":level,"pageNow":$scope.pageConfig.pageNow+1};
        console.log(param);
        CollectLogService.getLogs(param).then(function(resp) {
            if (resp.error_code == 0) {
                //concat函数：连接两个数据，返回副本，不改变原数组
                $scope.logs = resp.rt_mapinfo.collectLogs.concat($scope.logs);
                console.log(resp);
                if(resp.rt_mapinfo.maxPage!=0){
                    //logs不为空

                    $scope.pageConfig = {
                        "pageNow":$scope.pageConfig.pageNow+1,
                        "maxPage":resp.rt_mapinfo.maxPage,
                        "pageSize":$scope.pageConfig.pageSize
                    }

                }

            }
        }, function(resp) {
        })



    }




//获取折线图数据
    $scope.loadChartData = function(){


        var param = {"projectId":$scope.projectId};
        CollectLogService.getLogData(param).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.data.push(resp.rt_mapinfo.data);
                $scope.labels = resp.rt_mapinfo.labels;

                console.log(resp);
            }
        }, function(resp) {
        })



    }







    $scope.$on('$viewContentLoaded', function() {

        if($stateParams.projectId>0){
            $scope.projectId = $stateParams.projectId;
            $cookieStore.put('refreshPageParam',$stateParams);
        }else{
            $scope.projectId = $cookieStore.get('refreshPageParam').projectId;
        }
        $scope.getProject();



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



    $scope.chooseItem(1);

    $scope.loadChartData();

});