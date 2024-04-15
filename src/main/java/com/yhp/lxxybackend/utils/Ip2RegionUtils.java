package com.yhp.lxxybackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Scanner;

/**
 * @author yhp
 * @date 2024/4/1 19:11
 */


@Slf4j
@Component
public class Ip2RegionUtils {

    private Searcher searcher;

    @PostConstruct
    public void init(){
        try {
            log.info("开始加载ip2region.xdb资源");
            // 打成jar包的时候千万要注意，不能直接通过地址来获取文件内容
            // 要么使用输入流读到内存中
            // 要么就创建一个临时文件
            ClassPathResource resource = new ClassPathResource("ip2region.xdb");
            InputStream inputStream = resource.getInputStream();
            byte[] cBuff = new byte[inputStream.available()];
            inputStream.read(cBuff);
            this.searcher = Searcher.newWithBuffer(cBuff);
            log.info("加载ip2region.xdb资源成功");
        } catch (IOException e) {
            log.error("加载ip2region.xdb资源失败: {}", e.getMessage());
        } catch (Exception e){
            log.error("创建Searcher对象失败: {}", e.getMessage());
        }
        // 因为无法通过路径来将文件读到内存中，所以手动通过流的方式来放入内存

        // 1、从 dbPath 加载整个 xdb 到内存。
//        try {
//            RandomAccessFile r = new RandomAccessFile(file, "r");
//            cBuff = Searcher.loadContent(r);
////            cBuff = Searcher.loadContentFromFile(dbPath);
//        } catch (Exception e) {
//            log.error("加载内容失败 {}: {}\n",file, e);
//        }

        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
//        Searcher searcher = null;
//        try {
//            searcher = Searcher.newWithBuffer(cBuff);
//        } catch (Exception e) {
//            log.error("创建searcher缓存失败: {}\n", e);
//        }
    }
    public Searcher getSearcher() {
        return searcher;
    }
}
