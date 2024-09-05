package com.stephen.interview.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.interview.model.entity.Tag;
import com.stephen.interview.service.TagService;
import com.stephen.interview.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author stephen qiu
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2024-09-05 12:30:55
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




