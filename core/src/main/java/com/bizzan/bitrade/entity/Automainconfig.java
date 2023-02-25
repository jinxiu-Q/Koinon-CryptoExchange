package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "automainconfig")
@ToString
public class Automainconfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Excel(name = "ID", orderNum = "1", width = 20)
    private Integer id;

    @Excel(name = "币种ID", orderNum = "1", width = 20)
    private Integer coinid;

    @Excel(name = "币种名称", orderNum = "1", width = 20)
    @NotBlank(message = "币种名称不得为空")
    private String coinname;

    @Excel(name = "协议ID", orderNum = "1", width = 20)
    @NotNull(message = "协议ID不得为空")
    private Integer protocol;

    @Excel(name = "归集地址", orderNum = "1", width = 20)
    private String address;

    @Excel(name = "最低归集数量", orderNum = "1", width = 20)
    private double minnum;

    @Transient
    private String password;
}
