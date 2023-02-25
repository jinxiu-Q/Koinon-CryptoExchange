package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.entity.Addressext;
import com.bizzan.bitrade.entity.Coinprotocol;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.AddressextService;
import com.bizzan.bitrade.service.CoinprotocolService;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * @author Hevin E-Mali:390330302@qq.com
 * @Description: coin
 * @date 2021/4/214:20
 */
@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController extends BaseController {

    @Autowired
    private AddressextService addressextService;
    @Autowired
    private CoinprotocolService coinprotocolService;
    @Autowired
    private RestTemplate restTemplate;

    // 读取地址
    @GetMapping("read")
    public MessageResult read(@SessionAttribute(SESSION_MEMBER) AuthMember user,
                              @RequestParam(value = "coinprotocol") Integer coinprotocol) {
        Integer memberid = (int) user.getId();
        Addressext read = addressextService.read(memberid, coinprotocol);

        return success(read);
    }


    // 创建地址
    @PostMapping("create")
    public MessageResult create(@SessionAttribute(SESSION_MEMBER) AuthMember user,
                                @RequestParam(value = "coinprotocol") Integer coinprotocol) {
        Integer memberid = (int) user.getId();
        Addressext read = addressextService.read(memberid, coinprotocol);

        if (read != null) {
            return success(read);
        }
        //获取地址
        Coinprotocol protocol = coinprotocolService.findByProtocol(coinprotocol);
        String account = "U" + memberid;
        //远程RPC服务URL,后缀为币种单位
        String serviceName = "SERVICE-RPC-" + protocol.getSymbol().toUpperCase();
        Addressext addressext=null;

        try {
            String url = "http://" + serviceName + "/rpc/address/{account}";
            ResponseEntity<MessageResult> result = restTemplate.getForEntity(url, MessageResult.class, account);
            log.info("remote call:service={},result={}", serviceName, result);
            if (result.getStatusCode().value() == 200) {
                MessageResult mr = result.getBody();
                if (mr.getCode() == 0) {
                    String address = mr.getData().toString();
                    addressext = new Addressext();
                    addressext.setMemberId(memberid);
                    addressext.setAddress(address);
                    addressext.setCoinProtocol(coinprotocol);
                    addressext.setStatus(1);
                    addressextService.save(addressext);
                }
            }
        } catch (Exception e) {
            log.error("call {} failed,error={}", serviceName, e.getMessage());
        }
        return success(addressext);
    }


}
