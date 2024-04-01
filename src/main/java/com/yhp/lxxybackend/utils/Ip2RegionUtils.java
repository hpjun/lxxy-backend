package com.yhp.lxxybackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author yhp
 * @date 2024/4/1 19:11
 */


@Slf4j
public class Ip2RegionUtils {
    public Searcher getSearcher() {
        String dbPath = null;
        try {
            ClassPathResource resource = new ClassPathResource("ip2region.xdb");
            dbPath = resource.getURI().getPath();
        } catch (IOException e) {
            log.error("加载ip2region.xdb资源失败");
        }

        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff = new byte[0];
        try {
            cBuff = Searcher.loadContentFromFile(dbPath);
        } catch (Exception e) {
            log.error("加载内容失败 {}: {}\n",dbPath, e);
        }

        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            log.error("创建searcher缓存失败: {}\n", e);
        }
        return searcher;
    }
}
