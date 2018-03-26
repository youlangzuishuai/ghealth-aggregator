package com.todaysoft.ghealth.migrate.core;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.todaysoft.ghealth.migrate.config.CdcJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.CsmsJdbcTemplate;
import com.todaysoft.ghealth.migrate.config.GhealthJdbcTemplate;
import com.todaysoft.ghealth.migrate.model.target.LocusGenetype;
import com.todaysoft.ghealth.migrate.model.target.ObjectStorage;
import com.todaysoft.ghealth.migrate.model.target.OrderDetails;
import com.todaysoft.ghealth.migrate.model.target.OrderHistory;
import com.todaysoft.ghealth.migrate.model.target.OrderReportGenerateTask;
import com.todaysoft.ghealth.migrate.model.target.OrderTestingData;
import com.todaysoft.ghealth.migrate.utils.JsonUtils;

@Service
public class OrderReportProcessor implements Processor
{
    @Autowired
    private CdcJdbcTemplate cdc;
    
    @Autowired
    private CsmsJdbcTemplate csms;
    
    @Autowired
    private GhealthJdbcTemplate ghealth;
    
    private Map<String, String> productMappings = new HashMap<String, String>();
    
    private Map<String, String> customerMappings = new HashMap<String, String>();
    
    private Map<String, String> agencyMappings = new HashMap<String, String>();
    
    @Override
    public void process()
    {
        int limit = 20;
        int offset = 0;
        List<OrderDetails> orders;
        
        while (true)
        {
            String signKey = KeyGenerator.uuid();
            String deliveryKey = KeyGenerator.uuid();
            
            orders = fromHistory(offset, limit, signKey, deliveryKey);
            
            if (orders.isEmpty())
            {
                System.out.println(MessageFormat.format("Sync orders finished.", offset));
                break;
            }
            
            migrate(orders, signKey, deliveryKey);
            offset += limit;
            System.out.println(MessageFormat.format("Processed {0} records for sync orders.", offset));
        }
        
        //insertDefaultUploadRecord();
    }
    
    private List<OrderDetails> fromHistory(int offset, int limit, String signKey, String deliveryKey)
    {
        String sql = String.format("select * from csms_workorder where create_time >= '2018-01-01 00:00:00' order by create_time limit %1$d offset %2$d", limit, offset);
        return csms.query(sql, new RowMapper<OrderDetails>()
        {
            @Override
            public OrderDetails mapRow(ResultSet rs, int rowNum)
                throws SQLException
            {
                OrderDetails order = new OrderDetails();
                order.setId(KeyGenerator.uuid());
                order.setSyncKey(rs.getString("ID"));
                order.setProductId(asTargetProductId(rs.getString("PACKAGES_ID")));
                order.setCustomerId(asTargetCustomerId(rs.getString("CUSTOMER_ID")));
                order.setAgencyId(asTargetAgencyId(rs.getString("AGENT_ID")));
                order.setCode(rs.getString("SPECIMEN_CODE"));
                order.setActualPrice(rs.getBigDecimal("ADVANCE_PAYMENT"));
                order.setSampleType(asTargetSampleType(rs.getString("SPECIMEN_TYPE")));
                order.setStatus(asTargetStatus(rs.getString("STATE")));
                order.setSubmitorName(rs.getString("CREATOR_NAME"));
                order.setSubmitTime(rs.getTimestamp("SUBMIT_TIME"));
                order.setReportPrintRequired(asTargetReportPrintRequired(rs.getString("IS_PAPER_REPORT")));
                order.setCreatorName(rs.getString("CREATOR_NAME"));
                order.setCreateTime(rs.getTimestamp("CREATE_TIME"));
                order.setDeleted(false);
                order.setReportGenerateTask(asReportGenerateTask(order, rs.getTimestamp("REPORT_BUILD_DATE")));
                order.setOrderTestingData(asTargetTestingData(order));
                order.setHistories(asTargetHistory(order, signKey, deliveryKey));
                return order;
            }
        });
    }
    
