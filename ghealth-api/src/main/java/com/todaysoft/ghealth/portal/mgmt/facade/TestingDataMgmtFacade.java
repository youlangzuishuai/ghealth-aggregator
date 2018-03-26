package com.todaysoft.ghealth.portal.mgmt.facade;

import java.util.*;

import com.todaysoft.ghealth.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import com.todaysoft.ghealth.base.request.QueryDetailsRequest;
import com.todaysoft.ghealth.base.response.ObjectResponse;
import com.todaysoft.ghealth.base.response.Pager;
import com.todaysoft.ghealth.base.response.PagerResponse;
import com.todaysoft.ghealth.base.response.model.TestingDataUploadRecordDTO;
import com.todaysoft.ghealth.mgmt.model.OrderTestingDataDTO;
import com.todaysoft.ghealth.mgmt.request.QueryTestingDataUploadRecordsRequest;
import com.todaysoft.ghealth.mgmt.request.TestingDataUploadRequest;
import com.todaysoft.ghealth.mybatis.model.ManagementAccountDetails;
import com.todaysoft.ghealth.mybatis.model.OrderTestingData;
import com.todaysoft.ghealth.mybatis.model.TestingDataUploadRecord;
import com.todaysoft.ghealth.mybatis.searcher.TestingDataUploadRecordSearcher;
import com.todaysoft.ghealth.portal.mgmt.MgmtErrorCode;
import com.todaysoft.ghealth.portal.mgmt.facade.wrapper.TestingDataUploadRecordWrapper;
import com.todaysoft.ghealth.service.IAccountService;
import com.todaysoft.ghealth.service.ITestingDataService;
import com.todaysoft.ghealth.service.impl.PagerQueryer;
import com.todaysoft.ghealth.service.impl.ServiceException;
import com.todaysoft.ghealth.service.wrapper.PagerWrapper;
import com.todaysoft.ghealth.utils.IdGen;

@Component
public class TestingDataMgmtFacade
{
    @Autowired
    private IAccountService accountService;
    
    @Autowired
    private ITestingDataService testingDataService;
    
    @Autowired
    private TestingDataUploadRecordWrapper wrapper;
    
    public PagerResponse<TestingDataUploadRecordDTO> pager(@RequestBody QueryTestingDataUploadRecordsRequest request)
    {
        int pageNo = null == request.getPageNo() ? 1 : request.getPageNo();
        int pageSize = null == request.getPageSize() ? 10 : request.getPageSize();
        
        TestingDataUploadRecordSearcher searcher = new TestingDataUploadRecordSearcher();
        searcher.setUploaderName(request.getUploaderName());
        
        if (null != request.getUploadTimeStart())
        {
            Date start = new Date(request.getUploadTimeStart());
            searcher.setUploadTimeStart(DateUtils.truncate(start, Calendar.DATE));
        }
        
        if (null != request.getUploadTimeEnd())
        {
            Date end = new Date(request.getUploadTimeEnd());
            searcher.setUploadTimeEnd(DateUtils.addDays(DateUtils.truncate(end, Calendar.DATE), 1));
        }
        
        PagerQueryer<TestingDataUploadRecord> queryer = new PagerQueryer<TestingDataUploadRecord>(testingDataService);
        Pager<TestingDataUploadRecord> pager = queryer.query(searcher, pageNo, pageSize);
        return new PagerResponse<TestingDataUploadRecordDTO>(new PagerWrapper<TestingDataUploadRecord, TestingDataUploadRecordDTO>(wrapper).wrap(pager));
    }
    
    public ObjectResponse<TestingDataUploadRecordDTO> get(@RequestBody QueryDetailsRequest request)
    {
        if (StringUtils.isEmpty(request.getId()))
        {
            throw new ServiceException(MgmtErrorCode.API_ILLEGAL_ARGUMENT);
        }
        
        TestingDataUploadRecord entity = testingDataService.get(request.getId());
        return new ObjectResponse<TestingDataUploadRecordDTO>(wrapper.wrap(entity));
    }
    
    public Map<String,String> upload(TestingDataUploadRequest request)
    {
        ManagementAccountDetails account = accountService.getManagementAccountDetails(request.getToken());
        
        if (null == account)
        {
            throw new IllegalStateException();
        }
        
        TestingDataUploadRecord uploadRecord = new TestingDataUploadRecord();
        uploadRecord.setId(IdGen.uuid());
        uploadRecord.setTitle(request.getTitle());
        uploadRecord.setFilename(request.getFilename());
        uploadRecord.setDownloadUrl(request.getFileUri());
        uploadRecord.setUploaderName(account.getName());
        uploadRecord.setUploadTime(new Date());
        
        OrderTestingData testingData;
        List<OrderTestingData> testingDatas = new ArrayList<OrderTestingData>();
        
        if (!CollectionUtils.isEmpty(request.getOrderGenetypes()))
        {
            for (OrderTestingDataDTO orderGenetypes : request.getOrderGenetypes())
            {
                testingData = new OrderTestingData();
                testingData.setId(IdGen.uuid());
                testingData.setOrderId(orderGenetypes.getOrderId());
                testingData.setUploadRecordId(uploadRecord.getId());
                testingData.setDetails(JsonUtils.toJson(orderGenetypes.getGenetypes()));
                testingDatas.add(testingData);
            }
        }
        
       return testingDataService.upload(uploadRecord, testingDatas);
    }
}
