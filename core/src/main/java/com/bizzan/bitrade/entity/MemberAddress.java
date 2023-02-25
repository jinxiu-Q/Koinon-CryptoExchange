package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.MemberAddressStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 会员提币地址
 *
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年01月26日
 */
@Entity
@Data
public class MemberAddress {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deleteTime;
    @JoinColumn(name = "coin_id")
    @ManyToOne
    private Coin coin;
    private String address;
    @Enumerated(EnumType.ORDINAL)
    private MemberAddressStatus status= MemberAddressStatus.WAITING;
    private Long memberId;
    private String remark;
    private Integer protocol;
    @JsonIgnore
    private String confirmationString;
}
