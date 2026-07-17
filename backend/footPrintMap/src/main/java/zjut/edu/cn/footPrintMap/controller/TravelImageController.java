package zjut.edu.cn.footPrintMap.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import zjut.edu.cn.footPrintMap.common.result.Result;
import zjut.edu.cn.footPrintMap.common.result.ResultStatus;
import zjut.edu.cn.footPrintMap.dto.request.travelImageRequest.UploadTravelImageRequest;
import zjut.edu.cn.footPrintMap.entity.Travel;
import zjut.edu.cn.footPrintMap.entity.TravelImage;
import zjut.edu.cn.footPrintMap.entity.User;
import zjut.edu.cn.footPrintMap.service.TravelImageService;
import zjut.edu.cn.footPrintMap.service.TravelService;
import zjut.edu.cn.footPrintMap.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/travelImage")
public class TravelImageController {
    @Autowired
    private TravelImageService travelImageService;

    @Autowired
    private TravelService travelService;

    @Autowired
    private UserService userService;

    @PostMapping("/uploadTravelImage")
    public Result<String> uploadTravelImage(@RequestBody UploadTravelImageRequest uploadTravelImageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        Travel travel = travelService.getById(uploadTravelImageRequest.getTravelId());
        if(travel == null) {
            return Result.error(ResultStatus.NOT_FOUND, "游记不存在");
        }
        if(!travel.getUserId().equals(user.getId())) {
            return Result.error(ResultStatus.FORBIDDEN, "无权操作该游记");
        }
        List<TravelImage> images = travelImageService.list(
                new LambdaQueryWrapper<TravelImage>()
                        .eq(TravelImage::getTravelId,travel.getId())
        );
        for(TravelImage image:images){
            image.setSortOrder(image.getSortOrder()+1);
        }
        TravelImage travelImage = new TravelImage();
        BeanUtils.copyProperties(uploadTravelImageRequest,travelImage);
        boolean saved = travelImageService.save(travelImage);
        if(saved){
            travelImageService.updateBatchById(images);
        }
        return saved ? Result.success(travelImage.getId()):Result.error(ResultStatus.USE_FAILED);
    }

    //获取某游记的图片列表
    @GetMapping("/travelImageList")
    public Result<List<TravelImage>> getTravelImageList(@RequestParam String travelId) {
        Travel travel = travelService.getById(travelId);
        if (travel == null) {
            return Result.error(ResultStatus.NOT_FOUND, "游记不存在");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        if (!travel.getUserId().equals(user.getId())) {
            return Result.error(ResultStatus.FORBIDDEN, "无权查看该游记图片");
        }
        List<TravelImage> images = travelImageService.list(
                new LambdaQueryWrapper<TravelImage>()
                        .eq(TravelImage::getTravelId, travelId)
                        .orderByAsc(TravelImage::getSortOrder)
                        .orderByDesc(TravelImage::getCreatedTime)
        );
        return Result.success(images);
    }

    @DeleteMapping("/deleteTravelImage")
    public Result<Void> deleteTravelImage(@RequestParam String travelImageId) {
        TravelImage travelImage = travelImageService.getById(travelImageId);
        if (travelImage == null) {
            return Result.error(ResultStatus.NOT_FOUND, "图片不存在");
        }
        Travel travel = travelService.getById(travelImage.getTravelId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        if (travel == null || !travel.getUserId().equals(user.getId())) {
            return Result.error(ResultStatus.FORBIDDEN, "无权删除该图片");
        }
        boolean removed = travelImageService.removeById(travelImageId);
        if(removed){
            List<TravelImage> images = travelImageService.list(
                    new LambdaQueryWrapper<TravelImage>()
                            .eq(TravelImage::getTravelId,travel.getId())
                            .gt(TravelImage::getSortOrder,travelImage.getSortOrder())
                            .orderByAsc(TravelImage::getSortOrder)
            );
            for(TravelImage image:images){
                image.setSortOrder(image.getSortOrder()-1);
            }
        }
        return removed ? Result.success(null) : Result.error(ResultStatus.USE_FAILED);
    }
}
