package com.nx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.nx.common.ErrorCode;
import com.nx.exception.BusinessException;
import com.nx.model.domain.User;
import com.nx.service.UserService;
import com.nx.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.nx.content.RegisterContent.*;
import static com.nx.content.UserContent.*;

/**
 * 用户服务实现类
 *
 * @author 18702
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-10-28 21:25:11
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "nixiang";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8");
        }
        //校验账户特殊字符
        String regEx = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        if (!userPassword.equals(checkPassword)) {
            return WRONG_PASSWORD_LENGTH;
        }
        //校验账户重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        //2.对密码进行加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newPassword);
        user.setUsername(userAccount);
        user.setAvatarUrl(DEFAULT_AVATAR);
        boolean result = save(user);
        if (!result) {
            return SEVER_ERROR;
        }
        log.info("user register succeeded[({}):{}]", user.getId(), userAccount);
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String regEx = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", newPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user[{}] login failed, userAccount cannot match userPassword", userAccount);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }
        //3.脱敏
        User safetyUser = getSafetyUser(user);
        //4.记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        log.info("user[{}] login succeeded", userAccount);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return {@link User }
     * @author nx
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 移除登录态
     *
     * @param request
     * @author nx
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


    /**
     * @param tagList
     * @return {@link List }<{@link User }>
     * @author nx
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //内存查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            if (StringUtils.isBlank(user.getTags())){
                return false;
            }
            Set<String> tagNames = gson.fromJson(user.getTags(), new TypeToken<Set<String>>() {
            }.getType());
            for (String tag : tagList) {
                if (!tagNames.contains(tag)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * @return {@link List }<{@link User }>
     * @author nx
     */
    @Deprecated
    private List<User> searchUsersByTagsBySQL(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //sql查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
}




