package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "coinprotocol")
@ToString
public class Coinprotocol {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Excel(name = "协议ID", orderNum = "1", width = 20)
    @NotNull(message = "协议ID不得为空")
    private Integer protocol;

    @Excel(name = "协议名称", orderNum = "1", width = 20)
    @NotBlank(message = "协议名称不得为空")
    private String protocolname;

    @Excel(name = "rpc服务地址", orderNum = "1", width = 20)
    private String rpcserver;

    @Excel(name = "rpc服务用户名", orderNum = "1", width = 20)
    private String rpcuser;

    @Excel(name = "rpc密码", orderNum = "1", width = 20)
    private String rpcpassword;

    @Excel(name = "浏览器", orderNum = "1", width = 20)
    private String browser;

    @Excel(name = "符号", orderNum = "1", width = 20)
    private String symbol;

    @Excel(name = "链ID", orderNum = "1", width = 20)
    private Integer chainid;

}
