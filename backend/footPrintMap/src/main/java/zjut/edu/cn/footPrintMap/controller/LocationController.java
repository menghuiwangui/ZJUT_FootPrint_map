package zjut.edu.cn.footPrintMap.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import zjut.edu.cn.footPrintMap.common.result.Result;
import zjut.edu.cn.footPrintMap.common.result.ResultStatus;
import zjut.edu.cn.footPrintMap.dto.request.locationRequest.AddLocationRequest;
import zjut.edu.cn.footPrintMap.dto.response.locationResponse.AddLocationResponse;
import zjut.edu.cn.footPrintMap.entity.Location;
import zjut.edu.cn.footPrintMap.entity.User;
import zjut.edu.cn.footPrintMap.service.LocationService;
import zjut.edu.cn.footPrintMap.service.UserService;

import java.util.List;
//import java.util.UUID;

@RestController
@RequestMapping("/api/location")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    //添加地点
    @PostMapping("/addLocation")
    public Result<AddLocationResponse> addLocation(@RequestBody AddLocationRequest addLocationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        Location location = getLocation1(addLocationRequest, user);
        boolean saved = locationService.save(location);
        return saved ? Result.success(new AddLocationResponse(location.getId(), location.getName())) : Result.error(ResultStatus.USE_FAILED);
    }

    private static @NonNull Location getLocation1(AddLocationRequest addLocationRequest, User user) {
        Location location = new Location();
        location.setUserId(user.getId());
        location.setName(addLocationRequest.getLocationName());
        location.setLat(addLocationRequest.getLat());
        location.setLng(addLocationRequest.getLng());
        location.setContinent(addLocationRequest.getContinent());
        location.setCountry(addLocationRequest.getCountry());
        location.setProvince(addLocationRequest.getProvince());
        location.setAddress(addLocationRequest.getAddress());
        return location;
    }

    //获取地点详情
    @GetMapping("/getLocation")
    public Result<Location> getLocation(@RequestParam String locationId) {
        Location location = locationService.getById(locationId);
        if (location == null) {
            return Result.error(ResultStatus.NOT_FOUND);
        }
        return Result.success(location);
    }

    //模糊搜索地名
    @PostMapping("/search")
    public Result<List<Location>> searchLocation(@RequestBody String keyword) {
        return Result.success(locationService
                .list(new LambdaQueryWrapper<Location>()
                        .like(Location::getName,keyword)
                        .or()
                        .like(Location::getContinent,keyword)
                        .or()
                        .like(Location::getCountry,keyword)
                        .or()
                        .like(Location::getProvince,keyword)
                        .or()
                        .like(Location::getAddress,keyword)));
    }

    //获取去过的所有地点
    @GetMapping("/locationList")
    public Result<List<Location>> getUserLocations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return Result.error(ResultStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByUsername(authentication.getName());
        return Result.success(locationService
                .list(new LambdaQueryWrapper<Location>()
                        .eq(Location::getUserId,user.getId())));
    }

    //删除地点

}
