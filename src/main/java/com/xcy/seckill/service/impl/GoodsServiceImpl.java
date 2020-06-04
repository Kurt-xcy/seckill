package com.xcy.seckill.service.impl;

import com.xcy.seckill.mapper.GoodsMapper;
import com.xcy.seckill.pojo.Goods;
import com.xcy.seckill.pojo.SeckillGoods;
import com.xcy.seckill.service.GoodsService;
import com.xcy.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Override
    public List<GoodsVo> selAllGoods() {
        List<GoodsVo> goodsList = goodsMapper.listGoodsVo();
        return goodsList;
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public void reduceStock(GoodsVo goods) {
        SeckillGoods g = new SeckillGoods();
        g.setGoodsId(goods.getId());
        goodsMapper.reduceStock(g);
    }

}