    private List<OrderHistory> asTargetHistory(OrderDetails order, String signKey, String deliveryKey)
    {
        List<OrderHistory> histories = new ArrayList<OrderHistory>();
        
        if ("0".equals(order.getStatus()))
        {
            OrderHistory history = new OrderHistory();
            history.setId(KeyGenerator.uuid());
            history.setOrderId(order.getId());
            history.setTitle("订单暂存");
            history.setOperatorName(order.getCreatorName());
            history.setEventType("1");
            history.setEventTime(order.getCreateTime());
            histories.add(history);
        }
        else
        {
            OrderHistory history = new OrderHistory();
            history.setId(KeyGenerator.uuid());
            history.setOrderId(order.getId());
            history.setTitle("订单提交");
            history.setOperatorName(order.getCreatorName());
            history.setEventType("2");
            history.setEventTime(order.getSubmitTime());
            histories.add(history);
            
            if (null != order.getOrderTestingData())
            {
                history = new OrderHistory();
                history.setId(KeyGenerator.uuid());
                history.setOrderId(order.getId());
                history.setTitle("样本签收");
                history.setOperatorName("管理员");
                history.setEventType("3");
                history.setEventTime(DateUtils.addDays(order.getSubmitTime(), 1));
                history.setEventDetails(signKey);
                histories.add(history);
                
                history = new OrderHistory();
                history.setId(KeyGenerator.uuid());
                history.setOrderId(order.getId());
                history.setTitle("样本寄送");
                history.setOperatorName("管理员");
                history.setEventType("4");
                history.setEventTime(DateUtils.addDays(order.getSubmitTime(), 2));
                history.setEventDetails(deliveryKey);
                histories.add(history);
                
                history = new OrderHistory();
                history.setId(KeyGenerator.uuid());
                history.setOrderId(order.getId());
                history.setTitle("数据上传");
                history.setOperatorName("管理员");
                history.setEventType("5");
                history.setEventTime(DateUtils.addDays(order.getSubmitTime(), 9));
                history.setEventDetails(order.getOrderTestingData().getId());
                histories.add(history);
            }
            
            if (null != order.getReportGenerateTask())
            {
                history = new OrderHistory();
                history.setId(KeyGenerator.uuid());
                history.setOrderId(order.getId());
                history.setTitle("生成报告");
                history.setOperatorName("管理员");
                history.setEventType("6");
                history.setEventTime(order.getReportGenerateTask().getFinishTime());
                history.setEventDetails(order.getReportGenerateTask().getId());
                histories.add(history);
            }
        }
        
        return histories;
    }
    
    private OrderTestingData asTargetTestingData(OrderDetails order)
    {
        String sql = "select * from csms_detection_data where order_id = ?";
        List<LocusGenetype> genetypes = csms.query(sql, new Object[] {order.getSyncKey()}, new RowMapper<LocusGenetype>()
        {
            @Override
            public LocusGenetype mapRow(ResultSet rs, int rowNum)
                throws SQLException
            {
                LocusGenetype genetype = new LocusGenetype();
                genetype.setLocus(rs.getString("LOCUS_CODE"));
                genetype.setGenetype(rs.getString("GENETYPE"));
                
                if (!StringUtils.isEmpty(genetype.getGenetype()))
                {
                    genetype.setGenetype(genetype.getGenetype().replaceAll("/", ""));
                }
                
                return genetype;
            }
        });
        
        if (genetypes.isEmpty())
        {
            return null;
        }
        
        OrderTestingData data = new OrderTestingData();
        data.setId(KeyGenerator.uuid());
        data.setOrderId(order.getId());
        data.setUploadRecordId("f8e8dfa95d6840e3b51853b82e6a6f55");
        data.setGenetypes(genetypes);
        return data;
    }
    
