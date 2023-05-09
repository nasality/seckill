package com.itheida.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheida.pojo.Goods;
import com.itheida.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
