package com.yin.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yin.gmall.bean.PmsProductImage;
import com.yin.gmall.bean.PmsProductInfo;
import com.yin.gmall.bean.PmsProductSaleAttr;
import com.yin.gmall.bean.PmsProductSaleAttrValue;
import com.yin.gmall.manage.mapper.PmsProductImageMapper;
import com.yin.gmall.manage.mapper.PmsProductInfoMapper;
import com.yin.gmall.manage.mapper.PmsProductSaleAttrMapper;
import com.yin.gmall.manage.mapper.PmsProductSaleAttrValueMapper;
import com.yin.gmall.service.SpuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.PropertyMatches;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    PmsProductImageMapper pmsProductImageMapper;

    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;


    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        return pmsProductInfoMapper.select(pmsProductInfo);
    }

    @Override
    public String saveSpuInfo(PmsProductInfo pmsProductInfo) {

        String spuId = pmsProductInfo.getId();
        if(StringUtils.isBlank(spuId)){
            //create spu
            pmsProductInfoMapper.insertSelective(pmsProductInfo);
            spuId = pmsProductInfo.getId();
            List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductInfo.getSpuSaleAttrList();
            for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrs) {
                pmsProductSaleAttr.setProductId(spuId);
                pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);

                for (PmsProductSaleAttrValue pmsProductSaleAttrValue : pmsProductSaleAttr.getSpuSaleAttrValueList()) {
                    pmsProductSaleAttrValue.setProductId(spuId);
                    pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
                }
            }
            List<PmsProductImage> pmsProductImages = pmsProductInfo.getSpuImageList();
            for (PmsProductImage pmsProductImage : pmsProductImages) {
                pmsProductImage.setProductId(spuId);
                pmsProductImageMapper.insertSelective(pmsProductImage);
            }
        }else{
            //update

        }

        return "success";
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }
        return pmsProductSaleAttrs;
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        return pmsProductImageMapper.select(pmsProductImage);
    }

}
