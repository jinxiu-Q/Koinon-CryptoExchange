package com.bizzan.bitrade.entity.transform;

import lombok.Data;

import java.util.List;

/**
 *
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2019-5-14 12:30:47
 */
@Data
public class Special<E> {
    private List<E> context;
}
