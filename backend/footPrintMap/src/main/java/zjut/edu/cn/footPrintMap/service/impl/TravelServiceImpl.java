package zjut.edu.cn.footPrintMap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zjut.edu.cn.footPrintMap.entity.Travel;
import zjut.edu.cn.footPrintMap.mapper.TravelMapper;
import zjut.edu.cn.footPrintMap.service.TravelService;

@Service
public class TravelServiceImpl extends ServiceImpl<TravelMapper, Travel> implements TravelService {
}
