package com.enterprise.tasksuperviseserver.module.org.service.impl;

import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.org.dto.ChangePasswordDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.UpdateUserInfoDTO;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysUserMapper;
import com.enterprise.tasksuperviseserver.module.org.service.UserService;
import com.enterprise.tasksuperviseserver.module.org.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

/**
 * 用户个人信息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /** 允许的头像文件类型 */
    private static final Set<String> AVATAR_ALLOWED_TYPES = Set.of("jpg", "jpeg", "png", "gif", "webp");
    /** 头像文件大小限制（MB） */
    private static final long AVATAR_MAX_SIZE_MB = 5;
    /** 头像子目录 */
    private static final String AVATAR_DIR = "avatars";

    @Override
    public UserInfoVO getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        return getUserInfo(userId);
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return convert(user);
    }

    @Override
    public UserInfoVO updateUserInfo(UpdateUserInfoDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (StringUtils.hasText(dto.getName())) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
        return convert(user);
    }

    @Override
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录，请先登录");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        boolean oldOk = false;
        try {
            oldOk = passwordEncoder.matches(dto.getOldPassword(), user.getPassword());
        } catch (Exception ignored) {
        }
        if (!oldOk && dto.getOldPassword().equals(user.getPassword())) {
            oldOk = true;
        }
        if (!oldOk) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录，请先登录");
        }

        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的头像");
        }

        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName)) {
            throw new BusinessException("文件名不能为空");
        }

        // 验证文件类型
        String extension = getExtension(originalName);
        if (!AVATAR_ALLOWED_TYPES.contains(extension.toLowerCase())) {
            throw new BusinessException("仅支持 jpg/jpeg/png/gif/webp 格式的图片");
        }

        // 验证文件大小
        if (file.getSize() > AVATAR_MAX_SIZE_MB * 1024 * 1024) {
            throw new BusinessException("头像大小不能超过 " + AVATAR_MAX_SIZE_MB + "MB");
        }

        // 生成存储路径: avatars/yyyy/MM/dd/UUID.ext
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        String relativePath = AVATAR_DIR + "/" + datePath + "/" + storedName;

        // 保存文件
        Path targetPath = Paths.get(uploadDir, relativePath);
        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toFile());
        } catch (Exception e) {
            log.error("头像保存失败: {}", e.getMessage(), e);
            throw new BusinessException("头像保存失败: " + e.getMessage());
        }

        // 更新用户头像字段
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setAvatar(relativePath);
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        log.info("用户头像上传成功: userId={}, path={}", userId, relativePath);
        return relativePath;
    }

    @Override
    public Resource getAvatarResource(String filePath) {
        // 防止路径遍历攻击
        if (filePath.contains("..") || filePath.contains("~")) {
            throw new BusinessException(400, "非法路径");
        }

        Path targetPath = Paths.get(uploadDir, filePath);
        if (!Files.exists(targetPath)) {
            throw new BusinessException(404, "头像文件不存在");
        }
        return new FileSystemResource(targetPath);
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(dotIndex + 1) : "";
    }

    private UserInfoVO convert(SysUser user) {
        return UserInfoVO.builder()
                .id(user.getUserId())
                .name(user.getName() != null ? user.getName() : user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role(user.getRoleCode())
                .build();
    }
}
