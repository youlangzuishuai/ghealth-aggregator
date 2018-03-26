package com.todaysoft.ghealth.portal.mgmt.facade.report;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.todaysoft.document.generate.sdk.request.TableBookmarkContent;
import com.todaysoft.document.generate.sdk.request.TextBookmarkContent;
import com.todaysoft.ghealth.base.response.model.Drug;
import com.todaysoft.ghealth.config.RootContext;
import com.todaysoft.ghealth.mybatis.model.Dict;
import com.todaysoft.ghealth.mybatis.model.PharmacyTemModel;
import com.todaysoft.ghealth.mybatis.model.TestingItem;
import com.todaysoft.ghealth.mybatis.searcher.DictSearcher;
import com.todaysoft.ghealth.mybatis.searcher.DrugSearcher;
import com.todaysoft.ghealth.portal.mgmt.facade.DrugMgmtFacade;
import com.todaysoft.ghealth.service.IDictService;
import com.todaysoft.ghealth.service.impl.ServiceException;
import com.todaysoft.ghealth.service.impl.core.ItemLevelEvaluator;
import com.todaysoft.ghealth.service.impl.core.TestingItemAlgorithmConfig;
import com.todaysoft.ghealth.service.impl.core.TestingItemEvaluateResult;
import com.todaysoft.ghealth.service.impl.core.TestingItemLocusEvaluateConfig;
import com.todaysoft.ghealth.service.impl.core.TestingItemLocusEvaluateResult;

public class PharmacyReportContentsGenerator extends AbstractReportContentsGenerator
{
    private DrugMgmtFacade drugMgmtFacade = RootContext.getBean(DrugMgmtFacade.class);
    
    private IDictService dictService = RootContext.getBean(IDictService.class);
    
    private Map<Integer, String> resultMap = getResultMap();
    
    private Map<Integer, String> doseMap = getDoseMap();
    
    private final String DICT_CATEGORY = "DRUG";
    
    @Override
    protected List<TextBookmarkContent> generateTextBookmarkContents(ReportGenerateContext context)
    {
        List<TextBookmarkContent> contents = new ArrayList<>();
        List<TestingItemEvaluateResult> itemResults = context.getTestingItemEvaluateResults();
        
        for (TestingItemEvaluateResult itemResult : itemResults)
        {
            TestingItemAlgorithmConfig algorithmConfig = itemResult.getAlgorithmConfig();
            if (Objects.isNull(algorithmConfig))
            {
                continue;
            }
            TestingItem testingItem = algorithmConfig.getTestingItem();
            if (Objects.isNull(testingItem))
            {
                continue;
            }
            
            // 是安全用药的项目
            if (TestingItem.CATEGORY_PHARMACY_RISK.equals(testingItem.getCategory()))
            {
                Map<String, PharmacyTemModel> map = getCalPharmacyMap(algorithmConfig, itemResult.getLocusEvaluateResultsAsMap());
                map.forEach((k, v) -> {
                    contents.add(new TextBookmarkContent(MessageFormat.format("LOCUS_{0}_GENETYPE", k), v.getGeneType()));
                    contents.add(new TextBookmarkContent(MessageFormat.format("GENE_{0}_RESULT", k), v.getResult()));
                    contents.add(new TextBookmarkContent(MessageFormat.format("GENE_{0}_DOSE", k), v.getDose()));
                });
            }
        }
        
        return contents;
    }
    
    @Override
    protected List<TableBookmarkContent> generateTableBookmarkContents(ReportGenerateContext context)
    {
        List<TableBookmarkContent> contents = new ArrayList<>();
        List<TestingItemEvaluateResult> itemResults = context.getTestingItemEvaluateResults();
        
        for (TestingItemEvaluateResult itemResult : itemResults)
        {
            TestingItemAlgorithmConfig algorithmConfig = itemResult.getAlgorithmConfig();
            if (Objects.isNull(algorithmConfig))
            {
                continue;
            }
            TestingItem testingItem = algorithmConfig.getTestingItem();
            if (Objects.isNull(testingItem))
            {
                continue;
            }
            // 是安全用药的项目
            if (TestingItem.CATEGORY_PHARMACY_RISK.equals(testingItem.getCategory()))
            {
                Map<String, PharmacyTemModel> map = getCalPharmacyMap(algorithmConfig, itemResult.getLocusEvaluateResultsAsMap());
                List<Dict> dicts = getDicts();
                for (int i = 0; i < dicts.size(); i++)
                {
                    Dict dict = dicts.get(i);
                    
                    DrugSearcher searcher = new DrugSearcher();
                    searcher.setCategory(dict.getDictValue());
                    boolean isASME = false;
                    //成人安全用药
                    if ("ASME".equals(testingItem.getCode()))
                    {
                        searcher.setIsAdultUsed(1);
                        isASME = true;
                    }
                    else
                    {
                        searcher.setIsChildrenUsed(1);
                    }
                    
                    List<Drug> drugs = drugMgmtFacade.getDrugByCategory(searcher);
                    
                    if (CollectionUtils.isEmpty(drugs))
                    {
                        continue;
                    }
                    contents.add(getTableBookmarkContents(i + 1, drugs, map, isASME));
                }
            }
        }
        
        return contents;
    }
    
