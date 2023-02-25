package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.CountryDao;
import com.bizzan.bitrade.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年02月10日
 */
@Service
public class CountryService {
    @Autowired
    private CountryDao countryDao;

    public List<Country> getAllCountry() {
        return countryDao.findAllOrderBySort();
    }

    public List<Country> getAllCountryApp() {
        return countryDao.findAllAppSupport();
    }

    public Country findOne(String zhName) {
        return countryDao.findByZhName(zhName);
    }

    public Country findOneE(String enName) {
        return countryDao.findByEnName(enName);
    }

}
