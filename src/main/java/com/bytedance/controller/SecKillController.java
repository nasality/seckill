package com.bytedance.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bytedance.pojo.*;
import com.bytedance.rabbitmq.MQSender;
import com.bytedance.service.IGoodsService;
import com.bytedance.service.ISeckillGoodsService;
import com.bytedance.service.ISeckillOrderService;
import com.bytedance.util.JsonUtil;
import com.bytedance.vo.GoodsVo;
import com.bytedance.vo.RespBean;
import com.bytedance.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/seckill")
@Controller
public class SecKillController implements InitializingBean {
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender sender;
    //在內存中做标记（内存标记），标记当前商品是否有库存
    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    /**
     * 优化前 QPS 244  出现秒杀超卖问题
     * 前后端分离优化后QPS 244
     * Redis 优化后 1700
     *
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path,  User user, Long goodsId) {
        //判断用户是否登陆
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations ops = redisTemplate.opsForValue();
        //不对数据库进行操作
       /* GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }*/

        boolean check = seckillOrderService.checkPath(path, user, goodsId);
        if (!check) {
            return RespBean.error(RespBeanEnum.STATUS_ILLEGAL);
        }


        //检查重复购买
        //SeckillOrder secKillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        SeckillOrder secKillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (secKillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //检查内存标记 减少对Redis无意义的访问
        if (!emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        Long stock = ops.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            emptyStockMap.put(goodsId, false);
            //如果库存小于0，ops.decrement后会变成-1，要给加回0
            ops.increment("seckillGoods:" + goodsId);
            return  RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //创建该对象用于向mq发送用户和商品，用于下单
        SeckillMessage message = new SeckillMessage(user, goodsId);
        //将对象转换成JSON串放入mq
        sender.sendSeckillMessage(JsonUtil.object2JsonStr(message));

        //Order order = seckillOrderService.secKill(user,goods);
        /*if (order == null) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }*/
        return RespBean.success();
    }

    //实现InitializingBean接口，重写方法，当系统启动，启动流程加载完配置文件后自动执行这个方法
    //在系统初始化的时候，读取数据库秒杀商品，将商品库存存入redis中
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        //正常情况下应该加过期时间
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), true);
        });
    }

    @ResponseBody
    @RequestMapping("/result")
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    @ResponseBody
    @RequestMapping("/path")
    public RespBean getPath(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        String path = seckillOrderService.createPath(user, goodsId);
        return RespBean.success(path);
    }
}
