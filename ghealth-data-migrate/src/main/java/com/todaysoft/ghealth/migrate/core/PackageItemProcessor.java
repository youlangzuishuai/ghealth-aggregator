package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;

import com.todaysoft.ghealth.migrate.model.DecetionPackageItem;
import com.todaysoft.ghealth.migrate.model.DetectionPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

@Service
public class PackageItemProcessor implements Processor
{
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Override
    public void process()
    {
        Map<String, List<String>> map = getFormateData();
        List<DecetionPackageItem> list = getRealData(map);
        migrate(list);
    }
    
    private Map<String, String> getItemMap()
    {
        Map<String, String> map = new HashMap<>();
        String findItemIds = "SELECT ID FROM cdc_detection_item";
        List<String> itemIds = cdc.queryForList(findItemIds, String.class);
        for (String itemId : itemIds)
        {
            String findItemIdBySyncKey = "select Id FROM ghealth_testing_item where SYNC_KEY = ?";
            List<String> strs = ghealth.queryForList(findItemIdBySyncKey, String.class, new Object[] {itemId});
            if (CollectionUtils.isEmpty(strs))
            {
                continue;
            }
            map.put(itemId, strs.get(0));
        }
        return map;
    }
    
    private Map<String, String> getProductMap(Set<String> set)
    {
        Map<String, String> map = new HashMap<>();
        String findProducts = "SELECT DISTINCT(PACKAGES_ID) FROM cdc_detection_packages_item ";
        List<String> productIds = cdc.queryForList(findProducts, String.class);
        for (String productId : productIds)
        {
            
            String findProductIdBySyncKey = "select Id FROM ghealth_testing_product  where SYNC_KEY = ? ";
            List<String> strs = ghealth.queryForList(findProductIdBySyncKey, String.class, new Object[] {productId});
            if (CollectionUtils.isEmpty(strs))
            {
                continue;
            }
            if (strs.size() == 2)
            {
                set.add(productId);
                continue;
            }
            map.put(productId, strs.get(0));
        }
        return map;
    }
    
    private List<String> getCombineItems(String itemId)
    {
        String findByItemId = "select ITEM_ID from cdc_detection_combine_item where COMBINE_ITEM_ID = ?";
        List<String> itemIds = cdc.queryForList(findByItemId, String.class, new Object[] {itemId});
        return itemIds;
    }
    
    private Map<String, List<String>> getFormateData()
    {
        Map<String, List<String>> map = new HashMap<>();
        String findPackaheIds = "SELECT DISTINCT(PACKAGES_ID) FROM cdc_detection_packages_item ";
        List<String> packageIds = cdc.queryForList(findPackaheIds, String.class);
        for (String productId : packageIds)
        {
            
            String findItemIds = "SELECT ITEM_ID FROM cdc_detection_packages_item where PACKAGES_ID = ? AND ITEM_ID !='502'";
            List<String> itemIds = cdc.queryForList(findItemIds, String.class, new Object[] {productId});
            List<String> list = new ArrayList<>();
            for (String itemId : itemIds)
            {
                List<String> combineItems = getCombineItems(itemId);
                if (CollectionUtils.isEmpty(combineItems))
                {
                    list.add(itemId);
                }
                else
                {
                    list.addAll(combineItems);
                }
            }
            map.put(productId, list);
        }
        return map;
    }
    
    private List<DecetionPackageItem> getRealData(Map<String, List<String>> map)
    {
        Set<String> set = new HashSet<>();
        Map<String, String> productMap = getProductMap(set);
        Map<String, String> itemMap = getItemMap();
        List<DecetionPackageItem> list = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet())
        {
            String productId = entry.getKey();
            
            if (set.contains(productId))
            {
                List<DetectionPackage> products = ghealth.query("select * from ghealth_testing_product WHERE SYNC_KEY = ?", new RowMapper<DetectionPackage>()
                {
                    @Override
                    public DetectionPackage mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        DetectionPackage data = new DetectionPackage();
                        data.setSexRestraint(rs.getString("SEX_RESTRAINT"));
                        data.setId(rs.getString("ID"));
                        return data;
                    }
                }, new Object[] {productId});
                for (DetectionPackage product : products)
                {
                    String sql = "select ITEM_ID from cdc_detection_packages_item WHERE PACKAGES_ID = ? and GENDER = ?";
                    List<String> strings = cdc.queryForList(sql, String.class, new Object[] {productId, product.getSexRestraint()});
                    list.addAll(wrapItem(itemMap, strings, product.getId()));
                }
            }
            else
            {
                list.addAll(wrapItem(itemMap, entry.getValue(), productMap.get(productId)));
            }
        }
        return list;
    }
    
    private List<DecetionPackageItem> wrapItem(Map<String, String> itemMap, List<String> itemIds, String productId)
    {
        List<DecetionPackageItem> list = new ArrayList<>();
        
        for (String itemId : itemIds)
        {
            DecetionPackageItem data = new DecetionPackageItem();
            data.setPackageId(productId);
            data.setItemId(itemMap.get(itemId));
            list.add(data);
        }
        return list;
    }
    
    private void migrate(List<DecetionPackageItem> list)
    {
        String sql = "INSERT INTO ghealth_product_item(PRODUCT_ID,TESTING_ITEM_ID) VALUES (?, ?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (DecetionPackageItem data : list)
                {
                    if (data.getItemId() == null)
                    {
                        continue;
                    }
                    addBatch(data, ps);
                }
                
                return ps.executeBatch();
            }
        });
    }
    
    private void addBatch(DecetionPackageItem data, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, data.getPackageId());
        ps.setString(index++, data.getItemId());
        ps.addBatch();
    }
    
}
