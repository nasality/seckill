package com.itheida.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheida.pojo.Goods;
import com.itheida.vo.GoodsVo;

import java.util.List;


public interface IGoodsService extends IService<Goods> {
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
