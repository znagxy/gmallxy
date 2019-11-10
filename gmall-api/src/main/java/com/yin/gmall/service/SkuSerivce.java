package com.yin.gmall.service;

import com.yin.gmall.bean.PmsSkuInfo;

import java.util.List;

public interface SkuSerivce {

    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId,String ip);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);

    List<PmsSkuInfo> getAllSku();
}
