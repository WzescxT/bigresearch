package com.monetware.model.analysis;

import java.util.Date;

public class AnalysisProjectClassify {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column analysis_project_classify_0.id
	 * @mbg.generated
	 */
	private Long id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column analysis_project_classify_0.category_name
	 * @mbg.generated
	 */
	private String categoryName;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column analysis_project_classify_0.category_id
	 * @mbg.generated
	 */
	private Integer categoryId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column analysis_project_classify_0.text_info_id
	 * @mbg.generated
	 */
	private Long textInfoId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column analysis_project_classify_0.create_time
	 * @mbg.generated
	 */
	private Date createTime;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column analysis_project_classify_0.id
	 * @return  the value of analysis_project_classify_0.id
	 * @mbg.generated
	 */
	public Long getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column analysis_project_classify_0.id
	 * @param id  the value for analysis_project_classify_0.id
	 * @mbg.generated
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column analysis_project_classify_0.category_name
	 * @return  the value of analysis_project_classify_0.category_name
	 * @mbg.generated
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column analysis_project_classify_0.category_name
	 * @param categoryName  the value for analysis_project_classify_0.category_name
	 * @mbg.generated
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName == null ? null : categoryName.trim();
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column analysis_project_classify_0.category_id
	 * @return  the value of analysis_project_classify_0.category_id
	 * @mbg.generated
	 */
	public Integer getCategoryId() {
		return categoryId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column analysis_project_classify_0.category_id
	 * @param categoryId  the value for analysis_project_classify_0.category_id
	 * @mbg.generated
	 */
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column analysis_project_classify_0.text_info_id
	 * @return  the value of analysis_project_classify_0.text_info_id
	 * @mbg.generated
	 */
	public Long getTextInfoId() {
		return textInfoId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column analysis_project_classify_0.text_info_id
	 * @param textInfoId  the value for analysis_project_classify_0.text_info_id
	 * @mbg.generated
	 */
	public void setTextInfoId(Long textInfoId) {
		this.textInfoId = textInfoId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column analysis_project_classify_0.create_time
	 * @return  the value of analysis_project_classify_0.create_time
	 * @mbg.generated
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column analysis_project_classify_0.create_time
	 * @param createTime  the value for analysis_project_classify_0.create_time
	 * @mbg.generated
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}