    private TableBookmarkContent getTableBookmarkContents(Integer i, List<Drug> drugs, Map<String, PharmacyTemModel> map, boolean isASME)
    {
        List<String[]> records = new ArrayList<>();
        for (Drug drug : drugs)
        {
            List<String> record = new ArrayList<>();
            if (isASME)
            {
                record.add(drug.getIngredientCn());
                record.add(drug.getIngredientEn());
                record.add(drug.getGeneName());
                record.add(getDetectionResult(map, drug));
            }
            else
            {
                record.add(drug.getProductName());
                record.add(drug.getIngredientCn());
                record.add(drug.getGeneName());
                record.add(getDetectionResult(map, drug));
            }
            records.add(record.toArray(new String[record.size()]));
        }
        
        TableBookmarkContent content = new TableBookmarkContent();
        content.setBookmarkName(MessageFormat.format("DRUG_CATEGORY_{0}", i));
        content.setRecords(records);
        return content;
    }
    
    private Map<String, PharmacyTemModel> getCalPharmacyMap(TestingItemAlgorithmConfig algorithmConfig, Map<String, TestingItemLocusEvaluateResult> map)
    {
        String geneName = "CYP2C19";
        String rs4244285 = "rs4244285";
        String rs4986893 = "rs4986893";
        int levelFour = 4;
        int levelFive = 5;
        
        Map<String, PharmacyTemModel> modelMap = new HashMap<>();
        List<PharmacyTemModel> pharmacyTemModels = new ArrayList<>();
        TestingItem testingItem = algorithmConfig.getTestingItem();
        
        Map<String, TestingItemLocusEvaluateConfig> locusConfigMap = algorithmConfig.getLocusConfig();
        
        for (Map.Entry<String, TestingItemLocusEvaluateConfig> locusConfig : locusConfigMap.entrySet())
        {
            TestingItemLocusEvaluateResult result = map.get(locusConfig.getKey());
            String locusName = result.getLocus().getName();
            
            int levelForFour = ItemLevelEvaluator.getLevelInterval(testingItem, result.getFactor(), levelFour);
            int levelForFive = ItemLevelEvaluator.getLevelInterval(testingItem, result.getFactor(), levelFive);
            PharmacyTemModel model = new PharmacyTemModel(result.getLocus().getName(), result.getGenetype(), resultMap.get(levelForFour), doseMap.get(levelForFive), levelForFour);
            if (rs4244285.equals(locusName) || rs4986893.equals(locusName))
            {
                pharmacyTemModels.add(model);
            }
            else
            {
                modelMap.put(result.getLocus().getGeneName(), model);
            }
        }
        
        getMapByGenes(geneName, pharmacyTemModels, modelMap);
        return modelMap;
    }
    
    private String getDetectionResult(Map<String, PharmacyTemModel> map, Drug drug)
    {
        List<Integer> list = new ArrayList<>();
        
        for (String geneName : drug.getGeneName().split(","))
        {
            if (StringUtils.isNotEmpty(geneName))
            {
                list.add(map.get(geneName).getLevel());
            }
        }
        
        Integer min = Collections.min(list);
        
        for (Map.Entry<String, PharmacyTemModel> entry : map.entrySet())
        {
            if (min.equals(entry.getValue().getLevel()))
            {
                return entry.getValue().getDose();
            }
        }
        return null;
    }
    
    private List<Dict> getDicts()
    {
        DictSearcher searcher = new DictSearcher();
        searcher.setCategory(DICT_CATEGORY);
        List<Dict> dicts = dictService.findList(searcher);
        
        if (CollectionUtils.isEmpty(dicts))
        {
            throw new ServiceException("没有配置用药类型的字典项");
        }
        return dicts;
    }
    
    private void getMapByGenes(String geneName, List<PharmacyTemModel> pharmacyTemModels, Map<String, PharmacyTemModel> modelMap)
    {
        List<PharmacyTemModel> sortedList = pharmacyTemModels.stream().sorted(Comparator.comparingInt(PharmacyTemModel::getLevel)).collect(Collectors.toList());
        modelMap.put(geneName, sortedList.get(0));
    }
    
    private Map<Integer, String> getResultMap()
    {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "不良型代谢");
        map.put(2, "中间型代谢");
        map.put(3, "广泛型代谢");
        map.put(4, "超速型代谢");
        return map;
    }
    
    private Map<Integer, String> getDoseMap()
    {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "减少剂量");
        map.put(2, "略减少剂量");
        map.put(3, "正常剂量");
        map.put(4, "略增加剂量");
        map.put(5, "增加剂量");
        return map;
    }
}
