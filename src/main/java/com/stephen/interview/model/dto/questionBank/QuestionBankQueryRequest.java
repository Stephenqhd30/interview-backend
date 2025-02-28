package com.stephen.interview.model.dto.questionBank;

import com.stephen.interview.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询题库请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 图片
     */
    private String picture;
    
    /**
     * 创建用户id
     */
    private Long userId;
    
    /**
     * 是否需要查询题目列表
     */
    private Boolean needQueryQuestionList;

    private static final long serialVersionUID = 1L;
}