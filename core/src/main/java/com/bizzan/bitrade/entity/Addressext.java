package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@Table(name = "addressext")
@ToString
public class Addressext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(name = "id", orderNum = "1", width = 20)
    private Integer id;

    @Excel(name = "状态", orderNum = "1", width = 20)
    private Integer status;

    @Excel(name = "地址", orderNum = "1", width = 20)
    private String address;

    @Excel(name = "协议ID", orderNum = "1", width = 20)
    private Integer coinProtocol;

    @Excel(name = "用户ID", orderNum = "1", width = 20)
    private Integer memberId;
    
}
