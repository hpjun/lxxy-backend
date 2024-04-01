package com.yhp.lxxybackend;

import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.utils.BusinessUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author yhp
 * @date 2024/3/30 20:52
 */

public class test {

    @Test
    void test(){
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date();
//        System.out.println(dateFormat.format(date));


//        Set<String> strings = stringRedisTemplate.opsForZSet().reverseRangeByScore("statistics:system-run-time", 0, new Date().getTime());
//        System.out.println(strings);


//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH) + 1;
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        System.out.println(year+":"+month+":"+day);


//        List<String> past7Days = new ArrayList<>();
//        LocalDate today = LocalDate.now();
//        // 从今天开始向前推算7天
//        for (int i = 0; i < 7; i++) {
//            LocalDate pastDate = today.minusDays(i);
////            LocalDate furtherDate = today.plusDays(i);
//            String formattedDate = pastDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
//            past7Days.add(formattedDate);
//        }
//        System.out.println(past7Days);


//        LocalDate now = LocalDate.now();
//        String format = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
//        System.out.println(format);
//        List<String> past7Days = BusinessUtils.getPastNDays(7);
//        System.out.println(past7Days);


//        List<String> past7Days = BusinessUtils.getPastNDays(7);
//        for (int i = 0; i < 7; i++) {
//            System.out.println(RedisConstants.UV_KEY+past7Days.get(i));
//        }

//        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
//        System.out.println(startOfDay);

//        Long aLong = Long.parseLong("12");
//        System.out.println(aLong);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
//        LocalTime now = LocalTime.of(1,12);
//        String format = now.format(formatter);
//        System.out.println(format);
//        System.out.println(BusinessUtils.getHour());
//        String today = BusinessUtils.getToday();
//        System.out.println(today);

//        ArrayList<String> uvKeys = new ArrayList<>();
//        String format = "%02d"; // 格式化为四位数字，不足四位补零
//        for (int i = 0; i < 24; i++) {
//            uvKeys.add(RedisConstants.HOUR_UV_KEY+String.format(format,i));
//        }
//        System.out.println(uvKeys);


//        int t = LocalDateTime.now().minusHours(24).getHour();
//        String format = "%02d"; // 格式化为四位数字，不足四位补零
//        for (int i = 0; i < 24; i++) {
//            System.out.println((t+i)%24);
//        }

//        // 获取当前时间
//        LocalDateTime now = LocalDateTime.now();
//
//        // 减去24小时
//        LocalDateTime before24Hours = now.minusHours(24);
//        // 输出24小时之前的连续小时数
//        for (int i = 0; i < 24; i++) {
//            int hour = (before24Hours.getHour() + i) % 24; // 对24取模，确保小时数在0-23之间
//            System.out.print(hour + " ");
//        }
//        System.out.println(LocalDateTime.now().getHour());
//        System.out.println(LocalDateTime.now().minusHours(24).getHour());

//        System.out.println(BusinessUtils.getPastNDays(7));
//        List<String> past7Days = BusinessUtils.getPastNDays(7);
//        Collections.reverse(past7Days);
//        System.out.println(past7Days);
//       18 19 20 21 22 23 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17

//        System.out.println(BusinessUtils.getPastNDays(30));
//        List<String> past30Days = BusinessUtils.getPastNDays(30);
//        Collections.reverse(past30Days);
//        System.out.println(past30Days);

//        String context = "kjashfjk你好...ah";
//        System.out.println(context.length());

//        byte[] bytes = compressImage(file);
//        System.out.println(bytes.length);
        System.out.println(UUID.randomUUID()+".png");
    }

}
