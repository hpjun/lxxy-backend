package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.BeanToBeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.BusinessConstant;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.exception.BusinessException;
import com.yhp.lxxybackend.mapper.*;
import com.yhp.lxxybackend.model.dto.*;
import com.yhp.lxxybackend.model.entity.*;
import com.yhp.lxxybackend.model.vo.ActivityCardVO;
import com.yhp.lxxybackend.model.vo.PostCardVO;
import com.yhp.lxxybackend.model.vo.UserCardVO;
import com.yhp.lxxybackend.model.vo.UserVO;
import com.yhp.lxxybackend.service.UserService;
import com.yhp.lxxybackend.utils.HotUtils;
import com.yhp.lxxybackend.utils.RegexUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Admin
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-03-27 17:22:54
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    UserMapper userMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FollowMapper followMapper;
    @Resource
    FavoritesMapper favoritesMapper;
    @Resource
    PostMapper postMapper;
    @Resource
    ActivityMapper activityMapper;
    @Resource
    ActivityMemberMapper activityMemberMapper;

    @Override
    public Result register(UserFormDTO userFormDTO) {
        String phone = userFormDTO.getPhone();
        String password = userFormDTO.getPassword();
        String code = userFormDTO.getCode();
        // 校验手机号、密码、验证码
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail(MessageConstant.PHONE_FORMAT);
        }
        String realCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if (RegexUtils.isCodeInvalid(code) || !code.equals(realCode)) {
            return Result.fail(MessageConstant.CODE_FAIL);
        }
        if (RegexUtils.isPasswordInvalid(password)) {
            return Result.fail(MessageConstant.PASSWORD_FORMAT);
        }
        // 查询手机号，如果数据库存在手机号说明该手机号已注册
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        List<User> users = userMapper.selectByMap(map);
        if (users.size() != 0) {
            return Result.fail(MessageConstant.PHONE_REGISTERED);
        }

        User user = new User();
        // 对密码进行MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        // 封装User对象
        user.setPhone(phone);
        user.setUsername(phone);
        user.setAvatar(BusinessConstant.DEFAULT_USER_AVATAR);
        user.setProfile(BusinessConstant.DEFAULT_USER_PROFILE);
        user.setSex(BusinessConstant.DEFAULT_USER_SEX);
        user.setAddress(BusinessConstant.DEFAULT_USER_ADDRESS);
        // 存入数据库
        int success = userMapper.insert(user);
        if (success == 0) {
            return Result.fail(MessageConstant.SERVER_ERROR);
        } else {
            // 将验证码置为失效
            stringRedisTemplate.expire(RedisConstants.LOGIN_CODE_KEY + phone, 0, TimeUnit.SECONDS);
            return Result.ok();
        }
    }

    @Override
    public Result login(UserFormDTO userFormDTO, String role) {
        String phone = userFormDTO.getPhone();
        String password = userFormDTO.getPassword();
        String code = userFormDTO.getCode();

        Map<String, Object> map = new HashMap<>();
        List<User> users = null;
        map.put("phone", phone);

        if (StrUtil.isBlank(password) && StrUtil.isBlank(code)) {
            return Result.fail("请输入密码或验证码");
        }

        if (!StrUtil.isBlank(password)) {
            // 进行手机好格式验证
            if (RegexUtils.isPhoneInvalid(phone)) {
                return Result.fail(MessageConstant.PHONE_PASSWORD_ERROR);
            }
            // 进行密码格式比对
            if (RegexUtils.isPasswordInvalid(password)) {
                return Result.fail(MessageConstant.PHONE_PASSWORD_ERROR);
            }
            // 用户查询
            users = userMapper.selectByMap(map);
            if (users.size() == 0) {
                return Result.fail(MessageConstant.UN_REGISTER);
            }
            // 密码比对
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            if (!users.get(0).getPassword().equals(password)) {
                return Result.fail(MessageConstant.PHONE_PASSWORD_ERROR);
            }
        }
        if (!StrUtil.isBlank(code)) {
            // 进行手机好格式验证
            if (RegexUtils.isPhoneInvalid(phone)) {
                return Result.fail(MessageConstant.PHONE_FORMAT);
            }
            // 进行验证码格式比对
            if (RegexUtils.isCodeInvalid(code)) {
                return Result.fail(MessageConstant.CODE_FAIL);
            }
            // 用户查询
            users = userMapper.selectByMap(map);
            if (users.size() == 0) {
                return Result.fail(MessageConstant.UN_REGISTER);
            }
            // 验证码比对
            String realCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
            if (!code.equals(realCode)) {
                return Result.fail(MessageConstant.CODE_FAIL);
            }
        }

        if (users.get(0).getBan() == 1) {
            return Result.fail(MessageConstant.ACCOUNT_LOCKED);
        }

        if ("admin".equals(role)) {
            if (!(users.get(0).getRole().equals("admin"))) {
                return Result.fail(MessageConstant.UN_AUTH);
            }
        }


        // 生成token作为令牌
        String token = IdUtil.getSnowflake().nextIdStr();
        Long id = users.get(0).getId();
        token = token + id;
        LoginUserDTO loginUserDTO = BeanUtil.copyProperties(users.get(0), LoginUserDTO.class);
        loginUserDTO.setToken(token);
        // beanTOMap!!
        Map<String, Object> userMap = BeanUtil.beanToMap(loginUserDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        // 将token放入Redis
        if ("admin".equals(role)) {
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            stringRedisTemplate.expire(tokenKey, RedisConstants.LOGIN_ADMIN_TTL, TimeUnit.DAYS);

        } else {
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            stringRedisTemplate.expire(tokenKey, RedisConstants.LOGIN_USER_TTL, TimeUnit.DAYS);
        }
        return Result.ok(token);
    }

    @Override
    public Result logout() {
        // 获取当前登录的token信息
        String token = UserHolder.getUser().getToken();
        // 在Redis中移除token信息
        stringRedisTemplate.delete(RedisConstants.LOGIN_USER_KEY + token);
        return Result.ok();
    }

    @Override
    public Result updatePwd(UserFormDTO userFormDTO) {
        String password = userFormDTO.getPassword();
        String code = userFormDTO.getCode();
        // 判断密码和验证码的合法性
        if (RegexUtils.isPasswordInvalid(password)) {
            return Result.fail(MessageConstant.PASSWORD_FORMAT);
        }
        if (RegexUtils.isCodeInvalid(code)) {
            return Result.fail(MessageConstant.CODE_FAIL);
        }
        // 获取当前登录的手机号
        Long userId = UserHolder.getUser().getId();
        User user = query().eq("id", userId).one();
        // 校验该手机号的验证码是否正确
        String realCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + user.getPhone());
        if (!code.equals(realCode)) {
            return Result.fail(MessageConstant.CODE_FAIL);
        }
        // 对用户的新密码进行MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        // 更新用户密码信息
        int update = userMapper.updateById(user);
        if (update != 0) {
            stringRedisTemplate.expire(RedisConstants.LOGIN_CODE_KEY + user.getPhone(), 0, TimeUnit.SECONDS);
            // 获取当前登录的token信息
            String token = UserHolder.getUser().getToken();
            // 在Redis中移除token信息
            stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token, 0, TimeUnit.SECONDS);
            return Result.ok("更改成功");
        } else {
            return Result.fail("服务器异常");
        }
    }

    @Override
    public Result<List<UserCardVO>> listUser(Integer pageNum, String sc, String ban) {
        // 根据条件分页查询用户信息

        // 封装查询条件
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        Integer isBan;
        if ("正常".equals(ban)) {
            isBan = 0;
            userQueryWrapper.eq("ban", isBan);
        } else if ("封禁".equals(ban)) {
            isBan = 1;
            userQueryWrapper.eq("ban", isBan);
        } else {
            // 按照全部查询
        }
        if (!StrUtil.isBlank(sc)) {
            userQueryWrapper
                    .and(qw -> qw
                            .like("username", sc)
                            .or().like("phone", sc)
                            .or().like("id", sc));
        }
        // 封装分页对象
        Page<User> page = new Page<>(pageNum, MessageConstant.ADMIN_PAGE_SIZE);
        // 分页查询
        Page<User> userPage = userMapper.selectPage(page, userQueryWrapper);
        List<User> users = userPage.getRecords();
        ArrayList<UserCardVO> userCardVOS = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (User user : users) {
            UserCardVO userCardVO = BeanUtil.copyProperties(user, UserCardVO.class);
            // 格式化日期
            userCardVO.setCreateTime(dateFormat.format(user.getCreateTime()));
            userCardVOS.add(userCardVO);
        }
        return Result.ok(userCardVOS, page.getTotal());
    }

    @Override
    public Result delete(List<Integer> ids) {
        // 判断该数组是否为空
        if (ids.size() == 0) {
            return Result.fail("用户不存在，删除失败");
        }
        // TODO 删除之前，用户涉及的相关记录：活动、评论、收藏夹、帖子应该都被抹除


        userMapper.deleteBatchIds(ids);
        return Result.ok("删除成功");
    }

    @Override
    public Result changeStatus(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail(MessageConstant.USER_NOT_EXIST);
        }
        Integer ban = user.getBan();
        List<Post> posts = postMapper.selectList(new QueryWrapper<Post>().eq("user_id", userId));
        List<Activity> activities = activityMapper.selectList(new QueryWrapper<Activity>().eq("user_id", userId));
        if (ban == 0) {
            // 被ban，将其帖子、活动都设为不可见
            user.setBan(1);
            userMapper.updateById(user);
            posts.forEach(p->{
                p.setIsShow(0);
                postMapper.updateById(p);
            });
            activities.forEach(a->{
                a.setIsShow(0);
                activityMapper.updateById(a);
            });
            return Result.ok(true);
        } else {
            // 解封，将其帖子、活动都设为可见
            user.setBan(0);
            userMapper.updateById(user);
            posts.forEach(p->{
                p.setIsShow(1);
                postMapper.updateById(p);
            });
            activities.forEach(a->{
                a.setIsShow(1);
                activityMapper.updateById(a);
            });
            return Result.ok(false);
        }
    }

    @Override
    public Result<UserVO> userInfo() {
        LoginUserDTO u = UserHolder.getUser();
        if (u == null) {
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }


        User user = userMapper.selectById(u.getId());
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

        // 获取关注人数
        Long followsCount = followMapper.selectCount(new QueryWrapper<Follow>().eq("user_id", user.getId()));
        // 和粉丝人数
        Long fansCount = followMapper.selectCount(new QueryWrapper<Follow>().eq("follow_user_id", user.getId()));

        userVO.setFollowsCount(Math.toIntExact(followsCount));
        userVO.setFansCount(Math.toIntExact(fansCount));
        userVO.setAvatar(user.getAvatar() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
        return Result.ok(userVO);
    }

    @Override
    public Result<UserDTO> detail() {
        LoginUserDTO u = UserHolder.getUser();
        if (u == null) {
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        User user = userMapper.selectById(u.getId());
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

        userDTO.setAvatar(user.getAvatar() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
        return Result.ok(userDTO);
    }

    @Override
    public Result<UserDTO> edit(UserDTO userDTO) {
        LoginUserDTO u = UserHolder.getUser();
        if (u == null) {
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        User user = userMapper.selectById(u.getId());
        // 进行字段校验

        if (!StrUtil.isBlank(userDTO.getUsername())) {
            // 判断名字长度不能超过20个字符
            String newUsername = userDTO.getUsername();
            if (!(newUsername.length() > 0 && newUsername.length() <= 20)) {
                return Result.fail(MessageConstant.USERNAME_LENGTH);
            }
            // 写入新的用户名
            // TODO 异步更改用户名要更新：帖子表、Redis中token缓存的用户信息、活动表、活动成员表、帖子评论表
            user.setUsername(newUsername);
        }
        if (!StrUtil.isBlank(userDTO.getAvatar())) {
            // 直接写入，好像没法判断图片
            // TODO 异步更改头像要更新：帖子表、Redis中token缓存的用户信息、活动表、活动成员表、帖子评论表
            user.setAvatar(userDTO.getAvatar());
        }
        if (!StrUtil.isBlank(userDTO.getProfile())) {
            // 判断长度不超过70字符
            String newProfile = userDTO.getProfile();
            if (!(newProfile.length() > 0 && newProfile.length() <= 70)) {
                return Result.fail(MessageConstant.USER_PROFILE_LENGTH);
            }
            user.setProfile(newProfile);
        }
        if (!StrUtil.isBlank(userDTO.getSex())) {
            // 判断是否是男或女
            String newSex = userDTO.getSex();
            if (!("男".equals(newSex) || "女".equals(newSex))) {
                return Result.fail(MessageConstant.USER_SEX_ERROR);
            }
            user.setSex(newSex);
        }
        if (!StrUtil.isBlank(userDTO.getBirthday())) {
            // 判断日期(不能是未来时间、不能小于1900-1-1)
            String newBirthdayStr = userDTO.getBirthday();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date newBirthday = dateFormat.parse(newBirthdayStr);
                Calendar calendar1900 = Calendar.getInstance();
                calendar1900.set(1900, Calendar.JANUARY, 1); // 1900年1月1日
                Date date1900 = calendar1900.getTime();
                if ((newBirthday.before(date1900)) || (newBirthday.after(new Date()))) {
                    return Result.fail(MessageConstant.USER_BIRTHDAY_ERROR);
                }
                user.setBirthday(newBirthdayStr);
            } catch (Exception e) {
                throw new BusinessException("时间格式不对");
            }
        }
        if (!StrUtil.isBlank(userDTO.getAddress())) {
            // 判断地址格式(正则判断 "香港特别行政区-新界-北区")
            if (RegexUtils.isAddressInvalid(userDTO.getAddress())) {
                return Result.fail(MessageConstant.ADDRESS_FORMAT);
            }
            user.setAddress(userDTO.getAddress());
        }

        if (!StrUtil.isBlank(userDTO.getNewPassword())) {
            if (StrUtil.isBlank(userDTO.getCode())) {
                return Result.fail(MessageConstant.CODE_FAIL);
            }
            String realCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + user.getPhone());
            if (!(userDTO.getCode().equals(realCode))) {
                return Result.fail(MessageConstant.CODE_FAIL);
            }
            // 判断密码格式
            if (RegexUtils.isPasswordInvalid(userDTO.getNewPassword())) {
                return Result.fail(MessageConstant.PASSWORD_FORMAT);
            }
            // 写入新密码
            user.setPassword(DigestUtils.md5DigestAsHex(userDTO.getNewPassword().getBytes()));
        }
        user.setUpdateTime(new Date());
        // 写入数据库
        userMapper.updateById(user);
        return Result.ok();
    }

    @Override
    public Result<List<UserCardDTO>> fans(Integer pageNum, Long userId) {

        if (userId == null) {
            LoginUserDTO u = UserHolder.getUser();
            if (u == null) {
                return Result.fail(MessageConstant.USER_NOT_LOGIN);
            }
            userId = u.getId();
        }

        User user = userMapper.selectById(userId);
        // 查看该用户的粉丝列表
        Page<Follow> page = new Page<>(pageNum, MessageConstant.ADMIN_PAGE_SIZE);
        Page<Follow> followPage = followMapper.selectPage(page, new QueryWrapper<Follow>()
                .eq("follow_user_id", user.getId()));
        List<Follow> fanList = followPage.getRecords();
        // 查询我们的关注列表
        List<Follow> followList = followMapper.selectList(new QueryWrapper<Follow>().eq("user_id", user.getId()));
        ArrayList<Long> followIds = new ArrayList<>();
        followList.forEach(follow -> {
            followIds.add(follow.getFollowUserId());
        });

        ArrayList<UserCardDTO> userCardDTOList = new ArrayList<>();
        if (fanList.size() > 0) {
            ArrayList<Long> fanIds = new ArrayList<>();
            for (Follow fan : fanList) {
                fanIds.add(fan.getUserId());
            }
            List<User> fans = userMapper.selectBatchIds(fanIds);
            for (User fan : fans) {
                UserCardDTO userCardDTO = new UserCardDTO();
                userCardDTO.setAvatar(fan.getAvatar() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
                userCardDTO.setUsername(fan.getUsername());
                userCardDTO.setProfile(fan.getProfile());
                userCardDTO.setUserId(fan.getId());
                userCardDTO.setIsFollowTab(false);
                // 判断我们是否关注该粉丝
                userCardDTO.setIsFollow(false);
                if (followIds.contains(fan.getId())) {
                    userCardDTO.setIsFollow(true);
                }
                userCardDTOList.add(userCardDTO);
            }
        }
        return Result.ok(userCardDTOList);
    }

    @Override
    public Result<List<UserCardDTO>> follows(Integer pageNum, Long userId) {
        if (userId == null) {
            LoginUserDTO u = UserHolder.getUser();
            if (u == null) {
                return Result.fail(MessageConstant.USER_NOT_LOGIN);
            }
            userId = u.getId();
        }

        User user = userMapper.selectById(userId);
        // 获取用户的关注列表
        Page<Follow> page = new Page<>(pageNum, MessageConstant.ADMIN_PAGE_SIZE);
        Page<Follow> followPage = followMapper.selectPage(page, new QueryWrapper<Follow>()
                .eq("user_id", user.getId()));
        List<Follow> followList = followPage.getRecords();
        ArrayList<UserCardDTO> userCardDTOList = new ArrayList<>();
        if (followList.size() > 0) {
            ArrayList<Long> followsIds = new ArrayList<>();
            for (Follow follow : followList) {
                followsIds.add(follow.getFollowUserId());
            }
            List<User> follows = userMapper.selectBatchIds(followsIds);
            for (User follow : follows) {
                UserCardDTO userCardDTO = BeanUtil.copyProperties(follow, UserCardDTO.class);
                userCardDTO.setUserId(follow.getId());
                userCardDTO.setAvatar(follow.getAvatar() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
                userCardDTO.setIsFollowTab(true);
                userCardDTOList.add(userCardDTO);
            }
        }
        return Result.ok(userCardDTOList);
    }

    @Override
    public Result<String> getPhone() {
        LoginUserDTO u = UserHolder.getUser();
        if (u == null) {
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        String phone = userMapper.selectById(u.getId()).getPhone();
        return Result.ok(phone);
    }

    @Override
    public Result follow(Integer followUserId) {
        LoginUserDTO u = UserHolder.getUser();
        if (u == null) {
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        User followUser = userMapper.selectById(followUserId);
        if (followUser == null) {
            return Result.fail(MessageConstant.USER_NOT_EXIST);
        }
        Long count = followMapper.selectCount(new QueryWrapper<Follow>()
                .eq("user_id", u.getId())
                .eq("follow_user_id", followUserId));
        if (count != 0) {
            return Result.ok(true);
        }
        Follow follow = new Follow();
        follow.setFollowUserId(Long.valueOf(followUserId));
        follow.setUserId(u.getId());
        followMapper.insert(follow);

        // 将关注用户的发布的帖子和活动id信息存储到当前用户的收件箱
        // TODO 可以优化为异步操作另开一个线程来处理后续的更新收件箱操作, 取关同理
        List<Post> posts = postMapper.selectList(new QueryWrapper<Post>().eq("user_id", followUserId));
        List<Activity> activities = activityMapper.selectList(new QueryWrapper<Activity>().eq("user_id", followUserId));
        posts.forEach(p -> {
            // TODO 这里还可以优化为Redis的批量操作
            stringRedisTemplate.opsForZSet().add(RedisConstants.USER_INBOX + u.getId(), "postId:" + p.getId(), p.getCreateTime().getTime());
        });
        activities.forEach(a -> {
            stringRedisTemplate.opsForZSet().add(RedisConstants.USER_INBOX + u.getId(), "activityId:" + a.getId(), a.getCreateTime().getTime());
        });
        return Result.ok(true);
    }

    @Override
    public Result unFollow(Integer followUserId) {
        LoginUserDTO u = UserHolder.getUser();
        if (u == null) {
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        followMapper.delete(new QueryWrapper<Follow>()
                .eq("user_id", u.getId())
                .eq("follow_user_id", followUserId));

        // 将关注用户的发布的帖子和活动id信息从当前用户的收件箱中删除
        List<Post> posts = postMapper.selectList(new QueryWrapper<Post>().eq("user_id", followUserId));
        List<Activity> activities = activityMapper.selectList(new QueryWrapper<Activity>().eq("user_id", followUserId));
        posts.forEach(p -> {
            stringRedisTemplate.opsForZSet().remove(RedisConstants.USER_INBOX + u.getId(), "postId:" + p.getId());
        });
        activities.forEach(a -> {
            stringRedisTemplate.opsForZSet().remove(RedisConstants.USER_INBOX + u.getId(), "activityId:" + a.getId());
        });
        return Result.ok(false);
    }

    @Override
    public Result<List<PostCardVO>> favorites(Integer pageNum) {
        LoginUserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }

        Page<Favorites> page = new Page<>(pageNum, MessageConstant.ADMIN_PAGE_SIZE);
        Page<Favorites> favoritesPage = favoritesMapper.selectPage(page, new QueryWrapper<Favorites>()
                .eq("user_id", user.getId())
                .orderByDesc("create_time"));
        List<Favorites> favoritesList = favoritesPage.getRecords();
        ArrayList<PostCardVO> postCardVOList = new ArrayList<>();
        if (favoritesList.size() > 0) {
            ArrayList<Long> favoritesIds = new ArrayList<>();
            favoritesList.forEach(f -> {
                favoritesIds.add(f.getPostId());
            });
            List<Post> posts = postMapper.selectBatchIds(favoritesIds);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Post post : posts) {
                PostCardVO postCardVO = BeanUtil.copyProperties(post, PostCardVO.class);
                // 格式化时间
                postCardVO.setLcTime(dateFormat.format(post.getLcTime()));
                List<String> picUrlList = postCardVO.getPicUrlList();
                ArrayList<String> newPic = new ArrayList<>();
                picUrlList.forEach(pic -> {
                    pic += BusinessConstant.OSS_RESIZE_URL_EXTEND;
                    newPic.add(pic);
                });
                postCardVO.setPicUrlList(newPic);
                // 收藏夹内统一没有置顶标签
                postCardVO.setIsTop(false);
                postCardVOList.add(postCardVO);
            }
        }
        return Result.ok(postCardVOList);
    }

    @Override
    public Result<List<Object>> dynamic(String minTime, Integer offset, Long userId) {

        long time = Long.parseLong(minTime);
        if (userId == null) {
            // 查询用户信息
            LoginUserDTO u = UserHolder.getUser();
            if (u == null) {
                return Result.fail(MessageConstant.USER_NOT_LOGIN);
            }
            userId = u.getId();
        }

        Set<String> range = stringRedisTemplate.opsForZSet().reverseRangeByScore(
                RedisConstants.USER_INBOX + userId, 0, time
                , (offset - 1) * 5L, 5);

        ArrayList<Object> list = new ArrayList<>();
        if (range == null) {
            return Result.ok(list);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        range.forEach(r -> {
            String[] split = r.split(":");
            if ("postId".equals(split[0])) {
                Post post = postMapper.selectById(split[1]);
                if (post != null) {
                    PostCardVO postCardVO = BeanUtil.copyProperties(post, PostCardVO.class);
                    postCardVO.setType("post");
                    // 格式化时间
                    postCardVO.setLcTime(dateFormat.format(post.getLcTime()));
                    List<String> picUrlList = postCardVO.getPicUrlList();
                    ArrayList<String> newPic = new ArrayList<>();
                    picUrlList.forEach(pic -> {
                        pic += BusinessConstant.OSS_RESIZE_URL_EXTEND;
                        newPic.add(pic);
                    });
                    postCardVO.setPicUrlList(newPic);
                    postCardVO.setIsTop(false);
                    list.add(postCardVO);
                }
            } else {
                Activity activity = activityMapper.selectById(split[1]);
                if (activity != null) {
                    ActivityCardVO activityCardVO = BeanUtil.copyProperties(activity, ActivityCardVO.class);
                    activityCardVO.setType("activity");
                    // 进行图片压缩
                    activityCardVO.setPicUrl(activity.getPicUrl() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
                    Long memberCount = activityMemberMapper.selectCount(new QueryWrapper<ActivityMember>()
                            .eq("activity_id", activity.getId()));
                    activityCardVO.setMemberCount(Math.toIntExact(memberCount));
                    list.add(activityCardVO);
                }
            }
        });
        return Result.ok(list);
    }

    @Override
    public Result<UserVO> otherUserInfo(Integer userId) {

        User user = userMapper.selectById(userId);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

        //  查询当前用户是否关注
        userVO.setIsFollow(false);
        LoginUserDTO u = UserHolder.getUser();
        if(u != null){
            Long count = followMapper.selectCount(new QueryWrapper<Follow>()
                    .eq("user_id", u.getId())
                    .eq("follow_user_id", userId));
            if(count != 0){
                userVO.setIsFollow(true);
            }
        }

        // 获取关注人数
        Long followsCount = followMapper.selectCount(new QueryWrapper<Follow>().eq("user_id", user.getId()));
        // 和粉丝人数
        Long fansCount = followMapper.selectCount(new QueryWrapper<Follow>().eq("follow_user_id", user.getId()));

        userVO.setFollowsCount(Math.toIntExact(followsCount));
        userVO.setFansCount(Math.toIntExact(fansCount));
        userVO.setAvatar(user.getAvatar() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
        return Result.ok(userVO);

    }


}




