package com.monetware.controller;

import com.monetware.mapper.collect.AdvanceCollectMapper;
import com.monetware.model.collect.AdvanceProjectEntity;
import com.monetware.model.collect.AdvanceProjectModel;
import com.monetware.model.collect.AdvanceTaskEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/advance")
@Controller
public class AdvanceCollectController {

    @Autowired
    private AdvanceCollectMapper advanceCollectMapper;

    @GetMapping("/getProjectsBasicInfo")
    @ResponseBody
    public Object getProjectsBasicInfo(){

        ArrayList<AdvanceProjectEntity> arrayList = advanceCollectMapper.getProjects();

        return arrayList;
    }

    @GetMapping("/getProjectsDetailInfo")
    @ResponseBody
    public Object getProjectDetailInfo(){

        ArrayList<AdvanceProjectModel> responseList = new ArrayList<>();

        ArrayList<AdvanceProjectEntity> projects = advanceCollectMapper.getProjects();

        for(AdvanceProjectEntity project : projects) {

            ArrayList<AdvanceTaskEntity> tasks = advanceCollectMapper.getTasks(project.getProject_id());

            responseList.add(new AdvanceProjectModel(project,tasks));

        }

        return responseList;





//            if(projectMap.get(advanceTaskEntity.getProject_id())==null){
//                ArrayList<AdvanceTaskEntity> advanceTasks = new ArrayList<>();
//                advanceTasks.add(advanceTaskEntity);
//                projectMap.put(advanceTaskEntity.getProject_id(),advanceTasks);
//            }else {
//                projectMap.get(advanceTaskEntity.getProject_id()).add(advanceTaskEntity);
//            }
    }

    @PostMapping("/createProject")
    @ResponseBody
    public Object createProject(@RequestBody Map<String,Object> requestMap){


        AdvanceProjectEntity advanceProjectEntity = new AdvanceProjectEntity();

        String project_name = requestMap.get("project_name").toString();

        Timestamp create_time = new Timestamp(System.currentTimeMillis());

        advanceProjectEntity.setProject_name(project_name);
        advanceProjectEntity.setCreate_time(create_time);

        Map<String,Object> reponse = new HashMap<>();


        if(advanceCollectMapper.createProject(advanceProjectEntity)){
            reponse.put("code",true);
            reponse.put("message","新建项目成功");
            reponse.put("newProject", new AdvanceProjectModel(advanceProjectEntity,new ArrayList<AdvanceTaskEntity>()));
            return reponse;
        }else {
            reponse.put("code",false);
            reponse.put("message","新建项目失败");
            return reponse;
        }
    }

    @PostMapping("/createTask")
    @ResponseBody
    public Object createTask(@RequestBody Map<String,Object> requestMap){

        int project_id = (int) requestMap.get("project_id");
        String task_name = (String)requestMap.get("task_name");

        Timestamp create_time = new Timestamp(System.currentTimeMillis());


        AdvanceTaskEntity advanceTaskEntity = new AdvanceTaskEntity();
        advanceTaskEntity.setTask_name(task_name);
        advanceTaskEntity.setCreate_time(create_time);

        Map<String,Object> reponse = new HashMap<>();

        if(advanceCollectMapper.createTask(advanceTaskEntity)){
            if (advanceCollectMapper.setRelation(project_id,advanceTaskEntity.getTask_id())){
                reponse.put("code",true);
                reponse.put("message","新建任务成功");
                reponse.put("project_id",project_id);
                reponse.put("newTask",advanceTaskEntity);
                return reponse;
            }else {
                reponse.put("code",false);
                reponse.put("message","新建任务失败,无法插入项目和任务的关系");
                return reponse;
            }
        }else {
            reponse.put("code",false);
            reponse.put("message","新建任务失败，无法插入一个任务");
            return reponse;
        }

    }


    @PostMapping("/uploadFile")
    @ResponseBody
    public Object upload(HttpServletRequest httpServletRequest,@RequestParam("file_upload_path") MultipartFile file){



        String name = file.getOriginalFilename();

        String path = "/Users/luozhongjin/Desktop/";

        File dest = new File(path+name);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return httpServletRequest.getParameterMap();

    }

    @PostMapping("/test")
    @ResponseBody
    public Object test(@RequestBody Map<String,Object> requests){

        System.out.println(requests);

        return requests;

    }



}
