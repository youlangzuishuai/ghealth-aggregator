package com.todaysoft.ghealth.migrate.core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.utils.JsonUtils;

@Service
public class FixProcessor implements Processor
{
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        ghealth.query("select * from ghealth_object_storage", new RowCallbackHandler()
        {
            
            @Override
            public void processRow(ResultSet rs)
                throws SQLException
            {
                String id = rs.getString("ID");
                String details = rs.getString("STORAGE_DETAILS");
                Map<String, String> map = JsonUtils.fromJson(details, new TypeReference<Map<String, String>>()
                {
                });
                
                String objectKey = map.get("objectKey");
                
                String path = objectKey.substring(0, objectKey.lastIndexOf("/"));
                String filename = objectKey.substring(objectKey.lastIndexOf("/"));
                String suffix = filename.substring(filename.lastIndexOf("."));
                String name = filename.replaceAll("/", "").replaceAll(suffix, "");
                String code = name.substring(0, name.length() / 2);
                
                String newKey = path + "/" + code + "/" + code + suffix;
                System.out.println("id " + id + ",key " + objectKey + " -> new key" + newKey);
                
                map.put("objectKey", newKey);
                
                String newDetails = JsonUtils.toJson(map);
                ghealth.execute("update ghealth_object_storage set STORAGE_DETAILS = ? where id = ?", new PreparedStatementCallback<Object>()
                {
                    
                    @Override
                    public Object doInPreparedStatement(PreparedStatement ps)
                        throws SQLException, DataAccessException
                    {
                        int index = 1;
                        ps.setString(index++, newDetails);
                        ps.setString(index++, id);
                        return ps.execute();
                    }
                });
                
            }
        });
    }
}
