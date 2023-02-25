package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.ActivityRewardType;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.RewardActivitySetting;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年03月08日
 */
public interface RewardActivitySettingDao extends BaseDao<RewardActivitySetting> {
    RewardActivitySetting findByStatusAndType(BooleanEnum booleanEnum, ActivityRewardType type);
}
