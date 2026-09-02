package com.enterprise.tasksuperviseserver.module.org.controller;

import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.org.dto.ChangePasswordDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.UpdateUserInfoDTO;
import com.enterprise.tasksuperviseserver.module.org.service.UserService;
import com.enterprise.tasksuperviseserver.module.org.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户个人信息接口
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(userService.getUserInfo());
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/info")
    public Result<UserInfoVO> updateUserInfo(@RequestBody UpdateUserInfoDTO dto) {
        return Result.success(userService.updateUserInfo(dto));
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return Result.success();
    }

    /**
     * 上传头像
     *
     * @param file 头像文件（jpg/jpeg/png/gif/webp，最大5MB）
     * @return 头像访问路径
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return Result.success("头像上传成功", userService.uploadAvatar(file));
    }

    /**
     * 获取头像文件
     *
     * @param year  年份
     * @param month 月份
     * @param day   日期
     * @param filename 文件名
     * @return 头像图片
     */
    @GetMapping("/avatar/{year}/{month}/{day}/{filename}")
    public ResponseEntity<Resource> getAvatar(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String filename) {
        String filePath = "avatars/" + year + "/" + month + "/" + day + "/" + filename;
        Resource resource = userService.getAvatarResource(filePath);

        // 根据文件扩展名推断 Content-Type
        MediaType mediaType = resolveImageMediaType(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .body(resource);
    }

    /**
     * 根据文件名推断图片 MediaType
     */
    private MediaType resolveImageMediaType(String filename) {
        if (filename == null) return MediaType.APPLICATION_OCTET_STREAM;
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (lower.endsWith(".webp")) return new MediaType("image", "webp");
        if (lower.endsWith(".bmp")) return new MediaType("image", "bmp");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
