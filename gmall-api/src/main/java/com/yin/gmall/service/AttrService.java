package com.yin.gmall.service;

import com.yin.gmall.bean.PmsBaseAttrInfo;
import com.yin.gmall.bean.PmsBaseAttrValue;
import com.yin.gmall.bean.PmsBaseSaleAttr;

import java.util.List;

public interface AttrService {

    List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseSaleAttr> baseSaleAttrList();
}
