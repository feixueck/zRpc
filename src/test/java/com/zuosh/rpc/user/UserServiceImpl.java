package com.zuosh.rpc.user;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserServiceImpl implements UserService {
    @Override
    public String sayHello(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateInfo = format.format(new Date());
        String retInfo = String.format("%s : %s", dateInfo, s);
        return retInfo;
    }
}
