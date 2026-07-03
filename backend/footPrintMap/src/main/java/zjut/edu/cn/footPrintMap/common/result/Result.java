package zjut.edu.cn.FootPrintMap.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(ResultStatus resultStatus) {
        Result<T> result = new Result<>();
        result.code = resultStatus.getCode();
        result.message = resultStatus.getMessage();
        return result;
    }

    public static <T> Result<T> error(ResultStatus resultStatus,String customMessage) {
        Result<T> result = new Result<>();
        result.code = resultStatus.getCode();
        result.message = customMessage;
        return result;
    }
}
