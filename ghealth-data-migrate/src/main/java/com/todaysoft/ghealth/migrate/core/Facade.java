package com.todaysoft.ghealth.migrate.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Facade
{
    @Autowired
    private DiseaseProcessor diseaseProcessor;
    
    @Autowired
    private LocusProcessor locusProcessor;
    
    @Autowired
    private TestingItemProcessor itemProcessor;
    
    @Autowired
    private ItemLocusProcessor itemLocusProcessor;
    
    @Autowired
    private PackagesProcessor packagesProcessor;
    
    @Autowired
    private PackageItemProcessor packageItemProcessor;
    
    @Autowired
    private AgentProcessor agentProcessor;
    
    @Autowired
    private ClientProcessor clientProcessor;
    
    @Autowired
    private AgentPackagesProcessor agentPackagesProcessor;
    
    @Autowired
    private OrderProcessor orderProcessor;
    
    @Autowired
    private OrderTestingDataProcessor orderTestingDataProcessor;
    
    @Autowired
    private OrderReportItemProcessor orderReportDataProcessor;
    
    @Autowired
    private OrderReportItemLocusProcessor orderReportItemLocusProcessor;
    
    @Autowired
    private OrderReportProcessor orderReportProcessor;
    
    @Autowired
    private RenameProcessor renameProcessor;
    
    public void startup()
    {
        List<Processor> processors = getProcessors();
        processors.forEach(processor -> {
            processor.process();
            System.out.println(processor.getClass().getSimpleName() + " processed");
        });
    }
    
    private List<Processor> getProcessors()
    {
        List<Processor> processors = new ArrayList<Processor>();
        // 已同步的注释掉
        //processors.add(diseaseProcessor);
        //processors.add(locusProcessor);
        //processors.add(itemProcessor);
        //processors.add(itemLocusProcessor);
        //processors.add(packagesProcessor);
        //processors.add(packageItemProcessor);
        //processors.add(agentProcessor);
        //processors.add(agentPackagesProcessor);
        //        processors.add(clientProcessor);
        //        processors.add(orderProcessor);
        //        processors.add(orderTestingDataProcessor);
        //        processors.add(orderReportDataProcessor);
        //        processors.add(orderReportItemLocusProcessor);
        processors.add(orderReportProcessor);
        //        processors.add(renameProcessor);
        //        processors.add(fixProcessor);
        return processors;
    }
    
    @Autowired
    private FixProcessor fixProcessor;
}
