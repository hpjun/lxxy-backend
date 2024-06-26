package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.dto.UserCardDTO;
import com.yhp.lxxybackend.model.dto.UserDTO;
import com.yhp.lxxybackend.model.dto.UserFormDTO;
import com.yhp.lxxybackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhp.lxxybackend.model.vo.PostCardVO;
import com.yhp.lxxybackend.model.vo.UserCardVO;
import com.yhp.lxxybackend.model.vo.UserVO;

import java.util.List;

/**
* @author Admin
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-03-27 17:22:54
*/
public interface UserService extends IService<User> {
    /**
     * 注册用户
     * @param userFormDTO
     * @return
     */
    Result register(UserFormDTO userFormDTO);

    /**
     * 用户登录
     * @param userFormDTO
     * @param role
     * @return
     */
    Result login(UserFormDTO userFormDTO,String role);

    /**
     * 退出登录
     * @return
     */
    Result logout();

    /**
     * 修改用户密码
     * @param userFormDTO
     * @return
     */
    Result updatePwd(UserFormDTO userFormDTO);

    /**
     * 分页查询用户
     * @param pageNum
     * @param sc
     * @param ban
     * @return
     */
    Result<List<UserCardVO>> listUser(Integer pageNum, String sc, String ban);

    /**
     * 批量删除用户
     * @param ids
     * @return
     */
    Result delete(List<Integer> ids);

    /**
     * 封禁/解封用户
     * @param userId
     * @return
     */
    Result changeStatus(Integer userId);

    /**
     * 获取主页用户信息
     * @return
     */
    Result<UserVO> userInfo();

    /**
     * 获取我的详细信息
     * @return
     */
    Result<UserDTO> detail();

    /**
     * 编辑用户资料
     * @param userDTO
     * @return
     */
    Result<UserDTO> edit(UserDTO userDTO);

    /**
     * 分页查询用户粉丝列表
     * @param pageNum
     * @return
     */
    Result<List<UserCardDTO>> fans(Integer pageNum,Long userId);

    /**
     * 分页查询用户关注列表
     * @param pageNum
     * @return
     */
    Result<List<UserCardDTO>> follows(Integer pageNum,Long userId);

    /**
     * 获取当前用户手机号
     * @return
     */
    Result<String> getPhone();

    /**
     * 关注用户
     * @param followUserId
     * @return
     */
    Result follow(Integer followUserId);

    /**
     * 取消关注
     * @param followUserId
     * @return
     */
    Result unFollow(Integer followUserId);

    /**
     * 获取主页收藏夹
     * @param pageNum
     * @return
     */
    Result<List<PostCardVO>> favorites(Integer pageNum);

    /**
     * 动态获取动态信息
     * @param minTime
     * @param offset
     * @return
     */
    Result<List<Object>> dynamic(String minTime, Integer offset, Long userId);

    /**
     * 查看别人信息
     * @param userId
     * @return
     */
    Result<UserVO> otherUserInfo(Integer userId);


}
