package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 用户日志（记录用户登录/注册信息）
 * @Author: bigdogex.com
 * @Date: 2021-01-20 12:22
 */
@Entity
@Data
public class MemberRecord {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String ip; // 登录IP

    private Long memberId; //用户ID（为0则不是登录成功的用户）

    private String email; // 登录邮箱

    private String phone; // 登录手机号

    private String latitude; // 纬度

    private String longitude; // 经度

    private String countryName; // 国家名
    private String countryNameZh; // 国家名（中文）

    private String cityName; // 城市名
    private String cityNameZh; // 城市名（中文）

    private String subdivision; // 分支
    private String subdivisionZh; // 分支（中文）

    private int actionType; // 0：登录成功   1：登录失败   2：检查登录  3：注册成功 4:Token登录成功
    private String action; // login/register/other

    private String result; // success/failed/other

    private int analysised = 0; // 是否已解析IP地址信息(0：未解析，1：解析成功，2：解析失败)

    private String isoCode;

    private String remark; // 备注

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
