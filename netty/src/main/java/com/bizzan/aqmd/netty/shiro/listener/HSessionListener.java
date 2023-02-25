package com.bizzan.aqmd.netty.shiro.listener;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

public class HSessionListener implements SessionListener {
    @Override
    public void onStart(Session session) {

        //System.out.println("会话创建：" + session.getId());
    }

    @Override
    public void onStop(Session session) {

    }

    @Override
    public void onExpiration(Session session) {

    }
}
