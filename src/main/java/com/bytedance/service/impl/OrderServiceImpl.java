/*
package com.bytedance.service.impl;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bytedance.mapper.OrderMapper;
import com.bytedance.pojo.Order;
import com.bytedance.pojo.SeckillGoods;
import com.bytedance.pojo.User;
import com.bytedance.service.IGoodsService;
import com.bytedance.service.IOrderService;
import com.bytedance.service.ISeckillGoodsService;
import com.bytedance.service.ISeckillOrderService;
import com.bytedance.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private IGoodsService iGoodsService;
    @Autowired
    private ISeckillGoodsService iSeckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        //1 减少库存
        SeckillGoods SeckillGoods = iSeckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        //SeckillGoods.setStockCount(SeckillGoods.getStockCount() - 1);
        boolean b = iSeckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count=stock_count-1").eq("goods_id",goods.getId()).gt("stock_count",0));
        if (b == false){
            return null;
        }
        //2  生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliverAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getGoodsPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //3 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        //要明确，为什么这里用save而上面用insert，为了业务隔离
        //因为一套业务对应一套service和mapper.
        //你要用别人的mapper，你只能调用别人的service层  不能直接调用mapper层
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(),seckillOrder);
        return order;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        return null;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        return false;
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        return false;
    }

    @Override
    public OrderDetailVo detail(int orderId) {
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = iGoodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);
        return orderDetailVo;
    }
}
*/
