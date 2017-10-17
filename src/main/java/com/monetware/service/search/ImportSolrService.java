package com.monetware.service.search;

import com.monetware.model.collect.CollectProject;
import com.monetware.model.collect.CollectTemplateField;
import com.monetware.service.collect.CollectInfoService;
import com.monetware.service.collect.CollectProjectService;
import com.monetware.service.collect.CollectTemplateService;
import com.monetware.util.FileOpearte;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by venbill on 2017/4/1.
 */
@Service
public class ImportSolrService {
    @Autowired
    private CollectProjectService collectProjectService;
    @Autowired
    private CollectTemplateService templateService;
    @Autowired
    private CollectInfoService infoService;


    //获取字段名 写文件
    public boolean writeColumns(long projectId){
        String solrNodeConfPath="E:/OpenSourceProject/solr/apache-tomcat-8.5.12/webapps/solr/solrhome/search_node/conf/";
        String dataConfigPath = solrNodeConfPath+"data-config.xml";
        String schemaPath = solrNodeConfPath+"managed-schema";
        CollectProject project = collectProjectService.getCollectProject(projectId);
        List<CollectTemplateField> fields = templateService.getCollectTemplateById(project.getTemplateId()).getFields();
        String dataConfigStr = "\r\n\t<entity name=\"collect_info_"+projectId+"\" query=\"select *,"+project.getCreateUser()+" as 'solr_userId',"+projectId+" as 'solr_projectId' from collect_info_"+projectId+" where 1=1\" >\r\n";
        dataConfigStr += "\t\t<field column=\"solr_userId\" name=\"solr_userId\"/>\r\n";
        dataConfigStr += "\t\t<field column=\"solr_projectId\" name=\"solr_projectId\"/>\r\n";
        String schemaStr = "\r\n";
        String fieldName = "";
        for (CollectTemplateField field:fields) {
            fieldName = field.getName();
            dataConfigStr += "\t\t<field column=\""+fieldName+"\" name=\""+fieldName+"\"/>\r\n";

            schemaStr +="\t<field name=\""+fieldName+"\" type=\"text_general\"  indexed=\"true\" stored=\"true\"/>\r\n";
        }
        dataConfigStr +="\t</entity> \r\n" +"</document>   </dataConfig>";

        schemaStr += "</schema>";
        System.out.println(schemaStr);
        System.out.println("========>分界线");
        System.out.println(dataConfigStr);
        try {
            FileOpearte.reWriteEndline(dataConfigPath,dataConfigStr);
            FileOpearte.reWriteEndline(schemaPath,schemaStr);
        }catch (Exception e){
            return false;
        }
        return true;
    }



    //导入数据 建立索引
    public boolean dataImport(){
        String dataImportUrl = "http://localhost:9090/solr/search_node/dataimport?command=full-import";
        String result="";
        try {
            // 根据地址获取请求
            HttpGet request = new HttpGet(dataImportUrl);//这里发送get请求
            // 获取当前客户端对象
            HttpClient httpClient = new DefaultHttpClient();
            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(request);

            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result= EntityUtils.toString(response.getEntity(),"utf-8");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }



    //向solr中导入doc

    public boolean importDocs(long projectId){
        int userId = collectProjectService.getCollectProject(projectId).getCreateUser();
        String url="http://localhost:9090/solr/search_node";
        HttpSolrClient httpSolrClient = new HttpSolrClient(url);
        httpSolrClient.setParser(new XMLResponseParser()); // 设置响应解析器
        httpSolrClient.setConnectionTimeout(500); // 建立连接的最长时间

//      文档总数
        long sumNo = infoService.getCollectInfoNo(projectId);
        HashMap<String,Long> queryMap = new HashMap<String,Long>();
        queryMap.put("projectId",projectId);
        queryMap.put("pageSize",500L);

        for (long i=1 ;i<=(long) Math.ceil((double)sumNo/500) ;i++){
            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            queryMap.put("pageStart",(i-1)*500);

            List<Map<String,Object>> infoList = infoService.getCollectInfos(queryMap);
            for (Map<String,Object> map:infoList) {
                SolrInputDocument doc = new SolrInputDocument();

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equals("id")){
                        doc.addField("id",projectId+"_"+entry.getValue());
                    }else {
                        doc.addField(entry.getKey(),entry.getValue().toString());
//                        doc.addField(entry.getKey(),entry.getValue().toString().getBytes().length<=32766?entry.getValue(): entry.getValue().toString().substring(0,10000));
                        //todo
                        //字段默认类型 string   ,byte最大长度为32766，长度超过限制时截取，未能更改字段类型
                        //修改solrconfig.xml中的default字段值    strings===>text_general
                    }


                }
                doc.addField("solr_projectId",projectId);
                doc.addField("solr_userId",userId);
                docs.add(doc);

            }

            try {
                //添加并提交文本
                httpSolrClient.add(docs);


            }catch (Exception e){
                System.out.println(e);
                try {
                    httpSolrClient.close();
                }catch (Exception e2){

                }
                return false;
            }

        }

        try {

            httpSolrClient.commit();
            httpSolrClient.close();

        }catch (Exception e){
            return false;

        }

        boolean bol = true;
        return bol;
    }









}
