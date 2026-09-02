package com.enterprise.tasksuperviseserver.module.org.service;

import com.enterprise.tasksuperviseserver.module.org.dto.ChangePasswordDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.UpdateUserInfoDTO;
import com.enterprise.tasksuperviseserver.module.org.vo.UserInfoVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户个人信息服务（当前登录用户的资料管理）
 */
public interface UserService {

    /**
     * 获取当前登录用户信息
     */
    UserInfoVO getUserInfo();

    /**
     * 获取指定用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新当前登录用户信息
     */
    UserInfoVO updateUserInfo(UpdateUserInfoDTO dto);

    /**
     * 修改密码
     */
    void changePassword(ChangePasswordDTO dto);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 获取头像文件资源
     *
     * @param filePath 头像文件相对路径
     * @return 文件资源
     */
    Resource getAvatarResource(String filePath);
}
