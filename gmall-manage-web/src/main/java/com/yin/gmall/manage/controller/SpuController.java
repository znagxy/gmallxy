package com.yin.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yin.gmall.bean.*;
import com.yin.gmall.manage.util.PmsUploadUtil;
import com.yin.gmall.service.SpuService;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@Controller
public class SpuController {

    @Reference
    SpuService spuService;


    @RequestMapping("spuList")
    @ResponseBody
    public List<PmsProductInfo> spuList(String catalog3Id){
        return spuService.spuList(catalog3Id);
    }

    @RequestMapping("saveSpuInfo")
    @ResponseBody
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        String result = spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }


    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload (@RequestParam("file") MultipartFile multipartFile){
        String imageUrl = PmsUploadUtil.uploadImage(multipartFile);
        System.out.println(imageUrl);
        return imageUrl;
    }


    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId){
        return spuService.spuSaleAttrList(spuId);
    }

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<PmsProductImage> spuImageList(String spuId){
        return spuService.spuImageList(spuId);
    }


}
