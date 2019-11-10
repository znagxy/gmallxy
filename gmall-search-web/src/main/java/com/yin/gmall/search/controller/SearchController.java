package com.yin.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yin.gmall.bean.*;
import com.yin.gmall.service.AttrService;
import com.yin.gmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.PublicKey;
import java.util.*;

@Controller
public class SearchController {

    @Reference
    SearchService searchService;

    @Reference
    AttrService attrService;

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam , ModelMap modelMap){

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchParam);
        modelMap.put("skuLsInfoList",pmsSearchSkuInfos);

        System.out.println("skuLsInfoList size=======" + pmsSearchSkuInfos.size());

        Set<String> valueIdSet = new HashSet<>();

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue skuAttrValue : skuAttrValueList) {
                String valueId = skuAttrValue.getValueId();
                valueIdSet.add(valueId);
            }
        }
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueListByValueIds(valueIdSet);
        String[] delValueIds = pmsSearchParam.getValueId();
        if(delValueIds!=null){
            List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
            for (String delValueId : delValueIds) {
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                // 生成面包屑的参数
                pmsSearchCrumb.setValueId(delValueId);
                pmsSearchCrumb.setUrlParam(getUrlParamForCrumb(pmsSearchParam, delValueId));
                while(iterator.hasNext()){
                    PmsBaseAttrInfo info = iterator.next();
                    for (PmsBaseAttrValue pmsBaseAttrValue : info.getAttrValueList()) {
                        String valueId = pmsBaseAttrValue.getId();
                        if(valueId.equals(delValueId)){
                            pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                            iterator.remove();
                        }
                    }
                }
                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
            modelMap.put("attrValueSelectedList",pmsSearchCrumbs);
        }
        modelMap.put("attrList",pmsBaseAttrInfos);
        String urlParam = getUrlParam(pmsSearchParam);
        modelMap.put("urlParam",urlParam);
        return "list";
    }

    private String getUrlParamForCrumb(PmsSearchParam pmsSearchParam, String delValueId) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] valueIds = pmsSearchParam.getValueId();

        String urlParam ="";
        if(StringUtils.isNotBlank(pmsSearchParam.getKeyword()))
            urlParam = urlParam + "keyword=" + keyword;
        if(StringUtils.isNotBlank(pmsSearchParam.getCatalog3Id()))
            urlParam = urlParam + "catalog3Id=" + catalog3Id;

        if(valueIds!=null){
            for (String valueId : valueIds) {
                if(!valueId.equals(delValueId)){
                    urlParam = urlParam + "&valueId=" + valueId;
                }
            }
        }
        return urlParam;
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] valueIds = pmsSearchParam.getValueId();

        String urlParam ="";
        if(StringUtils.isNotBlank(pmsSearchParam.getKeyword()))
            urlParam = urlParam + "keyword=" + keyword;
        if(StringUtils.isNotBlank(pmsSearchParam.getCatalog3Id()))
            urlParam = urlParam + "catalog3Id=" + catalog3Id;

        if(valueIds!=null){
            for (String valueId : valueIds) {
                urlParam = urlParam + "&valueId=" + valueId;
            }
        }

        return urlParam;
    }

}
