package com.stephen.interview.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.interview.model.dto.tag.TagQueryRequest;
import com.stephen.interview.model.entity.Tag;
import com.stephen.interview.model.vo.TagVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 标签服务
 *
 * @author stephen qiu
 */
public interface TagService extends IService<Tag> {

    /**
     * 校验数据
     *
     * @param tag
     * @param add 对创建的数据进行校验
     */
    void validTag(Tag tag, boolean add);

    /**
     * 获取查询条件
     *
     * @param tagQueryRequest
     * @return
     */
    QueryWrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest);

    /**
     * 获取标签封装
     *
     * @param tag
     * @param request
     * @return
     */
    TagVO getTagVO(Tag tag, HttpServletRequest request);

    /**
     * 分页获取标签封装
     *
     * @param tagPage
     * @param request
     * @return
     */
    Page<TagVO> getTagVOPage(Page<Tag> tagPage, HttpServletRequest request);
}