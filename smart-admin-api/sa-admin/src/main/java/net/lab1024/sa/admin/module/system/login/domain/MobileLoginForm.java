package net.lab1024.sa.admin.module.system.login.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.swagger.SchemaEnum;
import net.lab1024.sa.base.common.validator.enumeration.CheckEnum;
import net.lab1024.sa.base.constant.LoginDeviceEnum;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 手机端员工登录，去除验证码
 *
 */
@Data
public class MobileLoginForm {

    @Schema(description = "登录账号")
    @NotBlank(message = "登录账号不能为空")
    @Length(max = 30, message = "登录账号最多30字符")
    private String loginName;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @SchemaEnum(desc = "登录终端", value = LoginDeviceEnum.class)
    @CheckEnum(value = LoginDeviceEnum.class, required = true, message = "此终端不允许登录")
    private Integer loginDevice;
}