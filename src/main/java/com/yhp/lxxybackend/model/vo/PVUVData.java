package com.yhp.lxxybackend.model.vo;


import lombok.Data;

@Data
public class PVUVData {
    private String date;    //2024-3-25 19:57:00这种格式，传到前端，它自己会格式化
    private Integer PV;    //没小时或每日的PV数据
    private Integer UV;    //没小时或每日的UV数据


    // JS的处理，或者直接截断
// const d = ref("2024-3-25 19:44:00")
// const d1 = new Date(d.value)
// const year = d1.getFullYear()
// const month = d1.getMonth() + 1
// const day = d1.getDate()
// const hours = d1.getHours()
// const minutes = d1.getMinutes()
// const seconds = d1.getMilliseconds()
}
