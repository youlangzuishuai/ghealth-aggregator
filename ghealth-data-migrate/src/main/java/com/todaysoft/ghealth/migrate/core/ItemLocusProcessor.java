package com.todaysoft.ghealth.migrate.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.ItemLocus;
import com.todaysoft.ghealth.migrate.model.TemplateModel;
import com.todaysoft.ghealth.migrate.model.TestingItemLocus;
import com.todaysoft.ghealth.migrate.utils.JsonUtils;

@Service
public class ItemLocusProcessor implements Processor
{
    
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        List<ItemLocus> list = getHsitorData();
        List<TestingItemLocus> datas = getFromateData(list);
        migrate(datas);
    }
    
    private List<ItemLocus> getHsitorData()
    {
        return cdc.query("select * from cdc_locus_genetype", new RowMapper<ItemLocus>()
        {
            @Override
            public ItemLocus mapRow(ResultSet rs, int rowNum)
                throws SQLException
            {
                ItemLocus data = new ItemLocus();
                data.setGender(rs.getString("GENDER"));
                data.setItemId(rs.getString("ITEM_ID"));
                data.setLocusCode(rs.getString("LOCUS_CODE"));
                data.setGeneType(rs.getString("GENETYPE"));
                data.setRisk(rs.getBigDecimal("RISK"));
                return data;
            }
        });
    }
    
    private void migrate(List<TestingItemLocus> list)
    {
        String sql = "INSERT INTO ghealth_testing_item_locus(ID, TESTING_ITEM_ID, LOCUS_ID,INFLUENCE_FACTORS) VALUES (?, ?, ?, ?)";
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (TestingItemLocus data : list)
                {
                    String findLocusesIdByItemId = "select LOCUS_ID FROM cdc_item_locus  where ITEM_ID = ?";
                    List<String> locusIds = cdc.queryForList(findLocusesIdByItemId, new Object[] {data.getTestingItemId()}, String.class);
                    //安全用药的数据数据库没有记录
                    if (locusIds.size() == 0)
                    {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>();
                    NamedParameterJdbcTemplate namedParameter = new NamedParameterJdbcTemplate(ghealth);
                    String findLocusByLocusCode = "select ID from ghealth_locus WHERE NAME = :name AND SYNC_KEY in (:ids)";
                    map.put("name", data.getLocusName());
                    map.put("ids", locusIds);
                    String locusId = null;
                    List<String> locusIdList = namedParameter.queryForList(findLocusByLocusCode, map, String.class);
                    if (!CollectionUtils.isEmpty(locusIdList))
                    {
                        locusId = locusIdList.get(0);
                    }
                    
                    if (locusId == null)
                    {
                        continue;
                    }
                    
                    String findTestingItemBySyncKey = "select Id FROM ghealth_testing_item  where SYNC_KEY = ?";
                    String itemId = ghealth.queryForObject(findTestingItemBySyncKey, String.class, new Object[] {data.getTestingItemId()});
                    data.setTestingItemId(itemId);
                    data.setLocusId(locusId);
                    addBatch(data, ps);
                }
                return ps.executeBatch();
            }
        });
    }
    
    private void addBatch(TestingItemLocus data, PreparedStatement ps)
        throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, data.getTestingItemId());
        ps.setString(index++, data.getLocusId());
        ps.setString(index++, data.getInfluenceFactors());
        ps.addBatch();
    }
    
    private List<List<List<ItemLocus>>> limitList(List<ItemLocus> list)
    {
        Set<String> limtByLocusCodes = new HashSet<>();
        Set<String> limtByitemIds = new HashSet<>();
        for (ItemLocus data : list)
        {
            limtByLocusCodes.add(data.getLocusCode());
            limtByitemIds.add(data.getItemId());
        }
        List<List<ItemLocus>> locusList = new ArrayList<>();
        for (String locusCode : limtByLocusCodes)
        {
            List<ItemLocus> ls = new ArrayList<>();
            for (ItemLocus data : list)
            {
                if (locusCode.equals(data.getLocusCode()))
                {
                    ls.add(data);
                }
            }
            locusList.add(ls);
        }
        
        List<List<List<ItemLocus>>> itemList = new ArrayList<>();
        for (String itemId : limtByitemIds)
        {
            List<List<ItemLocus>> locusList1 = new ArrayList<>();
            for (List<ItemLocus> lst : locusList)
            {
                List<ItemLocus> lllls = new ArrayList<>();
                for (ItemLocus data : lst)
                {
                    String cdcItemId = data.getItemId();
                    if (itemId.equals(cdcItemId))
                    {
                        lllls.add(data);
                    }
                }
                if (!CollectionUtils.isEmpty(lllls))
                {
                    locusList1.add(lllls);
                }
            }
            if (!CollectionUtils.isEmpty(locusList1))
            {
                itemList.add(locusList1);
            }
        }
        return itemList;
    }
    
    private List<TestingItemLocus> getFromateData(List<ItemLocus> list)
    {
        List<List<List<ItemLocus>>> itemList = limitList(list);
        List<TestingItemLocus> datas = new ArrayList<>();
        for (List<List<ItemLocus>> list1 : itemList)
        {
            for (List<ItemLocus> list2 : list1)
            {
                Map<String, ItemLocus> map1 = new HashMap<>();
                Map<String, ItemLocus> map2 = new HashMap<>();
                for (ItemLocus data : list2)
                {
                    String geneType = data.getGeneType();
                    if (!map1.containsKey(geneType))
                    {
                        map2.put(geneType, data);
                    }
                    map1.put(geneType, data);
                }
                List<TemplateModel> models = new ArrayList<>();
                String itemId = null;
                String locusName = null;
                for (Map.Entry<String, ItemLocus> entry : map1.entrySet())
                {
                    String geneType = entry.getKey();
                    ItemLocus data = entry.getValue();
                    itemId = data.getItemId();
                    locusName = data.getLocusCode();
                    TemplateModel model = getFactor(data, new TemplateModel());
                    
                    if (null != map2.get(geneType))
                    {
                        model = getFactor(map2.get(geneType), model);
                    }
                    models.add(model);
                }
                
                String jsonString = JsonUtils.toJson(models);
                TestingItemLocus data = new TestingItemLocus();
                data.setLocusName(locusName);
                data.setTestingItemId(itemId);
                data.setInfluenceFactors(jsonString);
                datas.add(data);
            }
        }
        return datas;
    }
    
    private TemplateModel getFactor(ItemLocus data, TemplateModel model)
    {
        String male_factor = null;
        String female_factor = null;
        //男
        if ("1".equals(data.getGender()))
        {
            male_factor = String.valueOf(data.getRisk());
        }
        else if ("2".equals(data.getGender()))
        {
            female_factor = String.valueOf(data.getRisk());
        }
        else
        {
            male_factor = String.valueOf(data.getRisk());
            female_factor = String.valueOf(data.getRisk());
        }
        if (!StringUtils.isEmpty(male_factor))
        {
            model.setMale_factor(male_factor);
        }
        if (!StringUtils.isEmpty(female_factor))
        {
            model.setFemale_factor(female_factor);
        }
        model.setGenetype(data.getGeneType());
        return model;
    }
}
