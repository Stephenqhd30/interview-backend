package com.stephen.interview.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新题目请求
 *
 * @author stephen qiu
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 标签列表（json数组）
     */
    private List<String> tagList;
    
    /**
     * 推荐答案
     */
    private String answer;
    
    /**
     * 点赞数
     */
    private Integer thumbNum;
    
    /**
     * 收藏数
     */
    private Integer favourNum;
    

    private static final long serialVersionUID = 1L;
}