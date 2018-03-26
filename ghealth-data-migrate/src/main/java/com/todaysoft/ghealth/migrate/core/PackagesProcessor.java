package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.DecetionPackageItem;
import com.todaysoft.ghealth.migrate.model.DetectionItem;
import com.todaysoft.ghealth.migrate.model.DetectionPackage;

import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PackagesProcessor implements Processor
{
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Override
    public void process()
    {
        List<DetectionPackage> list = getHsitorData();
        migrate(getAllDetectionPackages(list));
    }
    
    private List<DetectionPackage> getHsitorData()
    {
        return cdc.query("select * from cdc_detection_packages", new RowMapper<DetectionPackage>()
        {
            @Override
            public DetectionPackage mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                DetectionPackage data = new DetectionPackage();
                data.setId(rs.getString("ID"));
                data.setCode(rs.getString("CODE"));
                data.setName(rs.getString("NAME"));
                return data;
            }
        });
    }
    
    private List<DetectionPackage> getAllDetectionPackages(List<DetectionPackage> list)
    {
        List<DetectionPackage> realList = new ArrayList<>();
        for (DetectionPackage data : list)
        {
            String findPackageId = "select DISTINCT(gender) from cdc_detection_packages_item where PACKAGES_ID = ?";
            List<String> genders = cdc.queryForList(findPackageId, String.class, new Object[] {data.getId()});
            if (CollectionUtils.isEmpty(genders))
            {
                data.setSexRestraint(DecetionPackageItem.GLOBAL);
                realList.add(data);
                continue;
            }
            if (genders.size() > 1)
            {
                if (genders.contains(DecetionPackageItem.FEMALE) && genders.contains(DecetionPackageItem.MALE))
                {
                    String sourceName = data.getName();
                    String sourceCode = data.getCode();
                    for (String gender : genders)
                    {
                        DetectionPackage dp = new DetectionPackage();
                        BeanUtils.copyProperties(data, dp);
                        dp.setName(getRealName(gender, sourceName));
                        dp.setCode(getRealCode(gender, sourceCode));
                        dp.setSexRestraint(gender);
                        realList.add(dp);
                    }
                }
                else if (genders.contains(DecetionPackageItem.FEMALE))
                {
                    data.setSexRestraint(DecetionPackageItem.FEMALE);
                    realList.add(data);
                }
                else if (genders.contains(DecetionPackageItem.MALE))
                {
                    data.setSexRestraint(DecetionPackageItem.MALE);
                    realList.add(data);
                }
            }
            else
            {
                data.setSexRestraint(genders.get(0));
                realList.add(data);
            }
        }
        return realList;
    }
    
    private String getRealName(String gender, String sourceName)
    {
        String name = null;
        if (DecetionPackageItem.MALE.equals(gender))
        {
            name = sourceName + "-男";
        }
        else if (DecetionPackageItem.FEMALE.equals(gender))
        {
            name = sourceName + "-女";
        }
        else
        {
            name = sourceName;
        }
        return name;
    }
    
    private String getRealCode(String gender, String sourceCode)
    {
        String code = null;
        if (DecetionPackageItem.MALE.equals(gender))
        {
            code = sourceCode + "-m";
        }
        else if (DecetionPackageItem.FEMALE.equals(gender))
        {
            code = sourceCode + "-w";
        }
        else
        {
            code = sourceCode;
        }
        return code;
    }
    
    private void migrate(List<DetectionPackage> list)
    {
        String sql =
            "INSERT INTO ghealth_testing_product(ID, NAME, CODE,SEX_RESTRAINT,ENABLED, CREATE_TIME ,CREATOR_NAME, DELETED, SYNC_KEY) VALUES (?, ?, ?,?, ?, ?, ?, ?, ?)";
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (DetectionPackage data : list)
                {
                    addBatch(data, ps);
                }
                return ps.executeBatch();
            }
            
        });
    }
    
    private void addBatch(DetectionPackage data, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, data.getName());
        ps.setString(index++, data.getCode());
        ps.setString(index++, data.getSexRestraint());
        ps.setInt(index++, 1);
        ps.setTimestamp(index++, new Timestamp(new Date().getTime()));
        ps.setString(index++, "管理员");
        ps.setInt(index++, 0);
        ps.setString(index++, data.getId());
        ps.addBatch();
    }
}
