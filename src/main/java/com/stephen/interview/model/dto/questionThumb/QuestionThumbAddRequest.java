package com.stephen.interview.model.dto.questionThumb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建题目点赞请求
 *
 * @author stephen qiu
 */
@Data
public class QuestionThumbAddRequest implements Serializable {
    
    /**
     * 题目 id
     */
    private Long questionId;
    

    private static final long serialVersionUID = 1L;
}