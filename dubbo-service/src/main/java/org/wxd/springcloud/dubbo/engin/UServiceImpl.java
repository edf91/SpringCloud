package org.wxd.springcloud.dubbo.engin;

import com.alibaba.dubbo.config.annotation.Service;
import org.wxd.springcloud.dubbo.api.UService;
import org.wxd.springcloud.dubbo.api.User;

/**
 * Created by wangxd on 2017/10/25.
 */
@Service(version = "1.0.0")
public class UServiceImpl implements UService{

    public User ofId(String id) {
        return new User().id(id).name("dubbo-service");
    }
}
