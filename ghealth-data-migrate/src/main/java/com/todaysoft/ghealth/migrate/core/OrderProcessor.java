package com.todaysoft.ghealth.migrate.core;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.CsmsJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.Client;
import com.todaysoft.ghealth.migrate.model.Order;
import com.todaysoft.ghealth.migrate.model.OrderTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class OrderProcessor implements Processor
{
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Autowired
    private CsmsJdbcTemplate csms;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    @Override
    public void process()
    {
        List<Order> orders = wrapOrder(getHistoryData());
        migrate(orders);
    }
    
    private List<Order> getHistoryData()
    {
        String sql = "select * from csms_workorder";
        return csms.query(sql, new RowMapper<Order>()
        {
            
            @Override
            public Order mapRow(ResultSet rs, int i) throws SQLException
            {
                Order data = new Order();
                data.setId(rs.getString("ID"));
                data.setCustomerName(rs.getString("CUSTOMER_NAME"));
                data.setAgencyId(rs.getString("AGENT_ID"));
                data.setCreateName(rs.getString("CREATOR_NAME"));
                data.setCreateId(rs.getString("CREATOR_ID"));
                data.setCreateTime(rs.getTimestamp("CREATE_TIME"));
                data.setCustomerId(rs.getString("CUSTOMER_ID"));
                String paperRequired = rs.getString("IS_PAPER_REPORT");
                data.setIsPaperReport(StringUtils.isEmpty(paperRequired) ? "1" : paperRequired);
                data.setOrderCode(rs.getString("SPECIMEN_CODE"));
                data.setProductId(rs.getString("PACKAGES_ID"));
                String sampleType = rs.getString("SPECIMEN_TYPE");
                sampleType = StringUtils.isEmpty(sampleType) ? "0" : "0".equals(sampleType) ? "0" : "1";
                data.setSampleType(sampleType);
                data.setStatus(getStauts(rs.getInt("STATE")));
                data.setSubmitTime(rs.getTimestamp("SUBMIT_TIME"));
                return data;
            }
        });
    }
    
    private void migrate(List<Order> orders)
    {
        String createOrderSql =
            "insert into ghealth_order(ID,PRODUCT_ID,CUSTOMER_ID,AGENCY_ID,CODE,ACTUAL_PRICE,SAMPLE_TYPE,STATUS,SUBMIT_TIME,SUBMITOR_NAME,REPORT_PRINT_REQUIRED,REPORT_GENERATE_TIME,REPORT_GENERATE_TASK,CREATE_TIME,CREATOR_NAME,DELETED,SYNC_KEY) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        ghealth.execute(createOrderSql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException
            {
                for (Order data : orders)
                {
                    OrderTask orderTask = data.getOrderTask();
                    String taskId = null;
                    if (null != orderTask)
                    {
                        taskId = KeyGenerator.uuid();
                        String doc = orderTask.getWordFile();
                        String pdf = orderTask.getPdfFile();
                        Timestamp date = null == data.getSubmitTime() ? data.getCreateTime() : data.getSubmitTime();
                        ghealth.update("INSERT INTO ghealth_order_report_generate_task (ID, STATUS,CREATE_TIME,CREATOR_NAME, WORD_FILE_URL ,PDF_FILE_URL) "
                            + "values ('" + taskId + "', '" + "1" + "', '" + date + "', '" + "管理员" + "', '" + doc + "', '" + pdf + "')");
                    }
                    if (null == data.getAgencyId())
                    {
                        data.setAgencyId("1");
                    }
                    addBatch(data, taskId, ps);
                }
                return ps.executeBatch();
            }
        });
    }
    
    private void addBatch(Order data, String taskId, PreparedStatement ps) throws SQLException
    {
        int index = 1;
        ps.setString(index++, KeyGenerator.uuid());
        ps.setString(index++, data.getProductId());
        ps.setString(index++, data.getCustomerId());
        ps.setString(index++, data.getAgencyId());
        ps.setString(index++, data.getOrderCode());
        ps.setBigDecimal(index++, new BigDecimal(0));
        ps.setString(index++, data.getSampleType());
        ps.setString(index++, data.getStatus());
        ps.setTimestamp(index++, data.getSubmitTime());
        ps.setString(index++, data.getCreateName());
        ps.setString(index++, data.getIsPaperReport());
        ps.setTimestamp(index++, new Timestamp(new Date().getTime()));
        ps.setString(index++, taskId);
        ps.setTimestamp(index++, data.getCreateTime());
        ps.setString(index++, data.getCreateName());
        ps.setInt(index++, 0);
        ps.setString(index++, data.getId());
        ps.addBatch();
    }
    
    private List<Order> wrapOrder(List<Order> orders)
    {
        for (Order order : orders)
        {
            String clientId = null;
            String productId = null;
            String sourceAgentId = null;
            if (StringUtils.isEmpty(order.getAgencyId()))
            {
                if ("1".equals(order.getCreateId()))
                {
                    continue;
                }
                String sql = "select AGENT_ID from csms_agent_account where USER_ID = ?";
                sourceAgentId = csms.queryForObject(sql, String.class, new Object[] {order.getCreateId()});
            }
            else
            {
                sourceAgentId = order.getAgencyId();
            }
            
            String getAgencyIdSql = "select ID from ghealth_agency where SYNC_KEY = ?";
            String targetAgentId = ghealth.queryForObject(getAgencyIdSql, String.class, new Object[] {sourceAgentId});
            
            order.setAgencyId(targetAgentId);
            
            String getProductId = "select ID from ghealth_testing_product where SYNC_KEY = ?";
            List<String> productIds = ghealth.queryForList(getProductId, String.class, new Object[] {order.getProductId()});
            if (productIds.size() > 1)
            {
                String getCustomerId = "select * from ghealth_customer where SYNC_KEY = ?";
                Client client = ghealth.queryForObject(getCustomerId, new RowMapper<Client>()
                {
                    @Override
                    public Client mapRow(ResultSet rs, int i) throws SQLException
                    {
                        Client c = new Client();
                        c.setId(rs.getString("ID"));
                        c.setSex(rs.getString("SEX"));
                        return c;
                    }
                }, new Object[] {order.getCustomerId()});
                String getProductIdsql = "select ID from ghealth_testing_product where SYNC_KEY = ? AND SEX_RESTRAINT = ?";
                productId = ghealth.queryForObject(getProductIdsql, String.class, new Object[] {order.getProductId(), client.getSex()});
                clientId = client.getId();
            }
            else
            {
                productId = productIds.get(0);
                String getClientIdsql = "select ID from ghealth_customer where SYNC_KEY = ?";
                
                List<String> clients = ghealth.queryForList(getClientIdsql, String.class, new Object[] {order.getCustomerId()});
                if (CollectionUtils.isEmpty(clients))
                {
                    String getClientNameSql = "select ID from csms_client where NAME = ?";
                    String s = csms.queryForObject(getClientNameSql, String.class, new Object[] {order.getCustomerName()});
                    clientId = ghealth.queryForObject(getClientIdsql, String.class, new Object[] {s});
                }
                else
                {
                    clientId = clients.get(0);
                }
            }
            
            order.setCustomerId(clientId);
            order.setProductId(productId);
            
            if ("5".equals(order.getStatus()))
            {
                OrderTask orderTask = new OrderTask();
                String sql = "select PATH from cdc_detection_report where DETECTION_CODE = ?";
                List<String> paths = cdc.queryForList(sql, String.class, new Object[] {order.getOrderCode()});
                if (!CollectionUtils.isEmpty(paths))
                {
                    String path = paths.get(0);
                    if (path.indexOf(".") != -1)
                    {
                        String suffix = path.substring(path.indexOf("."), path.length());
                        if (".pdf".equals(suffix))
                        {
                            orderTask.setPdfFile(path);
                        }
                        else if (".doc".equals(suffix) || ".docx".equals(suffix))
                        {
                            orderTask.setWordFile(path);
                        }
                    }
                    order.setOrderTask(orderTask);
                }
            }
        }
        return orders;
    }
    
    private String getStauts(int status)
    {
        String state = null;
        if (status > 5)
        {
            state = "5";
        }
        else if (status == 5)
        {
            state = "4";
        }
        else if (status == 0)
        {
            state = "0";
        }
        else
        {
            state = "3";
        }
        return state;
    }
}
