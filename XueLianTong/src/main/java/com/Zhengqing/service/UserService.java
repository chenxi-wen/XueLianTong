package com.Zhengqing.service;

import com.Zhengqing.erro.BusinessException;
import com.Zhengqing.service.model.UserModel;



public interface UserService {
    //通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);
    /* 用户获取手机验证码时需要用到 */
    //public Boolean getUserByTelephone(String telephone);

    void register(UserModel userModel) throws BusinessException;
}
