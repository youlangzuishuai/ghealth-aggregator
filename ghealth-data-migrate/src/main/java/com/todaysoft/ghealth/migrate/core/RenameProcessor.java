package com.todaysoft.ghealth.migrate.core;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;

@Component
public class RenameProcessor implements Processor
{
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Override
    public void process()
    {
        File directory = new File("E:\\Merge\\2015");
        String[] dates = directory.list();
        
        for (String date : dates)
        {
            process(date);
            System.out.println(date + " processed.");
        }
    }
    
    private void process(String date)
    {
        String prefix = "/report/doc/" + date + "/";
        File directory = new File("E:\\Merge\\2015", date);
        
        cdc.query("select * from cdc_detection_report where path like '%" + prefix + "%'", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs)
                throws SQLException
            {
                String path = rs.getString("PATH");
                String code = rs.getString("DETECTION_CODE");
                String filename = path.replaceAll(prefix, "");
                String suffix = filename.substring(filename.lastIndexOf("."));
                
                File file = new File(directory, filename);
                
                if (!file.exists())
                {
                    throw new IllegalStateException();
                }
                
                file.renameTo(new File(directory, code + suffix));
                cdc.update("update cdc_detection_report set path = ? where id = ?", prefix + code + suffix, rs.getString("ID"));
            }
        });
    }
    
}
