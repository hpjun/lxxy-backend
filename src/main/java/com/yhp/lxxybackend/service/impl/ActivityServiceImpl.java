package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.BusinessConstant;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.exception.BusinessException;
import com.yhp.lxxybackend.mapper.ActivityMemberMapper;
import com.yhp.lxxybackend.model.dto.ActivityDTO;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.Activity;
import com.yhp.lxxybackend.model.entity.ActivityMember;
import com.yhp.lxxybackend.model.entity.PostComment;
import com.yhp.lxxybackend.model.vo.ActivityCardVO;
import com.yhp.lxxybackend.model.vo.ActivityVO;
import com.yhp.lxxybackend.service.ActivityService;
import com.yhp.lxxybackend.mapper.ActivityMapper;
import com.yhp.lxxybackend.utils.BusinessUtils;
import com.yhp.lxxybackend.utils.RegexUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Admin
 * @description 针对表【activity(活动)】的数据库操作Service实现
 * @createDate 2024-03-27 17:22:54
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity>
        implements ActivityService {

    @Resource
    ActivityMapper activityMapper;
    @Resource
    ActivityMemberMapper activityMemberMapper;

    @Override
    public Result<List<ActivityCardVO>> listActivity(Integer pageNum, String sc, String level) {
        // 封装查询信息
        QueryWrapper<Activity> activityQueryWrapper = new QueryWrapper<>();
        // 如果在难度范围内就查询，否则就不加，等于全查
        switch (level) {
            case "简单":
                activityQueryWrapper.eq("level", 0);
                break;
            case "休闲":
                activityQueryWrapper.eq("level", 1);
                break;
            case "困难":
                activityQueryWrapper.eq("level", 2);
                break;
            default:
        }
        if (!StrUtil.isBlank(sc)) {
            activityQueryWrapper
                    .and(qw -> qw
                            .like("id", sc)
                            .or().like("title", sc)
                            .or().like("profile", sc));
        }
        // 封装分页对象
        Page<Activity> page = new Page<>(pageNum, MessageConstant.ADMIN_PAGE_SIZE);
        // 分页查询
        Page<Activity> activityPage = activityMapper.selectPage(page, activityQueryWrapper);
        List<Activity> activities = activityPage.getRecords();
        Date now = new Date();

        ArrayList<ActivityCardVO> activityCardVOList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Activity activity : activities) {
            // 查询用户信息之前进行活动状态判断
            Date startTime = activity.getStartTime();
            Date endTime = activity.getEndTime();
            int s = startTime.compareTo(now);
            int e = endTime.compareTo(now);
            if (s > 0) {
                // 活动未开始
                if (!("未开始".equals(activity.getStatus()))) {
                    // 进行更新
                    activity.setStatus("未开始");
                    activityMapper.updateById(activity);
                }
            } else if (e < 0) {
                // 活动已结束
                if (!("已结束".equals(activity.getStatus()))) {
                    activity.setStatus("已结束");
                    activityMapper.updateById(activity);
                }
            } else {
                // 活动进行中
                if (!("进行中".equals(activity.getStatus()))) {
                    activity.setStatus("进行中");
                    activityMapper.updateById(activity);
                }
            }

            ActivityCardVO activityCardVO = BeanUtil.copyProperties(activity, ActivityCardVO.class);
            // 格式化时间
            activityCardVO.setStartTime(dateFormat.format(activity.getStartTime()));
            activityCardVO.setEndTime(dateFormat.format(activity.getEndTime()));
            activityCardVOList.add(activityCardVO);
        }
        return Result.ok(activityCardVOList, page.getTotal());
    }

    @Override
    public Result delete(List<Integer> ids) {
        // 判断ids是否为空
        if (ids.size() == 0) {
            return Result.fail("活动不存在，删除失败");
        }
        for (Integer id : ids) {
            // 删除帖子
            activityMapper.deleteById(id);
            // 删除活动成员
            activityMemberMapper.delete(new QueryWrapper<ActivityMember>()
                    .eq("activity_id", id));
        }
        return Result.ok();
    }

    @Override
    public Result publish(ActivityDTO activityDTO) {
        String title = activityDTO.getTitle();
        String profile = activityDTO.getProfile();
        String venue = activityDTO.getVenue();
        Integer distance = activityDTO.getDistance();
        String level = activityDTO.getLevel();
        String startTimeStr = activityDTO.getStartTime();
        String endTimeStr = activityDTO.getEndTime();
        Integer totalCount = activityDTO.getTotalCount();
        String contact = activityDTO.getContact();
        String picUrl = activityDTO.getPicUrl();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
        LocalDateTime now = LocalDateTime.now();


        // 校验信息：活动名称(小于50字)、活动简介(小于1000字)、集合地(小于50字)、里程(不超过10w公里)、难度(0，1，2)
        if (!(title.length() > 0 && title.length() <= 50)) {
            return Result.fail(MessageConstant.TITLE_TOO_LONG);
        }
        if (!(profile.length() > 0 && profile.length() <= 1000)) {
            return Result.fail(MessageConstant.CONTENT_TOO_LONG);
        }
        if (!(venue.length() > 0 && venue.length() <= 50)) {
            return Result.fail(MessageConstant.VENUE_TOO_LONG);
        }
        if (distance < 0 || distance > 100000) {
            return Result.fail(MessageConstant.DISTANCE_FAIL);
        }
        if (!("简单".equals(level) || "休闲".equals(level) || "困难".equals(level))) {
            return Result.fail(MessageConstant.LEVEL_FAIL);
        }
        // 开始时间(在合理范围内)、结束时间(结束时间需要大于开始时间)、总人数(小于500)、联系方式(就是手机号的正则判断)
        // 判断开始时间是否在当前时间之后
        if (startTime.isBefore(now)) {
            return Result.fail("开始时间应该在当前时间后");
        }
        if (endTime.isBefore(startTime)) {
            return Result.fail("结束时间应该在开始时间之后");
        }
        if (totalCount > 500) {
            return Result.fail("活动人数太多了");
        }
        if (RegexUtils.isPhoneInvalid(contact)) {
            return Result.fail(MessageConstant.PHONE_FORMAT);
        }
        // 活动封面为空就置为默认封面
        if (picUrl.length() == 0) {
            picUrl = "默认封面";
        }


        if ("简单".equals(level)) {
            activityDTO.setLevel("0");
        } else if ("休闲".equals(level)) {
            activityDTO.setLevel("1");
        } else {
            activityDTO.setLevel("2");
        }

        // 拷贝属性，补全之后的字段
        LoginUserDTO user = UserHolder.getUser();
//         start_time, end_time,status
        Activity activity = BeanUtil.copyProperties(activityDTO, Activity.class);
        activity.setUserId(user.getId());
        activity.setUsername(user.getUsername());
        activity.setUserAvatar(user.getAvatar());
        try {
            activity.setStartTime(simpleDateFormat.parse(startTimeStr));
            activity.setEndTime(simpleDateFormat.parse(endTimeStr));
        } catch (ParseException e) {
            throw new BusinessException("日期转换异常");
        }
        // 判断当前活动状态 未开始、进行中、已结束
        if (now.isBefore(startTime)) {
            activity.setStatus("未开始");
        } else if (now.isAfter(endTime)) {
            activity.setStatus("已结束");
        } else {
            activity.setStatus("进行中");
        }
        // 向活动表插入该条数据
        activityMapper.insert(activity);
        // 向活动成员表插入自己 activity_id, user_id, username, user_avatar
        ActivityMember activityMember = new ActivityMember();
        activityMember.setActivityId(activity.getId());
        activityMember.setUserId(user.getId());
        activityMember.setUsername(user.getUsername());
        activityMember.setUserAvatar(user.getAvatar());
        activityMemberMapper.insert(activityMember);
        return Result.ok();
    }

    @Override
    public Result<List<ActivityCardVO>> getAll(String minTime, Integer offset) {
        QueryWrapper<Activity> activityQueryWrapper = new QueryWrapper<>();

        // 将minTime转为Date类型方便查数据库
        long time = Long.parseLong(minTime);
        Date date = new Date(time);
        activityQueryWrapper
                .lt("create_time",date)
                .orderByDesc("create_time");
        // 封装分页对象
        Page<Activity> page = new Page<Activity>(offset,MessageConstant.USER_PAGE_SIZE);
        Page<Activity> activityPage = activityMapper.selectPage(page, activityQueryWrapper);
        ArrayList<ActivityCardVO> activityCardVOList = new ArrayList<>();
        List<Activity> activities = activityPage.getRecords();
        for (Activity activity : activities) {
            ActivityCardVO activityCardVO = BeanUtil.copyProperties(activity, ActivityCardVO.class);
            // 进行图片压缩
            activityCardVO.setPicUrl(activity.getPicUrl() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
            Long memberCount = activityMemberMapper.selectCount(new QueryWrapper<ActivityMember>()
                    .eq("activity_id", activity.getId()));
            activityCardVO.setMemberCount(Math.toIntExact(memberCount));
            activityCardVOList.add(activityCardVO);
        }

        return Result.ok(activityCardVOList);
    }

    @Override
    public Result<ActivityVO> activityDetail(Integer activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if(activity==null){
            return Result.fail(MessageConstant.ACTIVITY_NOT_EXIST);
        }
        // 拷贝activity属性到activityVO
        ActivityVO activityVO = BeanUtil.copyProperties(activity, ActivityVO.class);
        // 查询活动成员数
        Long memberCount = activityMemberMapper.selectCount(new QueryWrapper<ActivityMember>()
                .eq("activity_id", activity.getId()));
        activityVO.setMemberCount(Math.toIntExact(memberCount));
        // 进行时间格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        activityVO.setStartTime(dateFormat.format(activity.getStartTime()));
        activityVO.setEndTime(dateFormat.format(activity.getEndTime()));
        // 判断用户是否加入，没登录显示没加入
        activityVO.setIsJoin(false);
        LoginUserDTO user = UserHolder.getUser();
        if(user!=null){
            ActivityMember activityMember = activityMemberMapper.selectOne(new QueryWrapper<ActivityMember>()
                    .eq("user_id", user.getId())
                    .eq("activity_id", activity.getId()));
            if(activityMember != null){
                activityVO.setIsJoin(true);
            }
        }
        // 进行图片压缩
        activityVO.setPicUrl(activity.getPicUrl()+BusinessConstant.OSS_60Q_URL_EXTEND);

        return Result.ok(activityVO);
    }

    @Override
    public Result join(Integer activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if(activity == null){
            return Result.fail(MessageConstant.ACTIVITY_NOT_EXIST);
        }
        LoginUserDTO user = UserHolder.getUser();
        if(user == null){
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        ActivityMember activityMember = activityMemberMapper.selectOne(new QueryWrapper<ActivityMember>()
                .eq("user_id", user.getId())
                .eq("activity_id", activity.getId()));
        if(activityMember != null){
            return Result.ok(true);
        }
        activityMember = new ActivityMember();
        activityMember.setUserAvatar(user.getAvatar());
        activityMember.setActivityId(activity.getId());
        activityMember.setUserId(user.getId());
        activityMember.setUsername(user.getUsername());
        activityMemberMapper.insert(activityMember);
        return Result.ok(true);
    }

    @Override
    public Result unJoin(Integer activityId) {
        LoginUserDTO user = UserHolder.getUser();
        if(user == null){
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        // 直接删除
        activityMemberMapper.delete(new QueryWrapper<ActivityMember>()
                .eq("user_id",user.getId())
                .eq("activity_id",activityId));

        return Result.ok(false);
    }

    @Override
    public Result<List<ActivityCardVO>> getMine(Integer offset) {
        LoginUserDTO user = UserHolder.getUser();
        if(user == null){
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        QueryWrapper<Activity> activityQueryWrapper = new QueryWrapper<>();
        activityQueryWrapper.eq("user_id",user.getId());
        Page<Activity> page = new Page<>(offset,MessageConstant.USER_PAGE_SIZE);
        Page<Activity> activityPage = activityMapper.selectPage(page, activityQueryWrapper);
        List<Activity> activities = activityPage.getRecords();
        ArrayList<ActivityCardVO> activityCardVOList = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityCardVO activityCardVO = BeanUtil.copyProperties(activity, ActivityCardVO.class);
            // 进行图片压缩
            activityCardVO.setPicUrl(activity.getPicUrl() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
            Long memberCount = activityMemberMapper.selectCount(new QueryWrapper<ActivityMember>()
                    .eq("activity_id", activity.getId()));
            activityCardVO.setMemberCount(Math.toIntExact(memberCount));
            activityCardVOList.add(activityCardVO);
        }

        return Result.ok(activityCardVOList);
    }

    @Override
    public Result<List<ActivityCardVO>> getJoined(Integer offset) {
        LoginUserDTO user = UserHolder.getUser();
        if(user == null){
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        // 获取我参加的活动
        Page<ActivityMember> page = new Page<>(offset, MessageConstant.USER_PAGE_SIZE);
        Page<ActivityMember> activityMemberPage = activityMemberMapper.selectPage(page, new QueryWrapper<ActivityMember>()
                .eq("user_id", user.getId()));
        List<ActivityMember> activityMembers = activityMemberPage.getRecords();



        ArrayList<Long> activityIds = new ArrayList<>();
        List<Activity> activities = new ArrayList<>();
        if(activityMembers.size()>0){
            activityMembers.forEach(a->{
                activityIds.add(a.getActivityId());
            });
            activities = activityMapper.selectBatchIds(activityIds);
        }


        ArrayList<ActivityCardVO> activityCardVOList = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityCardVO activityCardVO = BeanUtil.copyProperties(activity, ActivityCardVO.class);
            // 进行图片压缩
            activityCardVO.setPicUrl(activity.getPicUrl() + BusinessConstant.OSS_RESIZE_URL_EXTEND);
            Long memberCount = activityMemberMapper.selectCount(new QueryWrapper<ActivityMember>()
                    .eq("activity_id", activity.getId()));
            activityCardVO.setMemberCount(Math.toIntExact(memberCount));
            activityCardVOList.add(activityCardVO);
        }

        return Result.ok(activityCardVOList);
    }
}




