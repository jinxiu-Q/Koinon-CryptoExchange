package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年12月29日
 */
@Data
public class RegisterByAppActive {

    @NotBlank(message = "{LoginByEmail.password.null}")
    @Length(min = 8, max = 20, message = "{LoginByEmail.password.length}")
    private String password;

    @NotBlank(message = "{LoginByEmail.username.null}")
    @Length(min = 5, max = 20, message = "{LoginByEmail.username.length}")
    private String username;

    @NotBlank(message = "{LoginByEmail.code.null}")
    private String code;

}
