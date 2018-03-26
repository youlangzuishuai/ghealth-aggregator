package com.todaysoft.ghealth.portal.mgmt.facade.report;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.todaysoft.document.generate.sdk.request.CaseBookmarkContent;
import com.todaysoft.document.generate.sdk.request.TextBookmarkContent;
import com.todaysoft.ghealth.mybatis.model.TestingItem;
import com.todaysoft.ghealth.service.impl.core.ItemLevelEvaluator;
import com.todaysoft.ghealth.service.impl.core.TestingItemAlgorithmConfig;
import com.todaysoft.ghealth.service.impl.core.TestingItemEvaluateReferenceValue;
import com.todaysoft.ghealth.service.impl.core.TestingItemEvaluateResult;
import com.todaysoft.ghealth.service.impl.core.TestingItemLocusEvaluateConfig;
import com.todaysoft.ghealth.service.impl.core.TestingItemLocusEvaluateResult;

public class TestingItemContentsGenerator extends AbstractReportContentsGenerator
{
    private final Integer LevelForThree = 3;
    
    private final Integer LevelForFive = 5;
    
    private final Integer LevelForNine = 9;
    
    @Override
    protected List<TextBookmarkContent> generateTextBookmarkContents(ReportGenerateContext context)
    {
        if (CollectionUtils.isEmpty(context.getTestingItemEvaluateResults()))
        {
            return null;
        }
        
        List<TextBookmarkContent> testingItemContents;
        List<TextBookmarkContent> contents = new ArrayList<TextBookmarkContent>();
        
        for (TestingItemEvaluateResult result : context.getTestingItemEvaluateResults())
        {
            testingItemContents = generateTestingItemTextBookmarkContents(result, context.getCustomer().getSex());
            contents.addAll(testingItemContents);
        }
        
        return contents;
    }
    
    @Override
    protected List<CaseBookmarkContent> generateCaseBookmarkContents(ReportGenerateContext context)
    {
        if (CollectionUtils.isEmpty(context.getTestingItemEvaluateResults()))
        {
            return null;
        }
        
        List<CaseBookmarkContent> testingItemContents;
        List<CaseBookmarkContent> contents = new ArrayList<CaseBookmarkContent>();
        
        for (TestingItemEvaluateResult result : context.getTestingItemEvaluateResults())
        {
            testingItemContents = generateTestingItemCaseBookmarkContents(result, context.getCustomer().getSex());
            contents.addAll(testingItemContents);
        }
        
        return contents;
    }
    
    private List<CaseBookmarkContent> generateTestingItemCaseBookmarkContents(TestingItemEvaluateResult result, String sex)
    {
        List<CaseBookmarkContent> contents = new ArrayList<CaseBookmarkContent>();
        String testingItemCode = result.getAlgorithmConfig().getTestingItem().getCode();
        
        int level = getLevelAsThreeIntervals(result, sex);
        contents.add(buildCaseBookmarkContent(testingItemCode, level));
        return contents;
    }
    
    private int getLevelAsThreeIntervals(TestingItemEvaluateResult result, String sex)
    {
        Double value = getItemCalculatedValue(result, sex);
        if (null == value)
        {
            return 0;
        }
        return ItemLevelEvaluator.getLevelInterval(result.getAlgorithmConfig().getTestingItem(), value, LevelForThree);
        
    }
    
