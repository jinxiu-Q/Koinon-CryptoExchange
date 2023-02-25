package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年01月16日
 */
@Data
public class BindWechat {
    @NotBlank(message = "{BindWechat.realName.null}")
    private String realName;
    @NotBlank(message = "{BindWechat.wechat.null}")
    private String wechat;
    @NotBlank(message = "{BindWechat.jyPassword.null}")
    private String jyPassword;
    /** 微信收款二维码 */
    private String qrCodeUrl;
}
