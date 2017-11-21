package com.monetware.controller;

import com.google.gson.Gson;
import com.monetware.mapper.collect.AdvanceCollectMapper;
import com.monetware.mapper.collect.SpiderTaskInfoMapper;
import com.monetware.model.collect.AdvanceProjectEntity;
import com.monetware.model.collect.AdvanceProjectModel;
import com.monetware.model.collect.AdvanceTaskEntity;
import com.monetware.model.collect.SpiderTaskInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/advance")
@Controller
public class AdvanceCollectController {

    @Autowired
    private AdvanceCollectMapper advanceCollectMapper;

    @Autowired
    SpiderTaskInfoMapper spiderTaskInfoMapper;

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
    public Object test(@RequestBody Map<String, Object> requests) {

        System.out.println(requests);

        Gson gson = new Gson();
        String jsonString = gson.toJson(requests);
        System.out.println("----------------------------------");
        System.out.println(jsonString);
        System.out.println("----------------------------------");

        String project_id = "";
        String task_id = "";
        for (Map.Entry<String, Object> entry : requests.entrySet()) {
            if (entry.getKey().equals("basic_rule") && entry.getValue() instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    if (e.getKey().equals("project_id")) {
                        project_id = e.getValue().toString();
                    } else if (e.getKey().equals("task_id")) {
                        task_id = e.getValue().toString();
                    }
                }
            }
        }

        FileWriter fileWriter = null;
        File file = null;
        try {
            file = new File("task_config_" + project_id + "_" + task_id + ".json");
            fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        String path = file.getAbsolutePath();
        spiderTaskInfoMapper.saveConfigPathById(path, task_id);

//        System.out.println("----------------------------------");
//        for (Map.Entry<String, Object> entry : requests.entrySet()) {
//            if (entry.getValue() instanceof Map) {
//                System.out.println("entry.getValue() is instanceof Map");
//            }
//
//            System.out.println("entry.getKey() = \"" + entry.getKey() +
//                    "\", entry.getValue() = \"" + entry.getValue() + "\".");
//        }
//        System.out.println("----------------------------------");

        return requests;
    }

    @PostMapping(value = "task_config", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object saveTaskConfig(@RequestBody Map<String, Object> requests) {
        String project_id = "";
        String task_id = "";
        for (Map.Entry<String, Object> entry : requests.entrySet()) {
            if (entry.getKey().equals("basic_rule")) {
                Map<String, Object> map = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    if (e.getKey().equals("project_id")) {
                        project_id = e.getValue().toString();
                    } else if (e.getKey().equals("task_id")) {
                        task_id = e.getValue().toString();
                    }
                }
            } else if (entry.getKey().equals("url_pattern")) {
                Map<String, Object> map = (Map<String, Object>) entry.getValue();
                String currentSelected = map.get("current_selected").toString();
                switch (currentSelected) {
                    case "single": {
                        break;
                    }

                    case "list": {
                        Map<String, Object> m = (Map<String, Object>) map.get("list");
                        String template = m.get("url_wildcard").toString();
                        int start = Integer.valueOf(m.get("init_value").toString());
                        int interval = Integer.valueOf(m.get("gap").toString());
                        int num = Integer.valueOf(m.get("pages_num").toString());
                        // TODO: store the url in the file

                        break;
                    }

                    case "click": {
                        // TODO: store the url in the file

                        break;
                    }

                    case "import": {
                        // TODO: store the url in the file

                        break;
                    }
                }
            }
        }

        // Convert the requests map to json string
        Gson gson = new Gson();
        String jsonString = gson.toJson(requests);

        // Write the json string into the configuration file
        FileWriter fileWriter = null;
        File file = null;
        try {
            file = new File("task_config_" + project_id + "_" + task_id + ".json");
            fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        // Save the configuration file path into the database
        String path = file.getAbsolutePath();
        spiderTaskInfoMapper.saveConfigPathById(path, task_id);

        return requests;
    }

    @GetMapping(value = "task_config", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getTaskConfig(@RequestParam("task_id") String taskId) {
        // Get the path of the configuration file of the task
        String path = spiderTaskInfoMapper.findSpiderTaskInfoById(taskId)
                .getTask_config_location();

        // Read the content of the configuration file
        File file = new File(path);
        FileInputStream fileInputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fileInputStream.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, length));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

}