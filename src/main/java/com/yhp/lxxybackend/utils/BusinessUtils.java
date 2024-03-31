package com.yhp.lxxybackend.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yhp
 * @date 2024/3/30 22:07
 */

public class BusinessUtils {

    /**
     * 获取前30天日期
     * @return yyyy:MM:dd
     */
    public static List<String> getPastNDays(int n){
        List<String> pastNDays = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 从今天开始向前推算30天
        for (int i = 0; i < n; i++) {
            LocalDate pastDate = today.minusDays(i);
            String formattedDate = pastDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
            pastNDays.add(formattedDate);
        }

        return pastNDays;
    }

    /**
     * 获取前24小时数
     * @return 01...
     */
    public static List<String> getPast24Hour(){
        ArrayList<String> past24Hour = new ArrayList<>();
        int t = Integer.parseInt(getHour()) + 1;
        String format = "%02d"; // 格式化为2位数字，不足2位补零
        for (int i = 0; i < 24; i++) {
            past24Hour.add(String.format(format,(t+i)%24));
        }
        return past24Hour;
    }

    /**
     * 获取当前日期
     * @return yyyy:MM:dd 2024:3:31
     */
    public static String getToday(){
        return LocalDate.now().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
    }

    /**
     * 获取当前小时数
     * @return HH 03
     */
    public static String getHour(){
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH"));
    }
}