    private Double getItemCalculatedValue(TestingItemEvaluateResult result, String sex)
    {
        String evalAlgorithm = result.getAlgorithmConfig().getTestingItem().getEvalAlgorithm();
        if (TestingItem.ALGORITHM_MC.equals(evalAlgorithm))
        {
            Double referenceValue = null;
            Double evaluateValue = result.getTestingItemEvaluateValue();
            
            if (null == evaluateValue)
            {
                return null;
            }
            
            TestingItemEvaluateReferenceValue evaluateReference = result.getAlgorithmConfig().getReferenceValue();
            
            if (null == evaluateReference)
            {
                return null;
            }
            else
            {
                referenceValue = evaluateReference.getValue(sex);
                
                if (null == referenceValue)
                {
                    return null;
                }
            }
            
            if (null != evaluateValue && null != referenceValue && referenceValue != 0)
            {
                return evaluateValue / referenceValue;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return result.getTestingItemEvaluateValue();
        }
    }
    
    private CaseBookmarkContent buildCaseBookmarkContent(String testingItemCode, int level)
    {
        Set<String> group = new HashSet<String>();
        group.add(MessageFormat.format("I_{0}_EVALUATE_H", testingItemCode));
        group.add(MessageFormat.format("I_{0}_EVALUATE_N", testingItemCode));
        group.add(MessageFormat.format("I_{0}_EVALUATE_L", testingItemCode));
        
        String matched;
        
        if (level == 0)
        {
            matched = MessageFormat.format("I_{0}_EVALUATE_N", testingItemCode);
        }
        else if (level == 1)
        {
            matched = MessageFormat.format("I_{0}_EVALUATE_L", testingItemCode);
        }
        else if (level == 2)
        {
            matched = MessageFormat.format("I_{0}_EVALUATE_N", testingItemCode);
        }
        else if (level == 3)
        {
            matched = MessageFormat.format("I_{0}_EVALUATE_H", testingItemCode);
        }
        else
        {
            throw new IllegalStateException();
        }
        
        CaseBookmarkContent content = new CaseBookmarkContent();
        content.setMatchedBookmarkName(matched);
        content.setBookmarkNames(group);
        return content;
    }
    
    private List<TextBookmarkContent> generateTestingItemTextBookmarkContents(TestingItemEvaluateResult result, String sex)
    {
        List<TextBookmarkContent> contents = new ArrayList<TextBookmarkContent>();
        
        String testingItemCode = result.getAlgorithmConfig().getTestingItem().getCode();
        
        Double relativeValue = null;
        Double referenceValue = null;
        Double evaluateValue = result.getTestingItemEvaluateValue();
        
        if (null == evaluateValue)
        {
            generateUndefinedEvaluateValue(testingItemCode, contents);
        }
        else
        {
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_EV", testingItemCode), this.riskNumberFormat(evaluateValue, 4)));
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_EV_P", testingItemCode), this.riskPercentFormat(evaluateValue)));
        }
        
        TestingItemEvaluateReferenceValue evaluateReference = result.getAlgorithmConfig().getReferenceValue();
        
        if (null == evaluateReference)
        {
            generateUndefinedReferenceValue(testingItemCode, contents);
        }
        else
        {
            referenceValue = evaluateReference.getValue(sex);
            
            if (null == referenceValue)
            {
                generateUndefinedReferenceValue(testingItemCode, contents);
            }
            
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RV", testingItemCode), this.riskNumberFormat(referenceValue, 4)));
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RV_P", testingItemCode), this.riskPercentFormat(referenceValue)));
        }
        
        if (null != evaluateValue && null != referenceValue && referenceValue != 0)
        {
            relativeValue = getRelativeValue(evaluateValue, referenceValue);
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RELATIVE_VALUE", testingItemCode), this.riskNumberFormat(relativeValue, 2)));
        }
        else
        {
            generateUndefinedRelativeValue(testingItemCode, contents);
        }
        TestingItem testingItem = result.getAlgorithmConfig().getTestingItem();
        
        int level;
        
        if (TestingItem.ALGORITHM_MC.equals(testingItem.getEvalAlgorithm()))
        {
            if (TestingItem.CATEGORY_DISEASE_RISK.equals(testingItem.getCategory()))
            {
                level = ItemLevelEvaluator.getLevelInterval(testingItem, relativeValue, LevelForFive);
            }
            else if (TestingItem.CATEGORY_YJ_RISK.equals(testingItem.getCategory()))
            {
                int YJResult;
                String resultNote;
                String resultAnalysis;
                level = ItemLevelEvaluator.getLevelInterval(testingItem, relativeValue, LevelForFive);
                if (TestingItem.JJDX.equals(testingItem.getCode()))
                {
                    YJResult = ItemLevelEvaluator.getLevelInterval(testingItem, relativeValue, LevelForNine);
                    resultNote = YJResultData.getJJDXMap().get(YJResult);
                    resultAnalysis = YJResultData.getJJDResultXMap().get(YJResult);
                    contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RESULT_ANALYSIS", testingItemCode), resultAnalysis));
                }
                else
                {
                    YJResult = ItemLevelEvaluator.getLevelInterval(testingItem, relativeValue, LevelForThree);
                    resultNote = testingItem.getName() + YJResultData.getYJSuffix(YJResult);
                }
                
                contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RESULT", testingItemCode), resultNote));
                
            }
            else if (TestingItem.CATEGORY_CHILD_RISK.equals(testingItem.getCategory()))
            {
                level = ItemLevelEvaluator.getLevelInterval(testingItem, relativeValue, LevelForFive);
            }
            else
            {
                level = ItemLevelEvaluator.getLevelInterval(testingItem, relativeValue, LevelForThree);
            }
        }
        else
        {
            level = result.getTestingItemEvaluateValue().intValue();
        }
        
        List<String> symbols = new ArrayList<String>();
        
        for (int i = 0; i < level; i++)
        {
            symbols.add("★");
        }
        
        contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_EVALUATE_SYMBOL", testingItemCode), StringUtils.join(symbols, "")));
        
        Set<String> genes = getTestingGenes(result.getAlgorithmConfig());
        
        if (CollectionUtils.isEmpty(genes))
        {
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RELATED_GENES", testingItemCode), ""));
        }
        else
        {
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RELATED_GENES", testingItemCode), StringUtils.join(genes, "、")));
        }
        
        List<TestingItemLocusEvaluateResult> locusEvaluateResults = result.getlocusEvaluateResults();
        
        if (!CollectionUtils.isEmpty(locusEvaluateResults))
        {
            List<TextBookmarkContent> locusContents;
            
            for (TestingItemLocusEvaluateResult locusEvaluateResult : locusEvaluateResults)
            {
                locusContents = generateTestingItemLocusTextBookmarkContents(testingItemCode, locusEvaluateResult);
                contents.addAll(locusContents);
            }
        }
        
        return contents;
    }
    
    private List<TextBookmarkContent> generateTestingItemLocusTextBookmarkContents(String testingItemCode, TestingItemLocusEvaluateResult locusResult)
    {
        List<TextBookmarkContent> contents = new ArrayList<TextBookmarkContent>();
        
        String locusName = locusResult.getLocus().getName();
        Double evaluateValue = locusResult.getFactor();
        
        if (null != evaluateValue)
        {
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_L_{1}_EV", testingItemCode, locusName), this.riskNumberFormat(evaluateValue, 4)));
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_L_{1}_EV_P", testingItemCode, locusName), this.riskPercentFormat(evaluateValue)));
        }
        else
        {
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_L_{1}_EV", testingItemCode, locusName), "-"));
            contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_L_{1}_EV_P", testingItemCode, locusName), "-"));
        }
        
        return contents;
    }
    
    private Set<String> getTestingGenes(TestingItemAlgorithmConfig config)
    {
        List<TestingItemLocusEvaluateConfig> locusConfigs = config.getLocusEvaluateConfigs();
        
        if (CollectionUtils.isEmpty(locusConfigs))
        {
            return Collections.emptySet();
        }
        
        Set<String> genes = new HashSet<String>();
        locusConfigs.forEach(locusConfig -> genes.add(locusConfig.getLocus().getGeneName()));
        return genes;
    }
    
    private void generateUndefinedEvaluateValue(String testingItemCode, List<TextBookmarkContent> contents)
    {
        contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_EV", testingItemCode), "-"));
        contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_EV_P", testingItemCode), "-"));
    }
    
    private void generateUndefinedReferenceValue(String testingItemCode, List<TextBookmarkContent> contents)
    {
        contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RV", testingItemCode), "-"));
        contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RV_P", testingItemCode), "-"));
    }
    
    private void generateUndefinedRelativeValue(String testingItemCode, List<TextBookmarkContent> contents)
    {
        contents.add(new TextBookmarkContent(MessageFormat.format("I_{0}_RELATIVE_VALUE", testingItemCode), "-"));
    }
    
    private String riskNumberFormat(Double risk, int length)
    {
        String pattern = StringUtils.rightPad("0.", length + 2, "0");
        DecimalFormat format = new DecimalFormat(pattern);
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(risk);
    }
    
    private String riskPercentFormat(Double risk)
    {
        DecimalFormat format = new DecimalFormat("0.00%");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(risk);
    }
    
    private double getRelativeValue(double evaluateValue, double referenceValue)
    {
        String ev = riskPercentFormat(evaluateValue);
        String rv = riskPercentFormat(referenceValue);
        double evd = Double.parseDouble(ev.replaceAll("%", ""));
        double rvd = Double.parseDouble(rv.replaceAll("%", ""));
        String value = riskNumberFormat(evd / rvd, 2);
        return Double.parseDouble(value);
    }
}
