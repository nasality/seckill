package com.bytedance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bytedance.pojo.Order;
import com.bytedance.pojo.SeckillOrder;
import com.bytedance.pojo.User;
import com.bytedance.vo.GoodsVo;

public interface ISeckillOrderService extends IService<SeckillOrder> {
    Order secKill(User user, GoodsVo goods);

    Long getResult(User user, Long goodsId);

    String createPath(User user, Long goodsId);

    boolean checkPath(String path, User user, Long goodsId);
}
