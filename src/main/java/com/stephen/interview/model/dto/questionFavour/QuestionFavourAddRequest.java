package com.stephen.interview.model.dto.questionFavour;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建题目收藏请求
 *
 * @author stephen qiu
 */
@Data
public class QuestionFavourAddRequest implements Serializable {
    
    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}