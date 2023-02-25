package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.PromotionLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年03月08日
 */
@Entity
@Data
public class MemberPromotion {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    //邀请者Id
    private Long inviterId;
    //受邀者Id
    private Long inviteesId;

    @Enumerated(EnumType.ORDINAL)
    private PromotionLevel level;
    
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
