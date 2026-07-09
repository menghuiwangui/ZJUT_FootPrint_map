package zjut.edu.cn.footPrintMap.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import zjut.edu.cn.footPrintMap.common.result.Result;
import zjut.edu.cn.footPrintMap.common.result.ResultStatus;
import zjut.edu.cn.footPrintMap.dto.request.travelRequest.AddTravelRequest;
import zjut.edu.cn.footPrintMap.dto.request.travelRequest.UpdateTravelRequest;
import zjut.edu.cn.footPrintMap.dto.response.travelResponse.AddTravelResponse;
import zjut.edu.cn.footPrintMap.entity.Travel;
import zjut.edu.cn.footPrintMap.entity.User;
import zjut.edu.cn.footPrintMap.service.TravelService;
import zjut.edu.cn.footPrintMap.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/travel")
public class TravelController {
    @Autowired
    private TravelService travelService;

    @Autowired
    private UserService userService;

    //添加游记
    @PostMapping("/addTravel")
    public Result<AddTravelResponse> addTravel(@RequestBody AddTravelRequest addTravelRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        Travel travel = new Travel();
        travel.setUserId(user.getId());
        BeanUtils.copyProperties(addTravelRequest,travel);
        boolean saved = travelService.save(travel);
        return saved ? Result.success(new AddTravelResponse(travel.getId())) : Result.error(ResultStatus.USE_FAILED);
    }

    //获取游记详情
    @GetMapping("/getTravel")
    public Result<Travel> getTravel(@RequestParam String travelId){
        Travel travel = travelService.getById(travelId);
        if(travel == null) {
            return Result.error(ResultStatus.NOT_FOUND);
        }
        return Result.success(travel);
    }

    //获取用户游记列表
    @GetMapping("/travelList")
    public Result<List<Travel>> getTravelListByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        return Result.success(travelService.list(new LambdaQueryWrapper<Travel>()
                .eq(Travel::getUserId,user.getId())
                .orderByDesc(Travel::getVisitTime)));
    }

    // 分页查询游记
    @GetMapping("/page")
    public Result<IPage<Travel>> pageTravel(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String locationId) {
        LambdaQueryWrapper<Travel> wrapper = new LambdaQueryWrapper<>();
        if(locationId != null) {
            wrapper.eq(Travel::getLocationId,locationId);
        }
        wrapper.orderByDesc(Travel::getVisitTime);
        Page<Travel> page = travelService.page(new Page<>(current,size),wrapper);
        return Result.success(page);
    }

    //更新游记
    @PutMapping("/updateTravel")
    public Result<Void> updateTravel(@RequestBody UpdateTravelRequest updateTravelRequest) {
        Travel travel = travelService.getById(updateTravelRequest.getTravelId());
        if(travel == null) {
            return Result.error(ResultStatus.NOT_FOUND);
        }
        BeanUtils.copyProperties(updateTravelRequest,travel);
        boolean updated = travelService.updateById(travel);
        return updated ? Result.success(null) : Result.error(ResultStatus.USE_FAILED);
    }

    //删除游记
    @DeleteMapping("deleteTravel")
    public Result<Void> deleteTravel(@RequestParam String travelId) {
        boolean removed = travelService.removeById(travelId);
        return removed ? Result.success(null):Result.error(ResultStatus.USE_FAILED);
    }
}
