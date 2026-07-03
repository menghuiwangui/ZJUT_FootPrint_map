package zjut.edu.cn.FootPrintMap.common.result;

import lombok.Getter;

@Getter
public enum ResultStatus {
    PARAM_ERROR(400,"参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_FOUND(500,"系统内部错误");

    private final int code;
    private final String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
