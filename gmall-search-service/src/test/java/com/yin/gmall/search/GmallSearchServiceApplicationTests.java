package com.yin.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yin.gmall.bean.PmsSearchSkuInfo;
import com.yin.gmall.bean.PmsSkuInfo;
import com.yin.gmall.service.SkuSerivce;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GmallSearchServiceApplicationTests {

    @Reference
    SkuSerivce skuSerivce;

    @Autowired
    JestClient jestClient;

    @Test
    void contextLoads() throws InvocationTargetException, IllegalAccessException, IOException {

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        //jest dsl
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        TermQueryBuilder termQueryBuilder2 = new TermQueryBuilder("skuAttrValueList.valueId","43");
        boolQueryBuilder.filter(termQueryBuilder2);
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","小米5");
        boolQueryBuilder.must(matchQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);
        searchSourceBuilder.highlighter(null);

        String dsl = searchSourceBuilder.toString();



        Search search = new Search.Builder(dsl).addIndex("gmallpms").addType("pmsSkuInfo").build();
        SearchResult result =jestClient.execute(search);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            pmsSearchSkuInfos.add(source);
        }

        System.out.println(pmsSearchSkuInfos.size());

    }


    public void put() throws IOException {
        List<PmsSkuInfo> pmsSkuInfos = skuSerivce.getAllSku();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo , pmsSearchSkuInfo);
            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            Index put = new Index.Builder(pmsSearchSkuInfo).index("gmallpms").type("pmsSkuInfo").build();
            jestClient.execute(put);
        }
    }

}
