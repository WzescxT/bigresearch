package com.monetware.mapper.collect;

import com.monetware.model.collect.CollectTemplate;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * recreated by venbill on 2017.3.20
 */
public interface CollectTemplateMapper {







    void createCollectTemplate(CollectTemplate collectTemplate);
    CollectTemplate getCollectTemplate(CollectTemplate collectTemplate);
    CollectTemplate getCollectTemplateById(@Param("id") long id);
    List<CollectTemplate> getUserTemplates(HashMap<String, Object> queryMap);
    List<CollectTemplate> getUserAllTemplates(@Param("userId")int uerId);
    long getUserTemplatesNo(@Param("userId") int uerId, @Param("name") String name);
    long getMonthTemplatesNo(@Param("firstDay") Timestamp firstDay, @Param("userId")int uerId, @Param("name") String name);
    void deleteUserTemplate(@Param("id")long id,@Param("createUser")int uerId);



}