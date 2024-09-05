package com.stephen.interview.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建题目请求
 *
 * @author stephen qiu
 */
@Data
public class QuestionAddRequest implements Serializable {
    
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
    

    private static final long serialVersionUID = 1L;
}