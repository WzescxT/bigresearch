angular.module('MetronicApp').controller('AnalysisDBController',function($rootScope,anchorScroll,$scope,$state,$modal,AnalysisDBService,$timeout,uibPaginationConfig) {
    $scope.pageTitle="数据库可视化分析";



//初始化uibpaginationConfig，汉化
    uibPaginationConfig.firstText="< <";
    uibPaginationConfig.lastText="> >";
    uibPaginationConfig.nextText=" > ";
    uibPaginationConfig.previousText=" < ";
    uibPaginationConfig.itemsPerPage=10;
    $scope.pageConfig={"itemsPerPage":uibPaginationConfig.itemsPerPage,"bigTotalItems":0,"maxSize":8,"bigCurrentPage":1};







//初始化数据
//获取数据库列表
    $scope.getUserLibraries =function(){

        AnalysisDBService.getUserLibraries().then(function(resp) {
            if (resp.error_code == 0) {
                $scope.textLibraries=resp.rt_info;
                angular.forEach($scope.textLibraries, function(data,index,array){

                    $scope.treedata[0].children[index]={label: data.name,id:data.id, type: "doc"};

                });

                //默认选中第一个
                $scope.selectedNode = $scope.treedata[0].children[0];

                //获取列名
                $scope.getLibraryColumns();
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })

    }





//获取字段
    $scope.getLibraryColumns=function(){
        $scope.queryParam={"id":$scope.selectedNode.id};
        if($scope.selectedNode.id==null||$scope.selectedNode.id==''){
            return ;
        }
        AnalysisDBService.getLibraryColumns($scope.queryParam).then(function(resp) {
            console.log(resp);
            if (resp.error_code == 0) {
                $scope.SQL.columns=angular.fromJson(resp.rt_info);
                //暂不添加id
                //$scope.SQL.columns.id=false;
                initSQL();
            } else {
                sweetAlert("error", resp.error_msg, "error");
            }
        }, function(resp) {
        })
    }







    /*    $scope.treedata=[
            {label: "Documents", type: "folder", children: [
                {label: "a picture", type: "pic"},
                {label: "another picture", type: "pic"},
                {label: "a doc", type: "doc"},
                {label: "a file", type: "file"},
                {label: "a movie", type: "movie"}
            ]},
            {label: "My Computer", type: "folder", children: [
                {label: "email", type: "email"},
                {label: "home", type: "home"}
            ]},
            {label: "trash", type: "trash"}

        ];*/


    //http://www.jq22.com/jquery-info5757

    $scope.treedata=[
        {label: "文本库列表", id:0,type: "folder", children: [
            {label: "", id:"",type: "doc"}

        ]}
    ];


    //默认全部展开
    $scope.expandedNodes = [$scope.treedata[0]];
    //展示选中
    $scope.showSelected = function(sel) {
        console.log(sel);
        $scope.selectedNode = sel;
        if(sel.id<=0){
            sweetAlert("error", "请选择有效数据库节点", "error");
        }
       $scope.getLibraryColumns();
    };


    $scope.SQL={type:1,value:[],columns:[],conditions:[]};

//初始化sql操作
    var initSQL=function(){

        console.log($scope.SQL.columns);
        console.log($scope.SQL);




//初始化条件
        $scope.condition={columns:[],compare:[],relation:[]};

        $scope.condition.columns=$scope.SQL.columns;
        console.log("===>condition.columns");
        console.log($scope.condition.columns);
        console.log("===>SQL.columns");
        console.log($scope.SQL.columns);

        $scope.condition.compare=[{'name':'等于','value':"1"},{'name':'不等于','value':"2"},{'name':'包含','value':"3"},{'name':'不包含','value':"4"}];

        $scope.condition.relation = [{'name':'并且','value':'1'},{'name':'或者','value':'2'}];

//设置初始值
        for(var key in $scope.condition.columns){
            $scope.tempColumn= key;
            break;

        }
        //$scope.tempColumn=$scope.condition.columns[0];




        console.log($scope.tempColumn);
        $scope.tempCompare=$scope.condition.compare[0].value;
        $scope.tempRelation=$scope.condition.relation[0].value;
        $scope.tempValue='';
        $scope.oneItem={column:$scope.tempColumn,compare:$scope.tempCompare,value:$scope.tempValue,relation:$scope.tempRelation};
        $scope.SQL.conditions=[$scope.oneItem];



        //设置参数
        $scope.SQL.value=[{'name':$scope.tempColumn,value:''}];
        console.log("===>SQL.value:");
        console.log($scope.SQL.value);
    }


    //新填一个条件
    $scope.addItem = function(){

        var item = {column:$scope.tempColumn,compare:$scope.tempCompare,value:$scope.tempValue,relation:$scope.tempRelation};

        $scope.SQL.conditions.push(item);
    }

    $scope.delItem = function(index){
        $scope.SQL.conditions.splice(index,1);
    }


    //添加一个修改值

    //新填一个条件
    $scope.addValueItem = function(){

        var item = {'name':$scope.tempColumn,'value':''};

        $scope.SQL.value.push(item);
    }


    $scope.delValueItem = function(index){
        $scope.SQL.value.splice(index,1);
    }






//监听条件 组装sql
    $scope.$watch('SQL',  function(newValue, oldValue) {
        if (newValue === oldValue) { return; } // AKA first run
        //查询
        if ($scope.SQL.type==1){
            $scope.sqlStr='select';
//不含列
            var bol=false;
            for(key in $scope.SQL.columns){
                if($scope.SQL.columns[key]==true){
                    bol=true;
                    $scope.sqlStr=$scope.sqlStr+ ' '+key+' ,';
                }
            }
            if(bol){
                $scope.sqlStr=$scope.sqlStr+ ' '+'id ' ;
                //$scope.sqlStr=$scope.sqlStr.substring(0, $scope.sqlStr.length-1);
            } else {
                $scope.sqlStr=$scope.sqlStr+' * ';

            }

            $scope.sqlStr=$scope.sqlStr+' from  '+' analysis_textlibrary_info_'+$scope.selectedNode.id+' ';


            //if($scope.SQL.conditions.length!=0){
            //    $scope.sqlStr=$scope.sqlStr+' where ';
            //    angular.forEach($scope.SQL.conditions, function(data){
            //        $scope.sqlStr=$scope.sqlStr+' '+data.column+' ';
            //        if(data.compare=='1'){
            //            $scope.sqlStr=$scope.sqlStr+'= ' +'\''+data.value+'\'';
            //        }else if(data.compare=='2'){
            //            $scope.sqlStr=$scope.sqlStr+'!= ' +'\''+ data.value+'\'';
            //        }else if(data.compare=='3'){
            //            $scope.sqlStr=$scope.sqlStr+' like \'%'+data.value+'%\'';
            //        }else if(data.compare=='4'){
            //            $scope.sqlStr=$scope.sqlStr+' not  like \'%'+data.value+'%\'';
            //        }
            //
            //
            //        if(data.relation=='1'){
            //            $scope.sqlStr=$scope.sqlStr+' and ';
            //        }else if(data.relation=='2'){
            //            $scope.sqlStr=$scope.sqlStr+' or ';
            //        }
            //
            //
            //    });
            //    $scope.sqlStr=$scope.sqlStr.substring(0, $scope.sqlStr.length-5);
            //
            //    console.log($scope.sqlStr);
            //}


        }

        //统计
        if($scope.SQL.type==2){
            $scope.sqlStr='select count(id)';
            $scope.sqlStr=$scope.sqlStr+' from  '+' analysis_textlibrary_info_'+$scope.selectedNode.id+' ';
        }

        //删除
        if($scope.SQL.type==4){
            $scope.sqlStr='delete ';
            $scope.sqlStr=$scope.sqlStr+' from  '+' analysis_textlibrary_info_'+$scope.selectedNode.id+' ';

        }

        //修改
        if($scope.SQL.type==3){
            $scope.sqlStr='update analysis_textlibrary_info_'+$scope.selectedNode.id+' ';
            if($scope.SQL.value.length!=0){

                $scope.sqlStr = $scope.sqlStr +' set ';
                angular.forEach($scope.SQL.value,function(data){
                    $scope.sqlStr = $scope.sqlStr +' '+data.name+' ='+ ' \''+data.value+'\', ';
                });

                $scope.sqlStr=$scope.sqlStr.substring(0, $scope.sqlStr.length-2);
            }

        }

        //插入
        if($scope.SQL.type==5){
            $scope.sqlStr='insert into analysis_textlibrary_info_'+$scope.selectedNode.id+' ';

            if($scope.SQL.value.length!=0){

                $scope.sqlStr = $scope.sqlStr +' ( ';
                angular.forEach($scope.SQL.value,function(data){
                    $scope.sqlStr = $scope.sqlStr + ' '+data.name+' , ';
                });
                $scope.sqlStr=$scope.sqlStr.substring(0, $scope.sqlStr.length-2);

                $scope.sqlStr = $scope.sqlStr +' ) values ( ';

                angular.forEach($scope.SQL.value,function(data){
                    $scope.sqlStr = $scope.sqlStr + ' \''+data.value+'\' , ';
                });
                $scope.sqlStr=$scope.sqlStr.substring(0, $scope.sqlStr.length-2);
                $scope.sqlStr = $scope.sqlStr +' ) ';

            }


        }


        //拼接条件语句
        if($scope.SQL.type!=5 ){

            if($scope.SQL.conditions.length!=0){
                $scope.sqlStr=$scope.sqlStr+' where ';
                angular.forEach($scope.SQL.conditions, function(data){
                    $scope.sqlStr=$scope.sqlStr+' '+data.column+' ';
                    if(data.compare=='1'){
                        $scope.sqlStr=$scope.sqlStr+'= ' +'\''+data.value+'\'';
                    }else if(data.compare=='2'){
                        $scope.sqlStr=$scope.sqlStr+'!= ' +'\''+ data.value+'\'';
                    }else if(data.compare=='3'){
                        $scope.sqlStr=$scope.sqlStr+' like \'%'+data.value+'%\'';
                    }else if(data.compare=='4'){
                        $scope.sqlStr=$scope.sqlStr+' not  like \'%'+data.value+'%\'';
                    }


                    if(data.relation=='1'){
                        $scope.sqlStr=$scope.sqlStr+' and ';
                    }else if(data.relation=='2'){
                        $scope.sqlStr=$scope.sqlStr+' or ';
                    }


                });
                $scope.sqlStr=$scope.sqlStr.substring(0, $scope.sqlStr.length-5);

                console.log($scope.sqlStr);
            }
        }




    },true);















    //分页查询
    //获取json中的列名
    var getColumnNames=function(jsonObj){
        var res=[];
        for(var key in jsonObj){
            res.push(key);
        }
        return res;
    }



    $scope.excuteSql=function(){
        $scope.param={"textLibraryId":$scope.selectedNode.id,"type":$scope.SQL.type,"sqlStr":$scope.sqlStr,"pageNow":$scope.pageConfig.bigCurrentPage,"pageSize":uibPaginationConfig.itemsPerPage};
        console.log($scope.param);
        AnalysisDBService.excuteSql($scope.param).then(function(resp) {
            if (resp.error_code == 0) {
                $scope.results=resp.rt_mapinfo.results;
                $scope.pageConfig.bigTotalItems=resp.rt_mapinfo.bigTotalItems;

                if($scope.SQL.type=='1'){
                    if($scope.pageConfig.bigTotalItems==0){
                        sweetAlert("没有查到数据", "","success");
                    }
                }else if($scope.SQL.type!='1'){

                    sweetAlert("success", resp.rt_msg, "success");

                }
                $scope.columns=getColumnNames($scope.results[0]);


            } else {
                sweetAlert("error", resp.error_msg, "error");

            }
        }, function(resp) {
        })

    }


















    $scope.$on('$viewContentLoaded', function() {

        $scope.getUserLibraries();

    });

//metronic bug 无法初始化checkbox和radio的初始值样式，通过一下这句初始化。只通过双向绑定无法解决。
    $timeout(jQuery.uniform.update, 0);




})


