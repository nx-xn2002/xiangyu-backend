package com.nx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nx.model.domain.Tag;
import com.nx.service.TagService;
import com.nx.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author 18702
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-01-24 22:26:04
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




