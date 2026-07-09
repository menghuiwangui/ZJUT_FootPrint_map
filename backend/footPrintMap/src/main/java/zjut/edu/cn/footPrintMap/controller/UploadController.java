package zjut.edu.cn.footPrintMap.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import zjut.edu.cn.footPrintMap.common.result.Result;
import zjut.edu.cn.footPrintMap.common.result.ResultStatus;
import zjut.edu.cn.footPrintMap.myConst.MyPathConst;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @PostMapping("/avatarImage")
    public Result<String> uploadAvatarImage(@RequestParam("file")MultipartFile file) {
        // 判空
        if(file.isEmpty()) {
            return Result.error(ResultStatus.NOT_FOUND,"文件不能为空");
        }
        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(ResultStatus.PARAM_ERROR,"只能上传图片文件");
        }
        // 创建目录
        File dir = new File(MyPathConst.getAvatarPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = UUID.randomUUID() + file.getOriginalFilename();
        File image = new File(dir,filename);
        try {
            file.transferTo(image);
        } catch (IOException e) {
            return Result.error(ResultStatus.SYSTEM_FOUND, "文件上传失败");
        }
        String imageURL = "http://localhost:8080/uploads/avatar/"+filename;
        return Result.success(imageURL);
    }

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file")MultipartFile file) {
        // 判空
        if(file.isEmpty()) {
            return Result.error(ResultStatus.NOT_FOUND,"文件不能为空");
        }
        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(ResultStatus.PARAM_ERROR,"只能上传图片文件");
        }
        // 创建目录
        File dir = new File(MyPathConst.getTravelImagePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = UUID.randomUUID() + file.getOriginalFilename();
        File image = new File(dir,filename);
        try {
            file.transferTo(image);
        } catch (IOException e) {
            return Result.error(ResultStatus.SYSTEM_FOUND, "文件上传失败");
        }
        String imageURL = "http://localhost:8080/uploads/travelImage/"+filename;
        return Result.success(imageURL);
    }
}
