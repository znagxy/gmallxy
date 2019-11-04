package com.yin.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yin.gmall.bean.PmsProductSaleAttr;
import com.yin.gmall.bean.PmsSkuImage;
import com.yin.gmall.bean.PmsSkuInfo;
import com.yin.gmall.service.SkuSerivce;
import com.yin.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ItemController {

    @Reference
    SkuSerivce skuSerivce;

    @Reference
    SpuService spuService;

    @RequestMapping("index")
    public String index(ModelMap modelMap){
        modelMap.put("item","aaaaaaaaaa");
        return "index";
    }

    @RequestMapping("{skuId}")
    public String item(@PathVariable String skuId , ModelMap map){
        PmsSkuInfo pmsSkuInfo = skuSerivce.getSkuById(skuId);
        map.put("skuInfo",pmsSkuInfo);

        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId());

        map.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        return "item";
    }

}