    private OrderReportGenerateTask asReportGenerateTask(OrderDetails order, Date reportBuildTime)
    {
        String code = order.getCode();
        
        if (StringUtils.isEmpty(code))
        {
            return null;
        }
        
        List<String> records = cdc.queryForList("select t.PATH from cdc_detection_report t where t.DETECTION_CODE = ?", new Object[] {code}, String.class);
        
        if (records.isEmpty())
        {
            return null;
        }
        
        String path = records.get(0);
        
        if (StringUtils.isEmpty(path))
        {
            return null;
        }
        
        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }
        
        OrderReportGenerateTask task = new OrderReportGenerateTask();
        task.setId(KeyGenerator.uuid());
        task.setStatus("1");
        task.setCreatorName("管理员");
        
        if (null == reportBuildTime)
        {
            Date submitTime = order.getSubmitTime();
            task.setCreateTime(DateUtils.addDays(submitTime, 10));
            task.setFinishTime(DateUtils.addSeconds(task.getCreateTime(), 30));
        }
        else
        {
            task.setFinishTime(reportBuildTime);
            task.setCreateTime(DateUtils.addSeconds(reportBuildTime, -30));
        }
        
        // 下载文件
        String date = DateFormatUtils.format(task.getFinishTime(), "yyyyMM");
        String dest = "E:\\ghealth-sync\\report\\" + date + "\\" + code + "\\";
        String filename = path.substring(path.lastIndexOf("/"));
        String suffix = filename.substring(filename.lastIndexOf("."));
        filename = "/" + code + suffix;
        
        if (filename.contains(".pdf"))
        {
            boolean success = download(path, dest + filename);
            
            if (success)
            {
                ObjectStorage storage = new ObjectStorage();
                storage.setId(KeyGenerator.uuid());
                storage.setEndpoint("oss-cn-hangzhou.aliyuncs.com");
                storage.setBucketName("ghealth");
                storage.setObjectKey("report/" + date + "/" + code + filename);
                task.setPdfFile(storage);
            }
            
            if (!path.contains("manual"))
            {
                String wordPath = path.replaceAll(".pdf", ".doc");
                String wordFilename = filename.replace(".pdf", ".doc");
                success = download(wordPath, dest + wordFilename);
                
                if (success)
                {
                    ObjectStorage storage = new ObjectStorage();
                    storage.setId(KeyGenerator.uuid());
                    storage.setEndpoint("oss-cn-hangzhou.aliyuncs.com");
                    storage.setBucketName("ghealth");
                    storage.setObjectKey("report/" + date + "/" + code + wordFilename);
                    task.setWordFile(storage);
                }
            }
        }
        else
        {
            boolean success = download(path, dest + filename);
            
            if (success)
            {
                ObjectStorage storage = new ObjectStorage();
                storage.setId(KeyGenerator.uuid());
                storage.setEndpoint("oss-cn-hangzhou.aliyuncs.com");
                storage.setBucketName("ghealth");
                storage.setObjectKey("report/" + date + "/" + code + filename);
                task.setWordFile(storage);
            }
        }
        
