package com.yin.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yin.gmall.bean.PmsSkuInfo;
import com.yin.gmall.service.SkuSerivce;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin
@Controller
public class SkuController {

    @Reference
    SkuSerivce skuSerivce;

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());

        //chuli moren tupian
        String skuDefaultImg = pmsSkuInfo.getSkuDefaultImg();
        if(StringUtils.isBlank(skuDefaultImg)){
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
        }

        String result = skuSerivce.saveSkuInfo(pmsSkuInfo);
        return "success";
    }

}
