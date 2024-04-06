package com.yhp.lxxybackend.controller.user;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.service.PostTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yhp
 * @date 2024/4/5 18:35
 */

@RestController("userPostTypeController")
@RequestMapping("/user/postType")
@Api(tags = "C端-板块接口")
@Slf4j
public class PostTypeController {

    @Resource
    PostTypeService postTypeService;

    @GetMapping()
    @ApiOperation("查询所有板块名称")
    public Result<List<String>> all(){
        return postTypeService.all();
    }
}
