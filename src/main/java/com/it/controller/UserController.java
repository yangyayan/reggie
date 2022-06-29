package com.it.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.common.R;
import com.it.entity.User;
import com.it.service.UserService;
import com.it.utils.ClientUtils;
import com.it.utils.SMSUtils;
import com.it.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController{
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request){
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //验证码发送接口
            //ClientUtils.sendMessage(phone,code);
            request.getSession().setAttribute(phone,code);
            return R.success("短信验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> map,HttpServletRequest request){
        String phone = map.get("phone");
        String code = map.get("code");

        String codeInSession = (String) request.getSession().getAttribute(phone);

        if (codeInSession.equals(code)){
            User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
            if (user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            request.getSession().setAttribute("user",user.getId());
            return  R.success(user);
        }
        return R.error("验证失败");
    }
}
