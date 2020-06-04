package com.xcy.seckill.service;

import com.xcy.seckill.pojo.Goods;
import com.xcy.seckill.vo.GoodsVo;

import java.util.List;

public interface GoodsService {
    /**
     * 查询所有商品
     * @return
     */
    public List<GoodsVo> selAllGoods();

    /**
     * 根据商品ID查询商品
     * @param goodsId
     * @return
     */
    GoodsVo getGoodsVoByGoodsId(long goodsId);

    /**
     * 库存减1
     * @param goods
     */
    public void reduceStock(GoodsVo goods);
}
