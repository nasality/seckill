package com.itheida.rabbitmq;

import com.itheida.pojo.SeckillMessage;
import com.itheida.pojo.SeckillOrder;
import com.itheida.pojo.User;
import com.itheida.service.IGoodsService;
import com.itheida.service.ISeckillOrderService;
import com.itheida.util.JsonUtil;
import com.itheida.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ISeckillOrderService orderService;
    @RabbitListener(queues = "seckillQueue")
    public void receive(String messgae) {
        log.info("接收消息" + messgae);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(messgae, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        //在数据库中判断商品库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() < 1) {
            return;
        }

        //判断是否重复购买
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return ;
        }
        //TODO 此处使用seckillOrderService还是orderService有争议
        orderService.secKill(user,goodsVo);
    }
}
