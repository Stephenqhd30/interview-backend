package com.stephen.interview.model.dto.question;

import com.stephen.interview.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询题目请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 需要过滤的id
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

    /**
     * 创建用户 id
     */
    private Long userId;
    
    /**
     * 题库id
     */
    private Long questionBankId;

    private static final long serialVersionUID = 1L;
}