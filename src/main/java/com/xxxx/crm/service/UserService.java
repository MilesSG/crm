package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserService extends BaseService<User, Integer> {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户登录
     * 1. 参数判断，判断用户姓名、密码非空等，如果参数为空，则抛出异常给Controller层做相应的处理
     * 2. 调用Dao数据访问层，通过用户名查询用户记录，返回用户对象
     * 3. 判断用户对象是否为空，如果对象为空，则也抛出异常给Controller层做相应的处理
     * 4. 如果存在用户记录，则判断前端传来的密码是否和数据库中的密码是否相等，如果密码不正确，则也抛出异常给Controller层做相应的处理
     * 5. 如果前面的所有操作都正确，则登录成功
     *
     * @param userName
     * @param userPwd
     */
    public UserModel userLogin(String userName, String userPwd) {
        checkLoginParams(userName, userPwd);
        User user = userMapper.queryUserByName(userName);
        AssertUtil.isTrue(user == null, "用户姓名不存在!");
        checkUserPwd(userPwd, user.getUserPwd());
        return buildUserInfo(user);
    }

    // 校验用户名和密码，如果为空，最后抛给Controller处理
    private void checkLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户姓名不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空!");
    }

    // 校验用户传来的密码(userPwd)和数据库的密码(pwd)是否一致
    private void checkUserPwd(String userPwd, String pwd) {
        userPwd = Md5Util.encode(userPwd);
        AssertUtil.isTrue(!userPwd.equals(pwd), "用户密码不正确!");
    }

    // 为了减少传输量，将用户信息封装成UserModel对象传给前台
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId())); // 设置加密后的用户ID
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 修改密码
     * 1. 接收四个参数：用户ID、原始密码、新密码、确认密码
     * 2. 通过用户ID查询用户记录，返回一个用户对象
     * 3. 参数校验：
     * 包括待更新的用户记录是否存在、
     * 原始密码是否为空、
     * 原始密码是否正确(查询的用户对象的新密码是否与原始密码一致，要保持不一致才行)、
     * 判断新密码是否为空、
     * 新密码是否和确认密码一样、
     * 不允许新密码与原始密码一致
     * 4. 设置用户新密码，将新密码通过MD5算法进行加密
     * 5. 执行更新操作，判断受影响的行数
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePassword(Integer userId, String oldPwd, String newPwd, String repeatPwd) {
        User user = userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(user == null, "待更新用户不存在!");
        checkPasswordParams(user, oldPwd, newPwd, repeatPwd);
        user.setUserPwd(Md5Util.encode(newPwd));
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) != 1, "修改密码失败!");
    }

    private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "原始密码不能为空!");
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)), "原始密码不正确!");
        AssertUtil.isTrue(!StringUtils.isBlank(newPwd), "新密码不能为空!");
        AssertUtil.isTrue(!newPwd.equals(oldPwd), "新密码不能与旧密码一致!");
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空!");
        AssertUtil.isTrue(!newPwd.equals(repeatPwd), "确认密码与新密码不一致!");
    }
}
