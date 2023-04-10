package com.bytedance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bytedance.pojo.Goods;
import com.bytedance.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
