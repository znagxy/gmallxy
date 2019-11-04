package com.yin.gmall.service;

import com.yin.gmall.bean.PmsSkuInfo;

import java.util.List;

public interface SkuSerivce {

    String saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);
}
