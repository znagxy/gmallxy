package com.yin.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yin.gmall.bean.PmsProductSaleAttr;
import com.yin.gmall.bean.PmsSkuImage;
import com.yin.gmall.bean.PmsSkuInfo;
import com.yin.gmall.bean.PmsSkuSaleAttrValue;
import com.yin.gmall.service.SkuSerivce;
import com.yin.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId , ModelMap map){
        PmsSkuInfo pmsSkuInfo = skuSerivce.getSkuById(skuId , "");
        map.put("skuInfo",pmsSkuInfo);

        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.spuSaleAttrListCheckBySku(pmsSkuInfo.getProductId() ,skuId);

        map.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);

        //search current sku of spu into hashmap
        Map<String , String> skuSaleAttrHash = new HashMap<>();
        List<PmsSkuInfo> pmsSkuInfos = skuSerivce.getSkuSaleAttrValueListBySpu(pmsSkuInfo.getProductId());

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String k = "";
            String v = skuInfo.getId();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
                k +=pmsSkuSaleAttrValue.getSaleAttrValueId() + "|";
            }
            skuSaleAttrHash.put(k , v);
        }
        String jsonStr = JSON.toJSONString(skuSaleAttrHash);
        System.out.println(jsonStr);
        map.put("skuSaleAttrJsonStr" , jsonStr);
        return "item";
    }

}
