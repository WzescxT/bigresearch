package com.monetware.mapper.analysis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.monetware.model.analysis.AnalysisProjectWord;
@Repository
public interface AnalysisProjectWordsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table analysis_project_words_1
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table analysis_project_words_1
     *
     * @mbg.generated
     */
    int insert(AnalysisProjectWord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table analysis_project_words_1
     *
     * @mbg.generated
     */
    int insertSelective(AnalysisProjectWord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table analysis_project_words_1
     *
     * @mbg.generated
     */
    AnalysisProjectWord selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table analysis_project_words_1
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(AnalysisProjectWord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table analysis_project_words_1
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AnalysisProjectWord record);

    void createAnalysisProjectWordTable(@Param("analysisProjectId")long analysisProjectId);
    
    
    void batchInsert(@Param("analysisProjectId")long analysisProjectId,@Param("words")List<AnalysisProjectWord> words);
    
    void sumWordFrequency(@Param("analysisProjectId")long analysisProjectId);
    
    List<AnalysisProjectWord> selectByCondition(@Param("analysisProjectId")long analysisProjectId,@Param("natures") String natures,@Param("count")long count);

	void deleteWords(@Param("analysisProjectId")long analysisProjectId);

	List<AnalysisProjectWord> getWordsFrequency(Map<String, Object> queryMap);

	long getWordsFrequencyNo(Map<String, Object> queryMap);
}