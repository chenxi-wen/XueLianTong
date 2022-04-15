package com.Zhengqing.controller;

import com.Zhengqing.controller.viewobject.UserVO;
import com.Zhengqing.erro.BusinessException;
import com.Zhengqing.erro.EmBusinessError;
import com.Zhengqing.responce.CommonReturnType;
import com.Zhengqing.responce.OtpCode;
import com.Zhengqing.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.Zhengqing.service.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


@Controller("user")
@RequestMapping("/user")
//前后端通信
/*   @CrossOrigin解决跨域请求问题
 *   No 'Access-Control-Allow-Origin' header is present on the requested resource
         *   加上这个注解后，就会让response对象返回'Access-Control-Allow-Origin' header 为 通配符*
         *   但是单纯的加注解，只是让跨域互通了，还不能实现互信
         *   需要再加两个参数，才能实现前后端互信
 */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController
{
    @Autowired
    private UserService userService;


    @Autowired
    private HttpServletRequest httpServletRequest;
    private HttpSession session;
    //用户获取otp短信接口
    //@RequestMapping(value = "/getOtp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @RequestMapping(value = "/getOtp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone") String telephone) {
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将OTP验证码同对应用户的手机号关联，使用httpsession的方式绑定手机号与OTPCDOE
        //httpServletRequest.getSession().setAttribute(telephone, otpCode);
        session = httpServletRequest.getSession();
        session.setAttribute(telephone, otpCode);

        //将OTP验证码通过短信通道发送给用户，省略
        System.out.println("telephone=" + telephone + "&otpCode=" + otpCode);

        // 4、将信息抽象为类
       OtpCode otpCodeObj = new OtpCode(telephone, otpCode);
        // 5、返回正确信息，方便前端获取
       return CommonReturnType.create(otpCodeObj, "successGetOtpCode");
        //return CommonReturnType.create(null);
    }
    //用户注册接口
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    //@RequestMapping(value = "/register")
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telephone") String telephone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") String gender,
                                     @RequestParam(name = "age") String age,
                                     @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telephone);
        if (!com.alibaba.druid.util.StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(Integer.valueOf(age));
        userModel.setGender(Byte.valueOf(gender));
        userModel.setTelephone(telephone);
        userModel.setRegisitMode("byphone");

        //密码加密
        userModel.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);
        return CommonReturnType.create(null);

    }

    //密码加密
    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    @RequestMapping("/get")
    @ResponseBody

//        public UserVO getUser(@RequestParam(name = "id") Integer id) {
//            //调用service服务获取对应id的用户对象并返回给前端
//            UserModel userModel = userService.getUserById(id);
//
//            //将核心领域模型用户对象转化为可供UI使用的viewobject
//            return convertFromModel(userModel);
//        }


    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException{
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO = convertFromModel(userModel);

        //返回通用对象
        return CommonReturnType.create(userVO);
    }


    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);
        return userVO;
    }
}
