package com.yin.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.yin.gmall.Constants.RedisConst;
import com.yin.gmall.bean.PmsSkuAttrValue;
import com.yin.gmall.bean.PmsSkuImage;
import com.yin.gmall.bean.PmsSkuInfo;
import com.yin.gmall.bean.PmsSkuSaleAttrValue;
import com.yin.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.yin.gmall.manage.mapper.PmsSkuImageMapper;
import com.yin.gmall.manage.mapper.PmsSkuInfoMapper;
import com.yin.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.yin.gmall.service.SkuSerivce;
import com.yin.gmall.utl.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuSerivce {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //cha yu sku
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        //插入平台属性相关
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }

        //插入销售属性相关

        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        //插入图片信息相关
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
    }

    public PmsSkuInfo getSkuById(String skuId , String ip) {
        System.out.println("ip为" +ip +"的用户" + Thread.currentThread().getName() +"进入商品详细请求");

        Jedis jedis = redisUtil.getJedis();
        PmsSkuInfo pmsSkuInfo = null;
        String skuKey= RedisConst.sku_prefix+skuId+ RedisConst.skuInfo_suffix;
        String skuInfoJson = jedis.get(skuKey);
        if(skuInfoJson!=null ){
            System.out.println("ip为" +ip +"的用户" + Thread.currentThread().getName() +"从缓存中获取商品详情");
            pmsSkuInfo = JSON.parseObject(skuInfoJson, PmsSkuInfo.class);
            jedis.close();
            return pmsSkuInfo;
        }else{
            System.out.println("ip为" +ip +"的用户" + Thread.currentThread().getName() +"发现缓存中没有，申请分布式锁" + "sku:"+ skuId+":lock");
            String token = UUID.randomUUID().toString();
            String okLock = jedis.set("sku:"+ skuId+":lock",token,"NX","EX",600);

            if(StringUtils.isNotBlank(okLock) && okLock.equals("OK")){
                System.out.println("ip为" +ip +"的用户" + Thread.currentThread().getName() +"成功拿到锁"+ token +" 访问数据库..." + "sku:"+ skuId+":lock");
                pmsSkuInfo = getSkuInfoDB(skuId);

                if(pmsSkuInfo!=null){
                    jedis.set(skuKey,JSON.toJSONString(pmsSkuInfo));
                }else{
                    jedis.setex(skuKey,60*3 ,JSON.toJSONString(""));
                }

                System.out.println("ip为" +ip +"的用户" + Thread.currentThread().getName() +"将锁归还 " + "sku:"+ skuId+":lock");

                /*String lockToken = jedis.get("sku:"+skuId+":lock");
                if(StringUtils.isNotBlank(lockToken) && lockToken.equals(token)){
                    jedis.del("sku:"+skuId+":lock");
                }*/
                String lockKey = "sku:"+skuId+":lock";
                String scripts = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                jedis.eval(scripts , Collections.singletonList(lockKey) , Collections.singletonList(token));

            }else{
                // 没拿到锁，自旋
                System.out.println("ip为" +ip +"的用户" + Thread.currentThread().getName() +"没有拿到锁开始自旋");
                return getSkuById(skuId, ip);
            }
           /* String skuInfoJsonStr = JSON.toJSONString(skuInfoDB);
            jedis.setex(skuKey,RedisConst.skuinfo_exp_sec,skuInfoJsonStr);
            System.err.println( Thread.currentThread().getName()+"：数据库更新完毕############### #####" );*/
            jedis.close();
            return pmsSkuInfo;
        }

    }


    public PmsSkuInfo getSkuInfoDB(String skuId) {
        //skuinfo
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        pmsSkuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        //skuimages
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        pmsSkuInfo.setSkuImageList(pmsSkuImages);

        return pmsSkuInfo;
    }


    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        return pmsSkuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(productId) ;
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            String skuId = pmsSkuInfo.getId();

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(skuId);
            List<PmsSkuAttrValue> select = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);

            pmsSkuInfo.setSkuAttrValueList(select);
        }
        return pmsSkuInfos;
    }


}
