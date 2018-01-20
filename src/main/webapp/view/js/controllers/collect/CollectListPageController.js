angular.module('MetronicApp')
// .directive("filesInput", function() {
//     return {
//         require: "ngModel",
//         link: function postLink(scope,elem,attrs,ngModel) {
//             elem.on("change", function(e) {
//                 // var files = elem[0].files;
//                 // var reader = new FileReader();
//                 // reader.onload = function(e){
//                 //     ngModel.$setViewValue("hello");
//                 // };
//                 // reader.readAsText(files);
//                 var input = document.querySelector('#input');
//                 var reader = new FileReader();
//                 reader.onload = function(e){
//                     span.innerText = e.target.result;
//                 };
//                 reader.readAsText(file);
//             })
//         }
//     }
// })

    .controller('CollectListPageController', function($rootScope, $scope, $http, $state, $timeout, CollectNewsService, anchorScroll, CollectCusTempService, $stateParams, $cookieStore, $q) {

        $scope.chartSeries = [
            {"name": "Some data", "data": [1, 2, 4, 7, 3], id: 's1'},
        ];


        $scope.chartConfig = {

            chart: {
                height: 500,
                width: 500,
                type: 'line'
            },
            plotOptions: {
                series: {
                    stacking: ''
                }
            },
            series: $scope.chartSeries,
            title: {
                text: '时间分布图'
            }
        }

        // monitor
        $scope.RunningTasks = [
            {name:"项目1",age:"任务1"},
            {name:"项目1",age:"任务2"},
            {name:"项目2",age:"任务2"},
            {name:"项目2",age:"任务1"},
            {name:"项目3",age:"任务2"},
            {name:"项目4",age:"任务2"},
            {name:"项目5",age:"任务1"}
        ];
        var vm = $scope.vm = {};
        vm.value = 50;
        vm.style = 'progress-bar-info';
        vm.showLabel = true;
        $scope.isModify = false;


        // 显示 monitor 详情
        $scope.showDetails = function($index){
            $('#monitor_detail').modal('show');
        };
        // 暂停
        $scope.isSuspend = function($index){
            if(confirm('确定暂停吗？')){
                //alert('已暂停')
            }
        };
        $scope.isStop = function($index){
            if(confirm('确定结束吗？')){
                //alert('已结束')
            }
        };


        $scope.mycheck = "Y";

        $scope.onInputChange = function () {
            console.log("input change");
        };

        $scope.setIPs = function (element) {
            $scope.currentFile = element.files[0];
            var reader = new FileReader();

            reader.onload = function (event) {
                // $scope.image_source = event.target.result
                var proxy_ids = event.target.result.split("\n");
                $scope.proxy_ids = proxy_ids;
                $scope.$apply()
            };
            // when the file is read it triggers the onload event above.
            reader.readAsText(element.files[0]);
        };

        $scope.setUrls = function (element) {
            $scope.currentFile = element.files[0];
            var reader = new FileReader();

            reader.onload = function (event) {
                // $scope.image_source = event.target.result
                var import_urls = event.target.result.split("\n");
                $scope.import_urls = import_urls;
                $scope.$apply()
            };
            // when the file is read it triggers the onload event above.
            reader.readAsText(element.files[0]);
        };

        $scope.getProjectsDetailInfo = function () {
            $http({
                method: 'GET',
                url: '/advance/getProjectsDetailInfo'
            }).then(function successCallback(response) {
                $scope.projects = response.data;
                $scope.selected_project = $scope.projects[$scope.projects.length - 1];
                $scope.selected_project_exporting = $scope.projects[$scope.projects.length - 1];
                tasks = $scope.selected_project.advanceTaskEntities;
                $scope.selected_task = tasks[tasks.length - 1];
                $scope.selected_task_exporting = tasks[tasks.length - 1];
                setTaskInfo($scope.selected_task.task_id);
            }, function errorCallback(response) {
                // 请求失败执行代码
                console.log("get projects bad")
            });
        };
        $scope.getProjectsDetailInfo();
        $scope.changeProject = function () {
            $scope.selected_task = $scope.selected_project.advanceTaskEntities[$scope.selected_project.advanceTaskEntities.length - 1];
            setTaskInfo($scope.selected_task.task_id);
        };

        $scope.changeProject_exporting = function () {
            //////////////////////////////////////////////
            // Jianfeng is debugging...
            console.log("$scope.selected_project_exporting = " + $scope.selected_project_exporting);
            //////////////////////////////////////////////
        };

        $scope.changeTask = function () {
            setTaskInfo($scope.selected_task.task_id);
        };

        $scope.changeTask_exporting = function () {
            //////////////////////////////////////////////
            // Jianfeng is debugging...
            console.log("$scope.selected_task_exporting = " + $scope.selected_task_exporting);
            //////////////////////////////////////////////
        };

        $scope.clearData = function () {
        };

        function clearInfo() {
            $scope.selected_project.advanceProjectEntity.project_id = null;
            $scope.selected_task.task_id = null;
            $scope.task_leader = null;
            $scope.task_description = null;

            // // 辅助规则
            $scope.open = null;
            $scope.login_page_path = null;
            $scope.login_username = null;
            $scope.login_username_xpath = null;
            $scope.login_password = null;
            $scope.login_password_xpath = null;
            $scope.login_verifycode = null;
            $scope.login_verifycode_xpath = null;
            $scope.cookie = null;

            $scope.crawl_pattern = null;
            $scope.url_path = null;

            $scope.url_wildcard = null;
            $scope.init_value = null;
            $scope.gap = null;
            $scope.pages_num = null;
            // jsonData.url_pattern.list.list_url_file_path = "";
            //
            $scope.url_index_path = null;
            $scope.next_page_xpath = null;
            // jsonData.url_pattern.click.click_url_file_path = "";

            $scope.import_urls = null;
            //jsonData.url_pattern.import.file_upload_path = "";
            //jsonData.url_pattern.import.import_url_file_path="";
            //
            //
            // 持久化规则
            $scope.store_pattern = null;
            //
            // 采集规则
            $scope.creep_rule = [];
            //
            // 执行计划
            $scope.proxy_ids = null;
            $scope.start_time = null;
            $scope.end_time = null;
            $scope.headers = null;
            $scope.custom_config = null;
        }

        function setTaskInfo(task_id) {
            $http({
                method: 'GET',
                url: '/advance/task_config?task_id=' + task_id
            }).then(function successCallback(response) {
                console.log("yes");
                clearInfo();
                var jsonData = response.data;
                changeFrontData(jsonData);
            }, function errorCallback(response) {
                // 请求失败执行代码
                console.log("get TaskInfo bad");
            });
        }

        function changeFrontData(jsonData) {
            // 基本规则
            $scope.selected_project.advanceProjectEntity.project_id = jsonData.basic_rule.project_id;
            $scope.selected_task.task_id = jsonData.basic_rule.task_id;
            $scope.task_leader = jsonData.basic_rule.task_leader;
            $scope.task_description = jsonData.basic_rule.task_description;

            // // 辅助规则
            $scope.open = jsonData.assistant_rule.open;
            $scope.login_page_path = jsonData.assistant_rule.login_page_path;
            $scope.login_username = jsonData.assistant_rule.login_username;
            $scope.login_username_xpath = jsonData.assistant_rule.login_username_xpath;
            $scope.login_password = jsonData.assistant_rule.login_password;
            $scope.login_password_xpath = jsonData.assistant_rule.login_password_xpath;
            $scope.login_verifycode = jsonData.assistant_rule.login_verifycode;
            $scope.login_verifycode_xpath = jsonData.assistant_rule.login_verifycode_xpath;
            $scope.cookie = jsonData.assistant_rule.cookie;

            // $scope.crawl_pattern = jsonData.url_pattern.current_selected;
            switch (jsonData.url_pattern.current_selected) {
                case "single": {
                    $scope.crawl_pattern = "单页";

                    break;
                }

                case "list": {
                    $scope.crawl_pattern = "列表";

                    break;
                }

                case "click": {
                    $scope.crawl_pattern = "翻页";

                    break;
                }

                case "import": {
                    $scope.crawl_pattern = "导入";

                    break;
                }

                default: {
                    break;
                }
            }

            $scope.url_path = jsonData.url_pattern.single.url_path;

            $scope.url_wildcard = jsonData.url_pattern.list.url_wildcard;
            $scope.init_value = jsonData.url_pattern.list.init_value;
            $scope.gap = jsonData.url_pattern.list.gap;
            $scope.pages_num = jsonData.url_pattern.list.pages_num;
            // jsonData.url_pattern.list.list_url_file_path = "";
            //
            $scope.url_index_path = jsonData.url_pattern.click.url_index_path;
            $scope.next_page_xpath = jsonData.url_pattern.click.next_page_xpath;
            // jsonData.url_pattern.click.click_url_file_path = "";

            $scope.import_urls = jsonData.url_pattern.import.import_urls;
            //jsonData.url_pattern.import.file_upload_path = "";
            //jsonData.url_pattern.import.import_url_file_path="";
            //
            //
            // 持久化规则
            $scope.store_pattern = jsonData.store_rule.store_pattern;
            //
            // 采集规则
            $scope.creep_rule = jsonData.creep_rule;
            //
            // 执行计划
            $scope.proxy_ids = jsonData.run_rule.proxy_ids;
            $scope.start_time = jsonData.run_rule.time.start_time;
            $scope.end_time = jsonData.run_rule.time.end_time;
            $scope.headers = jsonData.run_rule.headers;
            $scope.custom_config = jsonData.run_rule.custom_config;
        }
        // 执行计划
        $scope.executeTask = function () {
            var jsonData = {"task_id": "", "project_id": ""};
            jsonData.project_id = $scope.selected_project.advanceProjectEntity.project_id;
            jsonData.task_id = $scope.selected_task.task_id;
            console.log($scope.selected_project.advanceProjectEntity.project_id);
            $http({
                method: 'POST',
                url: '/CrawlPlan/Plan',
                data: jsonData
            }).then(function successCallback(response) {
                console.log(response.data);
            }, function errorCallback(response) {
                // 请求失败执行代码
                console.log("post data bad");
            });
        };

        // flag == 0 上一步
        // flag == 1 下一步
        $scope.changeTabs = function (id, flag) {
            $("#myTab a[href='/#" + id + "']").tab('show');

            var jsonData = {

                "basic_rule": {
                    "project_id": $scope.selected_project.advanceProjectEntity.project_id,
                    "task_id": $scope.selected_task.task_id,
                    "task_leader": "",
                    "task_description": ""
                },

                "assistant_rule": {
                    "open": false,
                    "login_page_path": "",
                    "login_username": "",
                    "login_username_xpath": "",
                    "login_password": "",
                    "login_password_xpath": "",
                    "login_verifycode": "",
                    "login_verifycode_xpath": "",
                    "cookie": "abc"
                },

                "url_pattern": {
                    "current_selected": "",

                    "single": {
                        "url_path": ""
                    },

                    "list": {
                        // "list_url_pattern_name": "abc",
                        "url_wildcard": "",
                        "init_value": 0,
                        "gap": 0,
                        "pages_num": 0,
                        "list_url_file_path": ""
                    },

                    "click": {
                        // "click_url_pattern_name": "abc",
                        "url_index_path": "",
                        "next_page_xpath": "",
                        "click_url_file_path": ""
                    },
                    "import": {
                        // "import_url_pattern_name": "abc",
                        "import_urls": [],
                        "file_upload_path": "",
                        "import_url_file_path": ""
                    }
                },

                "creep_rule":
                    [
                        {
                            "creep_pattern_name": "",
                            "ajax": {
                                "open": true,
                                "ajax_pattern": "",
                                "button_xpath": ""
                            },

                            "attribute_xpath": "",
                            "attribute_xpath2": "",
                            "attribute_name": "",
                            "extract_way": ""
                        },
                        {
                            "creep_pattern_name": "",
                            "ajax": {
                                "open": true,
                                "ajax_pattern": "",
                                "button_xpath": ""
                            },

                            "attribute_xpath": "",
                            "attribute_xpath2": "",
                            "attribute_name": "",
                            "extract_way": ""
                        }
                    ],

                "store_rule": {
                    "store_pattern": ""
                },

                "run_rule": {
                    "proxy_ids": [],
                    "time": {
                        "start_time": "",
                        "end_time": ""
                    },
                    "headers": "",
                    "custom_config": ""
                }

            };

            if (flag) {
                // 基本规则
                jsonData.basic_rule.project_id = $scope.selected_project.advanceProjectEntity.project_id;
                jsonData.basic_rule.task_id = $scope.selected_task.task_id;
                jsonData.basic_rule.task_leader = $scope.task_leader;
                jsonData.basic_rule.task_description = $scope.task_description;

                // 辅助规则
                jsonData.assistant_rule.open = $scope.open;
                jsonData.assistant_rule.login_page_path = $scope.login_page_path;
                jsonData.assistant_rule.login_username = $scope.login_username;
                jsonData.assistant_rule.login_username_xpath = $scope.login_username_xpath;
                jsonData.assistant_rule.login_password = $scope.login_password;
                jsonData.assistant_rule.login_password_xpath = $scope.login_password_xpath;
                jsonData.assistant_rule.login_verifycode = $scope.login_verifycode;
                jsonData.assistant_rule.login_verifycode_xpath = $scope.login_verifycode_xpath;
                jsonData.assistant_rule.cookie = $scope.cookie;

                // URL规则
                switch ($scope.crawl_pattern) {
                    case "单页": {
                        jsonData.url_pattern.current_selected = "single";

                        break;
                    }

                    case "列表": {
                        jsonData.url_pattern.current_selected = "list";

                        break;
                    }

                    case "翻页": {
                        jsonData.url_pattern.current_selected = "click";

                        break;
                    }

                    case "导入": {
                        jsonData.url_pattern.current_selected = "import";

                        break;
                    }

                    default: {
                        break;
                    }
                }

                jsonData.url_pattern.single.url_path = $scope.url_path;

                // 文件怎么上传？？url_file_path 是什么
                jsonData.url_pattern.list.url_wildcard = $scope.url_wildcard;
                jsonData.url_pattern.list.init_value =  $scope.init_value;
                jsonData.url_pattern.list.gap = $scope.gap;
                jsonData.url_pattern.list.pages_num =  $scope.pages_num;
                // jsonData.url_pattern.list.list_url_file_path = "";

                jsonData.url_pattern.click.url_index_path =  $scope.url_index_path;
                jsonData.url_pattern.list.init_value = $scope.init_value;
                jsonData.url_pattern.list.gap = $scope.gap;
                jsonData.url_pattern.list.pages_num = $scope.pages_num;
                // jsonData.url_pattern.list.list_url_file_path = "";

                jsonData.url_pattern.click.url_index_path = $scope.url_index_path;
                jsonData.url_pattern.click.next_page_xpath = $scope.next_page_xpath;
                // jsonData.url_pattern.click.click_url_file_path = "";

                jsonData.url_pattern.import.import_urls = $scope.import_urls;
                // jsonData.url_pattern.import.file_upload_path = "";
                // jsonData.url_pattern.import.import_url_file_path="";

                // 持久化规则
                jsonData.store_rule.store_pattern = $scope.store_pattern;

                // 采集规则
                jsonData.creep_rule = $scope.creep_rule;

                // 执行计划
                jsonData.run_rule.proxy_ids = $scope.proxy_ids;
                jsonData.run_rule.time.start_time=$scope.start_time;
                jsonData.run_rule.time.end_time=$scope.end_time;
                jsonData.run_rule.headers=$scope.headers;
                jsonData.run_rule.custom_config=$scope.custom_config;
                jsonData.run_rule.time.start_time = $scope.start_time;
                jsonData.run_rule.time.end_time = $scope.end_time;
                jsonData.run_rule.headers = $scope.headers;
                jsonData.run_rule.custom_config = $scope.custom_config;

                $http({
                    method: 'POST',
                    url: '/advance/task_config',
                    data: jsonData
                    // data: $.param(jsonData),
                    // headers: {'Content-Type':'application/x-www-form-urlencoded'},
                    // transformRequest: angular.identity
                }).then(function successCallback(response) {
                    console.log(response.data);
                }, function errorCallback(response) {
                    // 请求失败执行代码
                    console.log("post data bad");
                });
            }

            $http({
                method: 'POST',
                url: '/advance/task_config',
                data: jsonData
                // data: $.param(jsonData),
                // headers: {'Content-Type':'application/x-www-form-urlencoded'},
                // transformRequest: angular.identity
            }).then(function successCallback(response) {
                console.log(response.data);
            }, function errorCallback(response) {
                // 请求失败执行代码
                console.log("post data bad");
            });

            console.log(id);
            // next step
            if (id === "miningrule") {
                //
                if ($scope.crawl_pattern === null ||
                    $scope.crawl_pattern === "单页") {
                    // same as last
                    if (downloadPagePath === $scope.url_path && downloadState === 3) {
                        downloadState = 3;
                        return;
                    } else {
                        downloadPagePath = $scope.url_path;
                        downloadState = 1;
                    }
                } else if ($scope.crawl_pattern === "列表") {
                    if (downloadPagePath === getPage($scope.url_wildcard, $scope.init_value)
                        && downloadState === 3) {
                        downloadState = 3;
                        return;
                    } else {
                        downloadPagePath = getPage($scope.url_wildcard, $scope.init_value);
                        downloadState = 1;
                    }
                } else if ($scope.crawl_pattern === "导入") {
                    if (downloadPagePath === jsonData.url_pattern.import.import_urls[0]
                        && downloadState === 3) {
                        downloadState = 3;
                        return;
                    } else {
                        downloadPagePath = jsonData.url_pattern.import.import_urls[0];
                        downloadState = 1;
                    }
                }
                // check file
                $.post("/collect/file/exist", {filename: hashCode(downloadPagePath) + ".html"},
                    function (result) {
                        // exits
                        if (result) {
                            downloadState = 3;
                        }
                    });
                console.log(downloadPagePath);
                /**
                 * Download page
                 */
                $.post('/collect/download', {url_path: downloadPagePath}, function (result) {
                    if (result === "success") {
                        downloadState = 3;
                    } else {
                        downloadState = 2;
                    }
                    console.log(result);
                });
                // $http({
                //     method: 'POST',
                //     url: '/collect/download',
                //     data: url_data
                // }).then(function successCallback(response) {
                //     console.log(response.toString());
                //
                //
                // }, function errorCallback(response) {
                //     // 请求失败执行代码
                //     console.log(response);
                //     downloadState = 2;
                // });
            }
        };

        $scope.pageTitle = "自定义采集模块";

        $scope.creep_rule = [];

        var downloadState = 0;
        var downloadPagePath;

        $scope.pageTitle = "自定义采集模块";

        $scope.del = function ($index) {
            if ($index >= 0) {
                $scope.creep_rule.splice($index, 1);
            }
        };

        var select_xpath1;
        var select_xpath2;
        var select_ajax_xpath;
        $scope.add = function () {
            $scope.creep_name = "";
            $scope.creep_pattern = "单体";
            $scope.x = false;
            $scope.ajax_pattern = "点击";
            $scope.button_xpath = "";
            $scope.attribute_xpath = "";
            $scope.attribute_xpath2 = "";
            $scope.attribute_name = "";
            $scope.extract_way = "文本";
            select_xpath1 = "";
            select_xpath2 = "";
            select_ajax_xpath = "";
            // 添加规则
            $('#modal-add').modal('show');
        };
        var lastTag = null;
        var lastTagBorder = null;
        var selectedTag = null;
        var selectedTagBorder = null;
        var index = 0;
        // 选择xpath
        $scope.select_xpath = function () {
            // check if download page is exist
            if (downloadState === 0) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("请先在URL规则中配置，点击下一步～～")
            }
            else if (downloadState === 1) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("正在下载～～请稍后～～")
            } else if (downloadState === 2) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("下载界面失败～请重新配置～")
            } else if (downloadState === 3){
                $('#modal-select-xpath').modal('show');
                index = 0;
                $('#xpath').val("");
                $("#iframe").attr("src", "http://localhost:8888/" + hashCode(downloadPagePath) + ".html");
                $('#modal-select-xpath').on('shown.bs.modal', function (e) {
                    $(this).click(function (event) {
                        event.preventDefault();
                    });
                    //对所有的元素添加点击事件，获取xpath
                    $("#iframe").contents().find("*").hover(function (event) {
                        event.stopPropagation();
                        if (lastTag !== null) {
                            lastTag.css('border', lastTagBorder);
                        }
                        lastTagBorder = $(this).css('border');
                        $(this).css({
                            'border': '1.5px solid #f0f',
                            'border-radius': '5px solid'
                        });
                        $(this).click(function (event) {
                            event.preventDefault();
                            if (index === 0) {
                                select_xpath1 = $shadow.domXpath(this);
                                // console.log($shadow.domXpath(this));
                                $('#xpath').val($shadow.domXpath(this));
                                // update selectedTag;
                                if (selectedTagBorder !== null) {
                                    selectedTag.css('border', "");
                                }
                                selectedTag = $(this);
                                selectedTagBorder = $(this).css('border');
                            }
                            index++;
                        });
                        lastTag = $(this);
                        index = 0;
                        selectedTag.css({
                            'border': '1.5px solid #8B0000',
                            'border-radius': '5px solid'
                        });
                    });
                });
            }
        };
        // xpath2
        $scope.select_xpath2 = function () {
            // check if download page is exist
            if (downloadState === 0) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("请先在URL规则中配置，点击下一步～～")
            }
            else if (downloadState === 1) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("正在下载～～请稍后～～")
            } else if (downloadState === 2) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("下载界面失败～请重新配置～")
            } else if (downloadState === 3) {
                $('#modal-select-xpath2').modal('show');
                // console.log("-------------------------------\n" + $type  + "-------------------------------\n");
                $('#xpath2').val("");
                $("#iframe2").attr("src", "http://localhost:8888/" + hashCode(downloadPagePath) + ".html");
                index = 0;
                $('#modal-select-xpath2').on('shown.bs.modal', function (e) {
                    $(this).click(function (event) {
                        event.preventDefault();
                    });
                    //对所有的元素添加点击事件，获取xpath
                    $("#iframe2").contents().find("*").hover(function (event) {
                        event.stopPropagation();
                        // lastTag = $(this);
                        // var css = div.css('border');
                        // console.log($(this).css('border'));
                        if (lastTag !== null) {
                            lastTag.css('border', lastTagBorder);
                        }
                        lastTagBorder = $(this).css('border');
                        //console.log($(this));
                        $(this).css({
                            'border': '1.5px solid #f0f',
                            'border-radius': '5px solid'
                        });
                        $(this).click(function (event) {
                            event.preventDefault();
                            if (index === 0) {
                                // get select
                                select_xpath2 = $shadow.domXpath(this);
                                // console.log($shadow.domXpath(this));
                                $('#xpath2').val($shadow.domXpath(this));
                                // update selectedTag
                                if (selectedTagBorder !== null) {
                                    selectedTag.css('border', "");
                                }
                                selectedTag = $(this);
                                selectedTagBorder = $(this).css('border');
                            }
                            index++;
                        });
                        lastTag = $(this);
                        index = 0;
                        selectedTag.css({
                            'border': '1.5px solid #8B0000',
                            'border-radius': '5px solid'
                        });
                    });
                });
            }
        };

        // 选择ajax_xpath
        $scope.select_ajax_xpath = function () {
            // check if download page is exist
            if (downloadState === 0) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("请先在URL规则中配置，点击下一步～～")
            }
            else if (downloadState === 1) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("正在下载～～请稍后～～")
            } else if (downloadState === 2) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("下载界面失败～请重新配置～")
            } else if (downloadState === 3) {
                $('#modal-select-ajax-xpath').modal('show');
                //console.log("-------------------------------\n" + $type  + "-------------------------------\n");
                $('#ajax_xpath').val("");
                $("#iframe3").attr("src", "http://localhost:8888/" + hashCode(downloadPagePath) + ".html");
                $('#modal-select-ajax-xpath').on('shown.bs.modal', function (e) {
                    $(this).click(function (event) {
                        event.preventDefault();
                    });
                    //对所有的元素添加点击事件，获取xpath
                    $("#iframe3").contents().find("*").hover(function (event) {
                        event.stopPropagation();
                        if (lastTag !== null) {
                            lastTag.css('border', lastTagBorder);
                        }
                        lastTagBorder = $(this).css('border');
                        $(this).css({
                            'border': '1.5px solid #f0f',
                            'border-radius': '5px solid'
                        });
                        $(this).click(function (event) {
                            event.preventDefault();
                            if (index === 0) {
                                select_ajax_xpath = $shadow.domXpath(this);
                                // console.log($shadow.domXpath(this));
                                $('#ajax_xpath').val($shadow.domXpath(this));
                                // update selectedTag
                                if (selectedTagBorder !== null) {
                                    selectedTag.css('border', "");
                                }
                                selectedTag = $(this);
                                selectedTagBorder = $(this).css('border');
                            }
                            index++;
                        });
                        index = 0;
                        lastTag = $(this);
                        selectedTag.css({
                            'border': '1.5px solid #8B0000',
                            'border-radius': '5px solid'
                        });
                    });
                });
            }
        };

        /**
         * Check Download State
         */
        function checkDownloadState() {
            // check if download page finishing
            if (downloadState === 0) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("请先下载～～")
            }
            else if (downloadState === 1) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("正在下载～请稍后～～")
            } else if (downloadState === 2) {
                $('#download_page_loading').modal('show');
                $('#download_page_loading_info').text("下载界面失败～请请重新配置～")
            }
        }
        //　保存
        $scope.select_commit1 = function () {
            $scope.button_xpath = select_ajax_xpath;
        };
        $scope.select_commit2 = function () {

            $scope.attribute_xpath = select_xpath1;
        };
        $scope.select_commit3 = function () {
            $scope.attribute_xpath2 = select_xpath2;
        };


        $scope.save = function ($index) {

            if($scope.isModify)
            {
                $scope.isModify = false;
                $scope.modify($index);
                return;
            }
            var newEle = {
                "creep_name": $scope.creep_name,
                "creep_pattern": $scope.creep_pattern,
                "ajax": {
                    "open": $scope.x,
                    "ajax_pattern": $scope.ajax_pattern,
                    "button_xpath": $scope.button_xpath
                },
                "attribute_xpath": $scope.attribute_xpath,
                "attribute_xpath2": $scope.attribute_xpath2,
                "attribute_name": $scope.attribute_name,
                "extract_way": $scope.extract_way
            };
            $scope.creep_rule.push(newEle);

            $('#modal-add').modal('hide');
        };

        $scope.update = function ($index) {
            $scope.isModify = true;
            $scope.t_index = $index;
            $scope.creep_name = $scope.creep_rule[$index].creep_name;
            $scope.creep_pattern = $scope.creep_rule[$index].creep_pattern;
            $scope.x = $scope.creep_rule[$index].ajax.open;
            $scope.ajax_pattern = $scope.creep_rule[$index].ajax.ajax_pattern;
            $scope.button_xpath = $scope.creep_rule[$index].ajax.button_xpath;
            $scope.attribute_xpath = $scope.creep_rule[$index].attribute_xpath;
            $scope.attribute_xpath2 = $scope.creep_rule[$index].attribute_xpath2;
            $scope.attribute_name = $scope.creep_rule[$index].attribute_name;
            $scope.extract_way = $scope.creep_rule[$index].extract_way;
            $('#modal-add').modal('show');
        };

        $scope.modify = function ($index) {
            $scope.creep_rule[$index].creep_name = $scope.creep_name;
            $scope.creep_rule[$index].creep_pattern = $scope.creep_pattern;

            var ajaxTypes = ["点击", "翻页", "滚动"];
            $scope.creep_rule[$index].ajax.open = $scope.x;
            $scope.creep_rule[$index].ajax.ajax_pattern = ajaxTypes[$scope.y];
            $scope.creep_rule[$index].ajax.button_xpath = $scope.button_xpath;
            $scope.creep_rule[$index].attribute_xpath = $scope.attribute_xpath;
            $scope.creep_rule[$index].attribute_xpath2 = $scope.attribute_xpath2;
            $scope.creep_rule[$index].attribute_name = $scope.attribute_name;
            $scope.extract_way = $scope.creep_rule[$index].extract_way;
            $('#modal-update').modal('hide');
        };

        // 测试
        $scope.test = function($index) {
            $('#loading').modal('show');

            // var target = document.getElementById('loading_spinner');
            // new Spinner({color:'#fff', lines: 12}).spin(target);

            var url_path = $scope.url_path;
            var data = $scope.creep_rule[$index];
            data['url_path'] = downloadPagePath;
            var req = JSON.stringify(data);
            //alert(req.toString());
            // cun
            if ($scope.creep_rule[$index].creep_pattern === "线索") {
                $.post("/collect/crawler", {data: req}, function (result) {
                    console.log(result.toString());
                    $('#testarea').val(result);
                    $('#loading').modal('hide');
                });
            }
            else if ($scope.creep_rule[$index].creep_pattern === "单体") {
                var params = $("#crawlrule").serializeArray();
                var values = {};
                for (x in params) {
                    values[params[x].name] = params[x].value;
                }
                values['currenturl'] = url_path;
                values['task_id']=  $scope.selected_task.task_id;
                values['project_id'] = $scope.selected_project.advanceProjectEntity.project_id;
                var idata = JSON.stringify(values);
                console.log(idata.toString());
                CollectCusTempService.crawltest(idata);

            }
        };

        // 创建项目
        $scope.createAdvanceProject = function () {

            if ($scope.new_project_name != "") {

                for (var i = 0; i < $scope.projects.length; i++) {
                    if ($scope.projects[i].advanceProjectEntity.project_name == $scope.new_project_name) {
                        //alert("项目已经存在");
                        return;
                    }
                }

                $http({
                    method: 'POST',
                    url: '/advance/createProject',
                    data: {
                        project_name: $scope.new_project_name
                    }
                }).then(function successCallback(response) {
                    console.log(response.data.newProject);
                    $scope.projects.push(response.data.newProject);
                    $scope.selected_project = $scope.projects[$scope.projects.length - 1];
                    location.reload();
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
            if ($scope.new_task_name != "") {
                for (var i = 0; i < $scope.selected_project.advanceTaskEntities.length; i++) {
                    if ($scope.selected_project.advanceTaskEntities[i].task_name == $scope.new_task_name) {
                        //alert("任务已经存在");
                        return;
                    }
                }
                $http({
                    method: 'POST',
                    url: '/advance/createTask',
                    data: {
                        project_id: $scope.selected_project.advanceProjectEntity.project_id,
                        task_name: $scope.new_task_name
                    }
                }).then(function successCallback(response) {


                    for (var i = 0; i < $scope.projects.length; i++) {
                        if ($scope.projects[i].advanceProjectEntity.project_id == response.data.project_id) {
                            $scope.projects[i].advanceTaskEntities.push(response.data.newTask);
                            $scope.selected_project = $scope.projects[i];
                            tasks = $scope.selected_project.advanceTaskEntities;
                            $scope.selected_task = tasks[tasks.length - 1];

                            var jsonData = {

                                "basic_rule": {
                                    "project_id": $scope.selected_project.advanceProjectEntity.project_id,
                                    "task_id": $scope.selected_task.task_id,
                                    "task_leader": "",
                                    "task_description": ""
                                },

                                "assistant_rule": {
                                    "open": false,
                                    "login_page_path": "",
                                    "login_username": "",
                                    "login_username_xpath": "",
                                    "login_password": "",
                                    "login_password_xpath": "",
                                    "login_verifycode": "",
                                    "login_verifycode_xpath": "",
                                    "cookie": ""
                                },

                                "url_pattern": {

                                    "current_selected": "单页",

                                    "single": {

                                        // "single_url_pattern_name": "abc",
                                        "url_path": ""

                                    },
                                    "list": {
                                        // "list_url_pattern_name": "abc",
                                        "url_wildcard": "",
                                        "init_value": 0,
                                        "gap": 0,
                                        "pages_num": 0,
                                        "list_url_file_path": ""
                                    },


                                    "click": {
                                        // "click_url_pattern_name": "abc",
                                        "url_index_path": "",
                                        "next_page_xpath": "",
                                        "click_url_file_path": ""
                                    },

                                    "import": {
                                        // "import_url_pattern_name": "abc",
                                        "import_urls": [],
                                        "file_upload_path": "",
                                        "import_url_file_path": ""
                                    }

                                },

                                "creep_rule":
                                    [],

                                "store_rule": {
                                    "store_pattern": "文件"
                                },

                                "run_rule": {
                                    "proxy_ids": [],
                                    "time": {
                                        "start_time": "",
                                        "end_time": ""
                                    },
                                    "headers": "",
                                    "custom_config": ""
                                }

                            };
                            $http({
                                method: 'POST',
                                url: '/advance/task_config',
                                data: jsonData
                            }).then(function successCallback(response) {
                                console.log(response.data);
                                location.reload();
                            }, function errorCallback(response) {
                                // 请求失败执行代码
                                console.log("post data bad");
                            });

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

        $scope.changeTemplate = function (tableId) {
            $scope.tabId = tableId;
            if (tableId == 1) {
                $scope.isadvanced = false;
                $scope.usertemplateStr = angular.toJson($scope.temp1);
            } else if (tableId == 2) {
                $scope.isadvanced = false;
                $scope.usertemplateStr = angular.toJson($scope.temp2);
            } else if (tableId == 3) {
                $scope.isadvanced = false;
                $scope.usertemplateStr = angular.toJson($scope.temp3);
            } else if (tableId == 4) {
                $scope.isadvanced = true;
                $scope.side_index = 0;
            }

            initTemplate();
        };

        /**
         * Changes the tab in the advanced template
         * @param tabIndex The index of the destination tab
         */
        $scope.changeTab_advancedTemplate = function (tabIndex) {
            switch (tabIndex) {
                // The editing tab
                case 0: {
                    $scope.side_index = 0;

                    break;
                }

                // The importing tab
                case 1: {
                    $scope.side_index = 1;

                    break;
                }

                // The exporting tab
                case 2: {
                    $scope.side_index = 2;

                    break;
                }

                // The monitoring tab
                case 3: {
                    $scope.side_index = 3;

                    break;
                }

                // Illegal input
                default: {
                    console.log("changeTab_advancedTemplate: The parameter tabIndex cannot be " + tabIndex + "!");

                    break;
                }
            }
        };
        /**
         *  Imports the configuration data from the file into the editing area and change the tab
         */
        $scope.importIntoEditingTab = function () {
            $scope.side_index = 0;

            // Change the data displayed in the web page
            changeFrontData(angular.fromJson($scope.import_result_content));

            // TODO: Update the data in the Angular level: select_project, select_task, ...
        };

        /**
         * Exports the configuration json file to the local
         */
        $scope.exportConfigJson_advancedTemplate = function () {
            var id = $scope.selected_task_exporting;
            console.log(id);

            window.open("/advance/file/task_config?task_id=" + id.toString(), "_blank");

            // $http({
            //     url: '/advance/task_config?task_id=' + id,
            //     method: "GET"
            // }).success(function (data, status, headers, config) {
            //     console.log("get task config json success...");
            //
            //     var blob = new Blob([angular.toJson(data)], {type: "text/plain"});
            //     var objectUrl = (window.URL || window.webkitURL).createObjectURL(blob);
            //     var a = document.createElement('a');
            //     document.body.appendChild(a);
            //     a.setAttribute('style', 'display:none');
            //     a.setAttribute('href', objectUrl);
            //     var filename = "config_" + id.toString() + ".json";
            //     a.setAttribute('download', filename);
            //     console.log(a);
            //     a.click();
            //     document.body.removeChild(a);
            //     URL.revokeObjectURL(objectUrl);
            // }).error(function (data, status, headers, config) {
            //     console.log("get task config json error..." + status);
            // });
        };

        // For importing file from the local machine
        var form = document.forms.namedItem("fileinfo");
        form.addEventListener('submit', function (ev) {

            // var oOutput = document.querySelector("div");
            var oOutput = $("#import_result");
            var oData = new FormData(form);

            var configJsonFile = $("#config_json_file")[0].files[0];
            console.log("configJsonFile.toString() = \"" + configJsonFile.toString() + "\".");
            var reader = new FileReader();
            reader.onload = function () {
                console.log("reader.onload() called.");
                console.log("this.result = \"" + this.result + "\".");
                console.log("configJsonFile.size = " + configJsonFile.size + ".");
                document.getElementById("import_result").innerHTML = this.result;
                $scope.import_result_content = this.result;
            };
            reader.readAsText(configJsonFile);

            // $scope.side_index = 0;

            // oData.append("CustomField", "This is some extra data");

            // var oReq = new XMLHttpRequest();
            // oReq.open("POST", "stash.php", true);
            // oReq.onload = function(oEvent) {
            //     if (oReq.status == 200) {
            //         oOutput.innerHTML = "Uploaded!";
            //     } else {
            //         oOutput.innerHTML = "Error " + oReq.status + " occurred when trying to upload your file.<br \/>";
            //     }
            // };
            //
            // oReq.send(oData);
            ev.preventDefault();
        }, false);

        $scope.reset = function () {
            //刷新页面，重新加载
            $state.go('collect_listpage', {}, {reload: true});
        };

        $scope.ajaxType = false;
        $scope.ajaxXpath = false;
        $scope.changeifHide = function (x) {
            if (x == 0) {
                $scope.ajaxType = false;
                $scope.ajaxXpath = false;
            }
            else if (x == 1) {
                $scope.ajaxType = true;
                $scope.ajaxXpath = true;
            }
        };

        $scope.changeXpath = function (x) {
            if (x == 0) {
                $scope.ajaxXpath = false;
            }
            else if (x == 1) {
                $scope.ajaxXpath = true;
            }

        };

        $scope.testCrawler = function () {
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
            for (x in params) {
                values[params[x].name] = params[x].value;
            }
            var idata = JSON.stringify(values)
            console.log(idata.toString());
            CollectCusTempService.crawltest(idata);
        };

        // 单页采集模板
        $scope.temp1 = {
            "name": "模板名称",
            "description": "描述",
            "type": "user-singlepage",
            "domain": "",
            "cookie": "",
            "interval": 3000,
            "entryUrls": ["入口链接1", "入口链接2"],
            // "depth":1,
            "contentUrlRegex": "详情页校验",
            "fields": [
                {
                    "name": "info_1",
                    "contentXpath": "xpath获取info_1",
                    "require": true
                },
                {
                    "name": "info_2",
                    "contentXpath": "xpath获取info_2",
                    "require": false
                },
                {
                    "name": "info_3",
                    "contentXpath": "xpath获取info_3",
                    "require": true
                }
            ]
        };
        // 分页采集模板
        $scope.temp2 = {
            "name": "模板名称",
            "description": "描述",
            "type": "user-listpage",
            "domain": "",
            "cookie": "",
            "interval": 3000,
            "entryUrls": ["入口链接1", "入口链接2"],
            // "depth":2,
            "nextUrlXpath": "xpth获取下一页",
            "contentUrlXpath": "xpth获取详情页",
            "listUrlRegex": "列表页校验",
            "contentUrlRegex": "详情页校验",
            "fields": [
                {
                    "name": "info_1",
                    "contentXpath": "xpath获取info_1",
                    "require": true
                },
                {
                    "name": "info_2",
                    "contentXpath": "xpath获取info_2",
                    "require": false
                },
                {
                    "name": "info_3",
                    "contentXpath": "xpath获取info_3",
                    "require": true
                }
            ]
        };

        // 分层采集
        $scope.temp3 = {
            "name": "模板名称",
            "description": "描述",
            "type": "user-multipage",
            "domain": "",
            "cookie": "",
            "interval": 3000,
            "entryUrls": ["入口链接1", "入口链接2"],
            "depth": 3,
            "contentUrlRegex": "详情页校验",
            "fields": [
                {
                    "name": "info_1",
                    "contentXpath": "xpath获取info_1",
                    "require": true
                },
                {
                    "name": "info_2",
                    "contentXpath": "xpath获取info_2",
                    "require": false
                },
                {
                    "name": "info_3",
                    "contentXpath": "xpath获取info_3",
                    "require": true
                }
            ]
        };

        $scope.usertemplateStr = angular.toJson($scope.temp1);

        $scope.createTemplate = function () {
            //检测模板是否符合json规范
            try {
                angular.fromJson($scope.usertemplateStr);
            } catch (exception) {
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
        };

        //初始化模板数据
        var initTemplate = function () {
            var content = $scope.usertemplateStr;
            var result = '';
            if (content != '') {

                try {
                    current_json = jsonlint.parse(content);
                    current_json_str = JSON.stringify(current_json);
                    //current_json = JSON.parse(content);
                    result = new JSONFormat(content, 4).toString();
                } catch (e) {
                    result = '<span style="color: #f1592a;font-weight:bold;">' + e + '</span>';
                    current_json_str = result;
                }

                $('#json-target').html(result);
            } else {
                $('#json-target').html('');
            }
        };

        // 解析json(等时间充裕改写成angularjs语法)
        var current_json = '';
        var current_json_str = '';
        var xml_flag = false;
        var zip_flag = false;
        var shown_flag = false;
        $('.tip').tooltip();
        $('#json-src').keyup(function () {
            initTemplate();
        });

        $('.shown').click(function () {
            if (!shown_flag) {
                readerLine();
                $('#json-src').attr("style", "height:553px;padding:0 10px 10px 40px;border:0;border-right:solid 1px #ddd;border-bottom:solid 1px #ddd;border-radius:0;resize: none; outline:none;");
                $('#json-target').attr("style", "padding:0px 50px;");
                $('#line-num').show();
                $('.numberedtextarea-line-numbers').show();
                shown_flag = true;
                $(this).attr('style', 'color:#15b374;');
            } else {
                $('#json-src').attr("style", "height:553px;padding:0 10px 10px 20px;border:0;border-right:solid 1px #ddd;border-bottom:solid 1px #ddd;border-radius:0;resize: none; outline:none;");
                $('#json-target').attr("style", "padding:0px 20px;");
                $('#line-num').hide();
                $('.numberedtextarea-line-numbers').hide();
                shown_flag = false;
                $(this).attr('style', 'color:#999;');
            }
        });

        function readerLine() {
            var line_num = $('#json-target').height() / 20;
            $('#line-num').html("");
            var line_num_html = "";
            for (var i = 1; i < line_num + 1; i++) {
                line_num_html += "<div>" + i + "<div>";
            }
            $('#line-num').html(line_num_html);
        }

        $('#json-src').keyup();

        $scope.$on('$viewContentLoaded', function () {
            if ($stateParams.tabId > 0) {
                //console.log("初始化方法")
                $cookieStore.put('refreshPageParam', $stateParams);
            }
            //console.log("初始化获取tabId");
            $scope.tabId = $cookieStore.get('refreshPageParam').tabId;
            $scope.changeTemplate($scope.tabId);
            //console.log("初始化完毕");
            $scope.active = {
                "tab1": $scope.tabId == 1,
                "tab2": $scope.tabId == 2,
                "tab3": $scope.tabId == 3,
                "tab4": $scope.tabId == 4

            };
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

// second method of getting xpath
var $shadow = new Object();
/**
 获取元素的xpath
 特性：
 - 转换xpath为csspath进行jQuery元素获取
 - 仅生成自然表述路径（不支持非、或）
 @param dom {jQuery} 目标元素
 @returns {String} dom的xpath路径
 */
$shadow.domXpath = function(dom) {
    dom = $(dom).get(0);
    var path = "";
    var currentTagName = "";
    var isExistId = false;
    for (; dom && dom.nodeType == 1; dom = dom.parentNode) {
        var index = 1;
        for (var sib = dom.previousSibling; sib; sib = sib.previousSibling) {
            if (sib.nodeType == 1 && sib.tagName == dom.tagName) {
                index++;
            }
        }
        currentTagName = dom.tagName.toLowerCase();
        // check whether id is exist
        if (dom.id) {
            currentTagName = "//*[@id=\"" + dom.id + "\"]";
            isExistId = true;
            break;
        } else {
            if (index > 0)
                currentTagName += "[" + index + "]";
        }
        path = "/" + currentTagName + path;
    }
    if (isExistId) {
        path = currentTagName + path;
    }
    path = path.replace("html[1]/body[1]/","html/body/");
    return path;
};

/**
 根据xpath获取元素
 特性：
 - 转换xpath为css path进行jQuery元素获取
 - 仅支持自然表述（不支持非、或元素选取）
 @param xpath {String} 目标元素xpath
 @returns {jQuery Object} 元素/元素集合
 */
$shadow.xpathDom = function(xpath) {
    // 开始转换 xpath 为 css path
    // 转换 // 为 " "
    xpath = xpath.replace(/\/\//g, " ");
    // 转换 / 为 >
    xpath = xpath.replace(/\//g, ">");
    // 转换 [elem] 为 :eq(elem) ： 规则 -1
    xpath = xpath.replace(/\[([^@].*?)\]/ig, function(matchStr,xPathIndex)  {
        var cssPathIndex = parseInt(xPathIndex)-1;
        return ":eq(" + cssPathIndex + ")";
    });
    // 1.2 版本后需要删除@
    xpath = xpath.replace(/\@/g, "");
    // 去掉第一个 >
    xpath = xpath.substr(1);
    //alert(xpath);
    // 返回jQuery元素
    return $(xpath);
};

// 获取xpath
function readXPath(element) {
    if (element.id !== "") { //判断id属性，如果这个元素有id，则显 示//*[@id="xPath"]  形式内容
        if (element.id == undefined) {
            return "";
        }
        console.log(element.id);
        return '//*[@id=\"' + element.id + '\"]';
    }
    //这里需要需要主要字符串转译问题，可参考js 动态生成html时字符串和变量转译（注意引号的作用）
    if (element == document.body) {//递归到body处，结束递归
        return '/html/' + element.tagName.toLowerCase();
    }
    var ix = 1,//在nodelist中的位置，且每次点击初始化
        siblings = element.parentNode.childNodes;//同级的子元素

    for (var i = 0, l = siblings.length; i < l; i++) {
        var sibling = siblings[i];
        //如果这个元素是siblings数组中的元素，则执行递归操作
        if (sibling == element) {
            return arguments.callee(element.parentNode) + '/' + element.tagName.toLowerCase() + '[' + (ix) + ']';
            //如果不符合，判断是否是element元素，并且是否是相同元素，如果是相同的就开始累加
        } else if (sibling.nodeType == 1 && sibling.tagName == element.tagName) {
            ix++;
        }
    }
}

/**
 * Get the host
 * @param sUrl
 * @returns {string}
 */
function getHost(sUrl) {
    var sDomain = '';
    var rDomain = /([a-zA-Z0-9-]+)(.com\b|.net\b|.edu\b|.miz\b|.biz\b|.cn\b|.cc\b|.org\b){1,}/g;
    if (sUrl !== "") {
        sDomain = sUrl.match(rDomain);
    }
    return sDomain;
}

/**
 * Check file is exist
 * @param filename
 */
function exist(filename) {
    $.post("/collect/file/exist", { filename:　filename}, function(result){
        console.log(result.toString());
        return result;
    });
}

/**
 * Get hashCode
 * @param str
 * @returns {number}
 */
hashCode = function(str){
    var hash = 0;
    if (str.length === 0) return hash;
    for (let i = 0; i < str.length; i++) {
        var char2 = str.charCodeAt(i);
        hash = ((hash<<5) - hash) + char2;
        hash = hash & hash;  // Convert to 32bit integer
    }
    console.log(hash);
    return hash;
}

/**
 * Get page from list pages
 * @param page
 * @param intiValue
 * @returns {string}
 */
getPage = function(page, intiValue) {
    var html = page.substring(0, page.indexOf("{"))
    html = html + intiValue
    return html
}