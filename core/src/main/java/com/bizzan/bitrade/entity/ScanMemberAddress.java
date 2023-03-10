package com.bizzan.bitrade.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年01月26日
 */
@Builder
@Data
public class ScanMemberAddress {
    private long id;
    private String unit;
    private String remark;
    private String address;
    private Integer protocol;

    public static ScanMemberAddress toScanMemberAddress(MemberAddress memberAddress) {
        return ScanMemberAddress.builder().id(memberAddress.getId())
                .address(memberAddress.getAddress())
                .remark(memberAddress.getRemark())
                .unit(memberAddress.getCoin().getUnit())
                .protocol(memberAddress.getProtocol())
                .build();
    }
}
