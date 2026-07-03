package zjut.edu.cn.footPrintMap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zjut.edu.cn.footPrintMap.entity.Location;
import zjut.edu.cn.footPrintMap.mapper.LocationMapper;
import zjut.edu.cn.footPrintMap.service.LocationService;

@Service
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {
}
