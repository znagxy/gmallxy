package com.yin.gmall.service;

import com.yin.gmall.bean.PmsSearchParam;
import com.yin.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam);
}
