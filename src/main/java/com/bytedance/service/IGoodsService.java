package com.bytedance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bytedance.pojo.Goods;
import com.bytedance.vo.GoodsVo;
import com.bytedance.vo.RespBean;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IGoodsService extends IService<Goods> {
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
