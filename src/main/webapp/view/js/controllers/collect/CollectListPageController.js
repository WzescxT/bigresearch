angular.module('MetronicApp').controller('CollectListPageController', function($rootScope, $scope, $http, $state,$timeout,CollectNewsService,anchorScroll,CollectCusTempService,$stateParams,$cookieStore, $http) {


    var jsonData = {

        "basic_rule": {
            "project_id": 123,
            "task_id": 123,
            "task_leader": "abc",
            "task_description": "abc"
        },

        "assistant_rule": {
            "open": true,
            "login_page_path": "abc",
            "login_username": "abc",
            "login_username_xpath": "abc",
            "login_password": "abc",
            "login_password_xpath": "abc",
            "login_verifycode": "abc",
            "login_verifycode_xpath": "abc",
            "cookie": "abc"
        },

        "url_pattern": {

            "current_selected": "single",

            "single": {

                // "single_url_pattern_name": "abc",
                "url_path": "abc"

            },

            "list": {
                // "list_url_pattern_name": "abc",
                "url_wildcard": "abc",
                "init_value": 123,
                "gap": 123,
                "pages_num": 123,
                "list_url_file_path": "abc"
            },


            "click": {
                // "click_url_pattern_name": "abc",
                "url_index_path": "abc",
                "next_page_xpath": "abc",
                "click_url_file_path": "abc"
            },

            "import": {
                // "import_url_pattern_name": "abc",
                "file_upload_path": "abc",
                "click_url_file_path": "abc"
            }

        },

        "creep_rule":
            [
                {
                    "creep_pattern_name": "abc",
                    "ajax": {
                        "open": true,
                        "ajax_pattern": "abc",
                        "button_xpath": "abc"
                    },

                    "attribute_xpath": "abc",
                    "attribute_name": "abc"
                },
                {
                    "creep_pattern_name": "abc",
                    "ajax": {
                        "open": true,
                        "ajax_pattern": "abc",
                        "button_xpath": "abc"
                    },

                    "attribute_xpath": "abc",
                    "attribute_name": "abc"
                }
            ],

        "store_rule": {
            "store_pattern": "abc"
        },

        "run_rule": {
            "proxy_id": "abc",
            "time": {
                "start_time": "abc",
                "end_time": "abc"
            },
            "headers": "abc",
            "custom_config": "abc"
        }

    };

    // direction == 0 上一步
    // direction == 1 下一步
    $scope.changeTabs=function(id, flag) {

        if(flag){

            // 基本规则
            jsonData.basic_rule.project_id = $scope.selected_project.advanceProjectEntity.project_id;
            jsonData.basic_rule.task_id =  $scope.selected_task.task_id;
            jsonData.basic_rule.task_leader = $scope.task_leader;
            jsonData.basic_rule.task_description =  $scope.task_description;

            // 辅助规则
            jsonData.assistant_rule.open = $scope.open;
            jsonData.assistant_rule.login_page_path = $scope.login_page_path;
            jsonData.assistant_rule.login_username = $scope.login_username;
            jsonData.assistant_rule.login_username_xpath = $scope.login_username_xpath;
            jsonData.assistant_rule.login_password =  $scope.login_password;
            jsonData.assistant_rule.login_password_xpath = $scope.login_password_xpath;
            jsonData.assistant_rule.login_verifycode = $scope.login_verifycode;
            jsonData.assistant_rule.login_verifycode_xpath = $scope.login_verifycode_xpath;
            jsonData.assistant_rule.cookie = $scope.cookie;

            // URL规则
            var arr = ["single","list","click","import"];
            jsonData.url_pattern.current_selected = arr[$scope.crawl_pattern];

            jsonData.url_pattern.single.url_path = $scope.url_path;

            // 文件怎么上传？？url_file_path 是什么
            jsonData.url_pattern.list.url_wildcard = $scope.url_wildcard;
            jsonData.url_pattern.list.init_value =  $scope.init_value;
            jsonData.url_pattern.list.gap = $scope.gap;
            jsonData.url_pattern.list.pages_num =  $scope.pages_num;
            jsonData.url_pattern.list.list_url_file_path = "";

            jsonData.url_pattern.click.url_index_path =  $scope.url_index_path;
            jsonData.url_pattern.click.next_page_xpath = $scope.next_page_xpath;
            jsonData.url_pattern.click.click_url_file_path = "";

            jsonData.url_pattern.import.file_upload_path = "";
            jsonData.url_pattern.import.import_url_file_path="";


            // 持久化规则
            var storeArr = ['file','database'];
            jsonData.store_rule.store_pattern = storeArr[$scope.store_pattern];


            // 采集规则
            jsonData.creep_rule = $scope.creep_rule;

            // 执行计划
            jsonData.run_rule.proxy_id = $scope.proxy_id
            jsonData.run_rule.time.start_time=$scope.start_time;
            jsonData.run_rule.time.end_time=$scope.end_time;
            jsonData.run_rule.headers=$scope.headers;
            jsonData.run_rule.custom_config=$scope.custom_config;


            $http({
                method: 'POST',
                url: '/advance/test',
                data: jsonData
                // data: $.param(jsonData),
                // headers: {'Content-Type':'application/x-www-form-urlencoded'},
                // transformRequest: angular.identity
            }).then(function successCallback(response) {
                console.log(response.data);
            }, function errorCallback(response) {
                // 请求失败执行代码
                console.log("create project bad");
            });
        }

        $("#myTab a[href='/#" + id +"']").tab('show')

    };

    $scope.pageTitle = "自定义采集模块";


    $scope.creep_rule = [];
    
    $scope.del = function ($index) {
        if($index>=0){
            $scope.creep_rule.splice($index,1);
        }
    };

    $scope.add = function () {
        $scope.creep_name = "";
        $scope.creep_pattern = "单体";
        $scope.x = false;
        $scope.ajax_pattern = "点击";
        $scope.button_xpath = "";
        $scope.attribute_xpath = "";
        $scope.attribute_name = "";
        $('#modal-add').modal('show');
    };
    
    $scope.save = function () {

        var newEle = {
            "creep_name": $scope.creep_name,
            "creep_pattern": $scope.creep_pattern,
            "ajax": {
                "open": $scope.x,
                "ajax_pattern": $scope.ajax_pattern,
                "button_xpath": $scope.button_xpath
            },
            "attribute_xpath": $scope.attribute_xpath,
            "attribute_name":  $scope.attribute_name
        };
        $scope.creep_rule.push(newEle);

        $('#modal-add').modal('hide');
    };

    $scope.update = function($index){
        $scope.t_index = $index;
        $scope.creep_name = $scope.creep_rule[$index].creep_name;
        $scope.creep_pattern = $scope.creep_rule[$index].creep_pattern;
        $scope.x = $scope.creep_rule[$index].ajax.open;
        $scope.ajax_pattern = $scope.creep_rule[$index].ajax.ajax_pattern;
        $scope.button_xpath = $scope.creep_rule[$index].ajax.button_xpath;
        $scope.attribute_xpath = $scope.creep_rule[$index].attribute_xpath;
        $scope.attribute_name = $scope.creep_rule[$index].attribute_name;
        $('#modal-update').modal('show');
    };

    $scope.modify = function ($index) {

        $scope.creep_rule[$index].creep_name = $scope.creep_name;
        $scope.creep_rule[$index].creep_pattern =$scope.creep_pattern;

        var ajaxTypes = ["点击","滚动"];
        $scope.creep_rule[$index].ajax.open = $scope.x;
        $scope.creep_rule[$index].ajax.ajax_pattern = types[$scope.y];
        $scope.creep_rule[$index].ajax.button_xpath = $scope.button_xpath;
        $scope.creep_rule[$index].attribute_xpath = $scope.attribute_xpath;
        $scope.creep_rule[$index].attribute_name = $scope.attribute_name;

        $('#modal-update').modal('hide');

    }


    // 渲染项目和任务选择框
    $http({
        method: 'GET',
        url: '/advance/getProjectsDetailInfo'
    }).then(function successCallback(response) {
        $scope.projects = response.data;
        $scope.selected_project = $scope.projects[$scope.projects.length-1];
        tasks = $scope.selected_project.advanceTaskEntities;
        $scope.selected_task = tasks[tasks.length-1];


    }, function errorCallback(response) {
        // 请求失败执行代码
        console.log("get projects bad")
    });


    // 创建项目
    $scope.createAdvanceProject = function () {

        if($scope.new_project_name!=""){

            for(var i = 0; i < $scope.projects.length; i++){
                if($scope.projects[i].advanceProjectEntity.project_name == $scope.new_project_name){
                    alert("项目已经存在");
                    return;
                }

            }

            $http({
                method: 'POST',
                url: '/advance/createProject',
                data:{
                    project_name:$scope.new_project_name
                }
            }).then(function successCallback(response) {
                console.log(response.data.newProject);
                $scope.projects.push(response.data.newProject);
                $scope.selected_project = $scope.projects[$scope.projects.length-1];
                // tasks = $scope.selected_project.advanceTaskEntities;
                // $scope.selected_task = tasks[tasks.length-1];
            }, function errorCallback(response) {
                // 请求失败执行代码
                console.log("create project bad");
            });
        }

    };


    // 创建任务
    $scope.createAdvanceTask = function () {
        if($scope.new_task_name!=""){
            $http({
                method: 'POST',
                url: '/advance/createTask',
                data:{
                    project_id: $scope.selected_project.advanceProjectEntity.project_id,
                    task_name: $scope.new_task_name
                }
            }).then(function successCallback(response) {

                for(var i = 0; i < $scope.projects.length; i++){
                    if($scope.projects[i].advanceProjectEntity.project_id == response.data.project_id){
                        $scope.projects[i].advanceTaskEntities.push(response.data.newTask);
                        $scope.selected_project = $scope.projects[i];
                        tasks = $scope.selected_project.advanceTaskEntities;
                        $scope.selected_task = tasks[tasks.length-1];
                        return;
                    }
                    $('#modal_create_task').modal('hide');
                }

            }, function errorCallback(response) {
                // 请求失败执行代码
                console.log("create task bad");
            });
        }
    };


    $scope.uploadtest = function () {

        $("#myTab a[href='/#assist']").tab('show');

        // console.log("yes");
        // console.log($scope.file_upload_path);
        // var fd = new FormData();
        // var file = document.querySelector('input[id=file_upload_path]').files[0];
        // console.log(file);
        // fd.append('file_upload_path', file);
        // fd.append('code',"some");
        // $http({
        //     method: 'POST',
        //     url: '/advance/uploadFile',
        //     data: fd,
        //     headers: {'Content-Type':undefined},
        //     transformRequest: angular.identity
        // }).then(function successCallback(response) {
        //     // console.log(response.data)
        //     console.log("yes");
        //
        // }, function errorCallback(response) {
        //     // 请求失败执行代码
        //     console.log("bad");
        //
        // });
    };



    /************************************************新旧分隔线**************************************************/

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

