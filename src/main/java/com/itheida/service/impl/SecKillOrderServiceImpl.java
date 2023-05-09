package com.itheida.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheida.mapper.OrderMapper;
import com.itheida.mapper.SeckillOrderMapper;
import com.itheida.pojo.Order;
import com.itheida.pojo.SeckillGoods;
import com.itheida.pojo.SeckillOrder;
import com.itheida.pojo.User;
import com.itheida.service.ISeckillGoodsService;
import com.itheida.service.ISeckillOrderService;
import com.itheida.util.MD5Util;
import com.itheida.util.UUIDUtil;
import com.itheida.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class SecKillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    //设置为事务
    @Transactional
    @Override
    public Order secKill(User user, GoodsVo goods) {

        //减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        //seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //由于Mybatis-plus会对所有字段进行set，效率较低，超卖问题可能与其有关
        //seckillGoodsService.updateById(seckillGoods);

        //现将其更改为自定义Sql, gt -> greater than
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1")
                .eq("goods_id", goods.getId()).gt("stock_count", 0));
        if (seckillGoods.getStockCount() < 1) {
            redisTemplate.opsForValue().set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        //业务隔离，order用mapper保存，这里用service
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);
        return order;
    }

    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            return seckillOrder.getOrderId();
            //不能以Redis中的库存作为依据
        } else if (redisTemplate.hasKey("isStockEmpty" + goodsId)) {
            return -1L;
        } else {
            return 0L;
        }
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid() + "abcd");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, path, 1, TimeUnit.MINUTES);
        return path;
    }

    @Override
    public boolean checkPath(String path, User user, Long goodsId) {
        if (user == null || goodsId < 0 || !StringUtils.hasLength(path)) {
            return false;
        }
        String check = (String)redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(check);
    }
}
