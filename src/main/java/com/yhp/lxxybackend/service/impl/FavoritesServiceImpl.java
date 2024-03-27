package com.yhp.lxxybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.model.entity.Favorites;
import com.yhp.lxxybackend.service.FavoritesService;
import com.yhp.lxxybackend.mapper.FavoritesMapper;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【favorites(收藏)】的数据库操作Service实现
* @createDate 2024-03-27 17:22:54
*/
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites>
    implements FavoritesService{

}




