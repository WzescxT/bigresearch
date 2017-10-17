package com.monetware.controller;

import com.monetware.service.collect.CollectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("/Excel")
public class ExportExcelController {
    @Resource

    @Autowired
    private CollectInfoService collectInfoService;

    /**
     * 导出用户信息为excel表格
     *
     * @throws IOException
     *//*

    @RequestMapping("/ExportExcel")
    @ResponseBody
	*//*开发者数据导出功能*//*
    public void Export(@RequestBody HashMap<String, Long> queryMap,HttpServletRequest request) throws IOException {
        CollectInfo collectInfo = new CollectInfo();


        HashMap<String, Object> infoMap=new HashMap<String, Object>();
        long projectId=queryMap.get("projectId");

        long bigTotalItems=collectInfoService.getCollectInfoNo(projectId);
        queryMap.put("pageSize", bigTotalItems);

        queryMap.put("pageStart",0L);
        queryMap.put("bigTotalItems", bigTotalItems);
        List<HashMap> collectInfos=collectInfoService.getCommonInfo(queryMap);


        infoMap.put("collectInfos", collectInfos);
        infoMap.put("bigTotalItems",bigTotalItems );




        List<Map<String, Object>> headInfoList = new ArrayList<Map<String,Object>>();


        Iterator ite = collectInfos.get(0).entrySet().iterator();
        while (ite.hasNext()) {
            Map<String, Object> itemMap = new HashMap<String, Object>();

            Map.Entry entry = (Map.Entry) ite.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            itemMap.put("title", key);
            itemMap.put("columnWidth", 50);
            itemMap.put("dataKey", key);
            if(key.equals("id")==false && key.equals("Id")==false && key.equals("ID")==false){

                headInfoList.add(itemMap);

            }

        }



        List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();




        Map<String, Object> dataItem = null;


        for(int i=0; i <collectInfos.size(); i++){
            dataItem = new HashMap<String, Object>();


            Iterator iter = collectInfos.get(i).entrySet().iterator();
            while (iter.hasNext()) {

                Map.Entry entry = (Map.Entry) iter.next();
                String key = entry.getKey().toString();
                String val = entry.getValue().toString();

                if(key.equals("id")==false && key.equals("Id")==false && key.equals("ID")==false){
                    dataItem.put(key, val);
                }
            }




            dataList.add(dataItem);
        }





        POIUtil.exportExcelFilePath("统一认证平台开发者数据信息","D:\\"+Math.random()+".xls", headInfoList, dataList);


    }*/

}
