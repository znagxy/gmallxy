package com.yin.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yin.gmall.bean.PmsSearchParam;
import com.yin.gmall.bean.PmsSearchSkuInfo;
import com.yin.gmall.bean.PmsSkuAttrValue;
import com.yin.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;
    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {
        String dsl = getSearchDsl(pmsSearchParam);
        System.err.println(dsl);
        //TermQueryBuilder termQueryBuilder2 = new TermQueryBuilder("skuAttrValueList.valueId","43");
        //boolQueryBuilder.filter(termQueryBuilder2);
        /*MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","小米5");
        boolQueryBuilder.must(matchQueryBuilder);*/

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        Search search = new Search.Builder(dsl).addIndex("gmallpms").addType("pmsSkuInfo").build();
        SearchResult result = null;
        try {
            result = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = result.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            Map<String, List<String>> highlight = hit.highlight;
            if(highlight!=null){
                String skuName = highlight.get("skuName").get(0);
                source.setSkuName(skuName);
            }
            pmsSearchSkuInfos.add(source);
        }

        return pmsSearchSkuInfos;
    }

    private String getSearchDsl(PmsSearchParam pmsSearchParam) {
        String[] valueIds = pmsSearchParam.getValueId();
        //jest dsl

        //TermQueryBuilder termQueryBuilder2 = new TermQueryBuilder("skuAttrValueList.valueId","43");
        //boolQueryBuilder.filter(termQueryBuilder2);
        /*MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","小米5");
        boolQueryBuilder.must(matchQueryBuilder);*/
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if(StringUtils.isNotBlank(pmsSearchParam.getCatalog3Id())){
            TermQueryBuilder termQueryBuilder2 = new TermQueryBuilder("catalog3Id",pmsSearchParam.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder2);
        }

        if(valueIds!=null){
            for (String value : valueIds) {
                TermQueryBuilder termQueryBuilder2 = new TermQueryBuilder("skuAttrValueList.valueId",value);
                boolQueryBuilder.filter(termQueryBuilder2);
            }
        }
        if(StringUtils.isNotBlank(pmsSearchParam.getKeyword())){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",pmsSearchParam.getKeyword());
            boolQueryBuilder.must(matchQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort("id", SortOrder.DESC);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlighter(highlightBuilder);
        return searchSourceBuilder.toString();
    }
}