        return task;
    }
    
    private boolean download(String uri, String filepath)
    {
        //        if (new File(filepath).exists())
        //        {
        //            System.out.println("已下载" + filepath);
        //            return true;
        //        }
        //        
        //        String url = "http://csms.hsgene.com/cdc" + uri;
        //        
        //        InputStream in = null;
        //        
        //        try
        //        {
        //            URL u = new URL(url);
        //            in = u.openStream();
        //            File file = new File(filepath);
        //            
        //            if (!file.getParentFile().exists())
        //            {
        //                file.getParentFile().mkdirs();
        //            }
        //            
        //            FileCopyUtils.copy(in, new FileOutputStream(filepath));
        //            return true;
        //        }
        //        catch (MalformedURLException e)
        //        {
        //            System.out.println("Download error, code " + uri);
        //            return false;
        //        }
        //        catch (IOException e)
        //        {
        //            e.printStackTrace();
        //            System.out.println("Download error, code " + uri);
        //            return false;
        //        }
        //        finally
        //        {
        //            if (null != in)
        //            {
        //                try
        //                {
        //                    in.close();
        //                }
        //                catch (IOException e)
        //                {
        //                    e.printStackTrace();
        //                    System.out.println("Download error, code " + uri);
        //                    return false;
        //                }
        //            }
        //        }
        return true;
    }
    
    private String asTargetProductId(String productId)
    {
        String id = productMappings.get(productId);
        
        if (null != id)
        {
            if (StringUtils.isEmpty(id))
            {
                System.out.println(MessageFormat.format("Product {0} has not key.", productId));
                throw new IllegalStateException();
            }
            
            return id;
        }
        
        try
        {
            String sql = "select id from ghealth_testing_product where SYNC_KEY = ?";
            System.out.println("Product" + productId);
            id = ghealth.queryForObject(sql, new Object[] {productId}, String.class);
        }
        catch (Exception e)
        {
            System.out.println(productId);
            throw new IllegalStateException(e);
        }
        
        if (StringUtils.isEmpty(id))
        {
            System.out.println(MessageFormat.format("Product {0} has not key.", productId));
            throw new IllegalStateException();
        }
        
        productMappings.put(productId, id);
        return id;
    }
    
    private String asTargetCustomerId(String customerId)
    {
        String id = customerMappings.get(customerId);
        
        if (null != id)
        {
            if (StringUtils.isEmpty(id))
            {
                System.out.println(MessageFormat.format("Customer {0} has not key.", customerId));
                throw new IllegalStateException();
            }
            
            return id;
        }
        
        try
        {
            String sql = "select id from ghealth_customer where SYNC_KEY = ?";
            id = ghealth.queryForObject(sql, new Object[] {customerId}, String.class);
            
            if (StringUtils.isEmpty(id))
            {
                System.out.println(MessageFormat.format("Customer {0} has not key.", customerId));
                throw new IllegalStateException();
            }
        }
        catch (Exception e)
        {
            System.out.println(customerId);
            throw new IllegalStateException();
        }
        
        customerMappings.put(customerId, id);
        return id;
    }
    
    private String asTargetAgencyId(String agencyId)
    {
        String id = agencyMappings.get(agencyId);
        
        if (null != id)
        {
            if (StringUtils.isEmpty(id))
            {
                System.out.println(MessageFormat.format("Agency {0} has not key.", agencyId));
                throw new IllegalStateException();
            }
            
            return id;
        }
        
        String sql = "select id from ghealth_agency where SYNC_KEY = ?";
        id = ghealth.queryForObject(sql, new Object[] {agencyId}, String.class);
        
        if (StringUtils.isEmpty(id))
        {
            System.out.println(MessageFormat.format("Agency {0} has not key.", agencyId));
            throw new IllegalStateException();
        }
        
        agencyMappings.put(agencyId, id);
        return id;
    }
    
    private String asTargetSampleType(String type)
    {
        if ("1".equals(type))
        {
            return "1";
        }
        else if ("2".equals(type))
        {
            return "3";
        }
        else if ("0".equals(type))
        {
            return "2";
        }
        else
        {
            return null;
        }
    }
    
    private String asTargetStatus(String status)
    {
        int value = Integer.valueOf(status);
        String s;
        
        switch (value)
        {
            case 0:
            case 1:
                s = "0";
                break;
            
            case 2:
            case 3:
                s = "1";
                break;
            
            case 4:
                s = "2";
                break;
            
            case 5:
                s = "3";
                break;
            
            case 6:
                s = "4";
                break;
            
            case 7:
                s = "5";
                break;
            
            default:
                s = "5";
                break;
        }
        
        return s;
    }
    
    private boolean asTargetReportPrintRequired(String s)
    {
        return "1".equals(s);
    }
    
    private void migrate(List<OrderDetails> orders, String signKey, String deliveryKey)
    {
        insertOrders(orders);
        insertOrderHistories(orders);
        insertOrderTestingDatas(orders);
        insertSampleSignRecord(orders, signKey);
        insertSampleDeliveryRecord(orders, deliveryKey);
        insertOrderReports(orders);
    }
    
    private void insertDefaultUploadRecord()
    {
        String sql = "INSERT INTO GHEALTH_TESTING_DATA_UPLOAD_RECORD (ID, TITLE, FILENAME, DOWNLOAD_URL, UPLOAD_TIME, UPLOADER_NAME) values (?,?,?,?,?,?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                Date timestamp = null;
                
                try
                {
                    timestamp = DateUtils.parseDate("2017-12-31", "yyyy-MM-dd");
                }
                catch (Exception e)
                {
                    //
                }
                
                int index = 1;
                ps.setString(index++, "f8e8dfa95d6840e3b51853b82e6a6f55");
                ps.setString(index++, "历史数据同步");
                ps.setString(index++, "历史数据同步.txt");
                ps.setString(index++, "-");
                ps.setTimestamp(index++, new Timestamp(timestamp.getTime()));
                ps.setString(index++, "管理员");
                return ps.execute();
            }
        });
    }
    
    private void insertOrders(List<OrderDetails> orders)
    {
        String sql =
            "INSERT INTO GHEALTH_ORDER (ID, PRODUCT_ID, CUSTOMER_ID, AGENCY_ID, CODE, ACTUAL_PRICE, SAMPLE_TYPE, STATUS, SUBMIT_TIME, SUBMITOR_NAME, REPORT_PRINT_REQUIRED, REPORT_GENERATE_TIME, REPORT_GENERATE_TASK, CREATE_TIME, CREATOR_NAME, DELETED, SYNC_KEY) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (OrderDetails order : orders)
                {
                    addOrderBatch(order, ps);
                }
                
                return ps.executeBatch();
            }
        });
    }
    
    private void addOrderBatch(OrderDetails data, PreparedStatement ps)
        throws SQLException
    {
        int index = 1;
        ps.setString(index++, data.getId());
        ps.setString(index++, data.getProductId());
        ps.setString(index++, data.getCustomerId());
        ps.setString(index++, data.getAgencyId());
        ps.setString(index++, data.getCode());
        ps.setBigDecimal(index++, null == data.getActualPrice() ? BigDecimal.ZERO : data.getActualPrice());
        ps.setString(index++, data.getSampleType());
        ps.setString(index++, data.getStatus());
        ps.setTimestamp(index++, null == data.getSubmitTime() ? null : new Timestamp(data.getSubmitTime().getTime()));
        ps.setString(index++, data.getSubmitorName());
        ps.setBoolean(index++, data.isReportPrintRequired());
        ps.setTimestamp(index++, null == data.getReportGenerateTask() ? null : new Timestamp(data.getReportGenerateTask().getFinishTime().getTime()));
        ps.setString(index++, null == data.getReportGenerateTask() ? null : data.getReportGenerateTask().getId());
        ps.setTimestamp(index++, null == data.getCreateTime() ? null : new Timestamp(data.getCreateTime().getTime()));
        ps.setString(index++, data.getCreatorName());
        ps.setBoolean(index++, false);
        ps.setString(index++, data.getSyncKey());
        ps.addBatch();
    }
    
    private void insertOrderHistories(List<OrderDetails> orders)
    {
        List<OrderHistory> histories = new ArrayList<OrderHistory>();
        
        for (OrderDetails order : orders)
        {
            if (!CollectionUtils.isEmpty(order.getHistories()))
            {
                histories.addAll(order.getHistories());
            }
        }
        
        String sql = "INSERT INTO GHEALTH_ORDER_HISTORY (ID, ORDER_ID, TITLE, EVENT_TYPE, EVENT_DETAILS, EVENT_TIME, OPERATOR_NAME) values (?,?,?,?,?,?,?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (OrderHistory history : histories)
                {
                    addOrderHistoryBatch(history, ps);
                }
                
                return ps.executeBatch();
            }
        });
    }
    
    private void addOrderHistoryBatch(OrderHistory data, PreparedStatement ps)
        throws SQLException
    {
        int index = 1;
        ps.setString(index++, data.getId());
        ps.setString(index++, data.getOrderId());
        ps.setString(index++, data.getTitle());
        ps.setString(index++, data.getEventType());
        ps.setString(index++, data.getEventDetails());
        ps.setTimestamp(index++, null == data.getEventTime() ? null : new Timestamp(data.getEventTime().getTime()));
        ps.setString(index++, data.getOperatorName());
        ps.addBatch();
    }
    
    private void insertOrderTestingDatas(List<OrderDetails> orders)
    {
        List<OrderTestingData> testingDatas = new ArrayList<OrderTestingData>();
        
        for (OrderDetails order : orders)
        {
            if (null != order.getOrderTestingData())
            {
                testingDatas.add(order.getOrderTestingData());
            }
        }
        
        String sql = "INSERT INTO GHEALTH_ORDER_TESTING_DATA (ID, ORDER_ID, UPLOAD_RECORD_ID, DATA_DETAILS) values (?,?,?,?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (OrderTestingData testingData : testingDatas)
                {
                    addOrderTestingDataBatch(testingData, ps);
                }
                
                return ps.executeBatch();
            }
        });
    }
    
    private void addOrderTestingDataBatch(OrderTestingData data, PreparedStatement ps)
        throws SQLException
    {
        int index = 1;
        ps.setString(index++, data.getId());
        ps.setString(index++, data.getOrderId());
        ps.setString(index++, data.getUploadRecordId());
        ps.setString(index++, CollectionUtils.isEmpty(data.getGenetypes()) ? null : JsonUtils.toJson(data.getGenetypes()));
        ps.addBatch();
    }
    
    private void insertSampleSignRecord(List<OrderDetails> orders, String signKey)
    {
        List<OrderDetails> signedOrders = new ArrayList<OrderDetails>();
        
        for (OrderDetails order : orders)
        {
            if (null != order.getOrderTestingData())
            {
                signedOrders.add(order);
            }
        }
        
        if (CollectionUtils.isEmpty(signedOrders))
        {
            return;
        }
        
        Date timestamp = signedOrders.get(signedOrders.size() - 1).getSubmitTime();
        timestamp = DateUtils.addDays(timestamp, 1);
        
        final Date time = timestamp;
        
        String sql = "INSERT INTO GHEALTH_SAMPLE_SIGN (ID, SAMPLE_COUNT, OPERATOR_NAME, OPERATE_TIME) VALUES (?, ?, ?, ?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                int index = 1;
                ps.setString(index++, signKey);
                ps.setInt(index++, signedOrders.size());
                ps.setString(index++, "管理员");
                ps.setTimestamp(index++, new Timestamp(time.getTime()));
                return ps.execute();
            }
        });
        
        sql = "INSERT INTO GHEALTH_SAMPLE_SIGN_DETAILS (ID, SIGN_RECORD_ID, ORDER_ID) VALUES (?, ?, ?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (OrderDetails signedOrder : signedOrders)
                {
                    int index = 1;
                    ps.setString(index++, KeyGenerator.uuid());
                    ps.setString(index++, signKey);
                    ps.setString(index++, signedOrder.getId());
                    ps.addBatch();
                }
                
                return ps.executeBatch();
            }
        });
    }
    
    private void insertSampleDeliveryRecord(List<OrderDetails> orders, String deliveryKey)
    {
        List<OrderDetails> deliveryOrders = new ArrayList<OrderDetails>();
        
        for (OrderDetails order : orders)
        {
            if (null != order.getOrderTestingData())
            {
                deliveryOrders.add(order);
            }
        }
        
        if (CollectionUtils.isEmpty(deliveryOrders))
        {
            return;
        }
        
        Date timestamp = deliveryOrders.get(deliveryOrders.size() - 1).getSubmitTime();
        timestamp = DateUtils.addDays(timestamp, 2);
        
        final Date time = timestamp;
        
        String sql = "INSERT INTO GHEALTH_SAMPLE_DELIVERY (ID, SAMPLE_COUNT, OPERATOR_NAME, OPERATE_TIME) VALUES (?, ?, ?, ?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                int index = 1;
                ps.setString(index++, deliveryKey);
                ps.setInt(index++, deliveryOrders.size());
                ps.setString(index++, "管理员");
                ps.setTimestamp(index++, new Timestamp(time.getTime()));
                return ps.execute();
            }
        });
        
        sql = "INSERT INTO GHEALTH_SAMPLE_DELIVERY_DETAILS (ID, DELIVERY_RECORD_ID, ORDER_ID) VALUES (?, ?, ?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (OrderDetails deliveryOrder : deliveryOrders)
                {
                    int index = 1;
                    ps.setString(index++, KeyGenerator.uuid());
                    ps.setString(index++, deliveryKey);
                    ps.setString(index++, deliveryOrder.getId());
                    ps.addBatch();
                }
                
                return ps.executeBatch();
            }
        });
    }
    
    private void insertOrderReports(List<OrderDetails> orders)
    {
        List<OrderReportGenerateTask> tasks = new ArrayList<OrderReportGenerateTask>();
        
        List<ObjectStorage> objects = new ArrayList<ObjectStorage>();
        
        for (OrderDetails order : orders)
        {
            if (null != order.getReportGenerateTask())
            {
                tasks.add(order.getReportGenerateTask());
                
                if (null != order.getReportGenerateTask().getWordFile())
                {
                    objects.add(order.getReportGenerateTask().getWordFile());
                }
                
                if (null != order.getReportGenerateTask().getPdfFile())
                {
                    objects.add(order.getReportGenerateTask().getPdfFile());
                }
            }
        }
        
        String sql =
            "INSERT INTO GHEALTH_ORDER_REPORT_GENERATE_TASK (ID, STATUS, CREATE_TIME, CREATOR_NAME, FINISH_TIME, WORD_FILE_URL, PDF_FILE_URL) values (?, ?, ?, ?, ?, ? ,?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (OrderReportGenerateTask task : tasks)
                {
                    int index = 1;
                    ps.setString(index++, task.getId());
                    ps.setString(index++, task.getStatus());
                    ps.setTimestamp(index++, null == task.getCreateTime() ? null : new Timestamp(task.getCreateTime().getTime()));
                    ps.setString(index++, task.getCreatorName());
                    ps.setTimestamp(index++, null == task.getFinishTime() ? null : new Timestamp(task.getFinishTime().getTime()));
                    ps.setString(index++, null == task.getWordFile() ? null : task.getWordFile().getId());
                    ps.setString(index++, null == task.getPdfFile() ? null : task.getPdfFile().getId());
                    ps.addBatch();
                }
                
                return ps.executeBatch();
            }
        });
        
        sql = "INSERT INTO GHEALTH_OBJECT_STORAGE (ID, STORAGE_TYPE, STORAGE_DETAILS) values (?, ?, ?)";
        
        ghealth.execute(sql, new PreparedStatementCallback<Object>()
        {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                throws SQLException, DataAccessException
            {
                for (ObjectStorage object : objects)
                {
                    Map<String, String> attributes = new HashMap<String, String>();
                    attributes.put("endpoint", object.getEndpoint());
                    attributes.put("bucketName", object.getBucketName());
                    attributes.put("objectKey", object.getObjectKey());
                    
                    int index = 1;
                    ps.setString(index++, object.getId());
                    ps.setString(index++, object.getStorageType());
                    ps.setString(index++, JsonUtils.toJson(attributes));
                    ps.addBatch();
                }
                
                return ps.executeBatch();
            }
        });
    }
}
