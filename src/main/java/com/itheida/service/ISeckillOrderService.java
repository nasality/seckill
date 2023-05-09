package com.itheida.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheida.pojo.Order;
import com.itheida.pojo.SeckillOrder;
import com.itheida.pojo.User;
import com.itheida.vo.GoodsVo;

public interface ISeckillOrderService extends IService<SeckillOrder> {
    Order secKill(User user, GoodsVo goods);

    Long getResult(User user, Long goodsId);

    String createPath(User user, Long goodsId);

    boolean checkPath(String path, User user, Long goodsId);
}
