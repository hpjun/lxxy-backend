package com.yhp.lxxybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.model.entity.Activity;
import com.yhp.lxxybackend.service.ActivityService;
import com.yhp.lxxybackend.mapper.ActivityMapper;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【activity(活动)】的数据库操作Service实现
* @createDate 2024-03-27 17:22:54
*/
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity>
    implements ActivityService{

}




