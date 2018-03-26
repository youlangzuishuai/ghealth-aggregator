package com.todaysoft.ghealth.mvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.todaysoft.ghealth.base.response.model.OrderUploadRequest;
import com.todaysoft.ghealth.model.OrderUploadData;
import com.todaysoft.ghealth.model.agencyAccount.AgencyAccountDetail;
import com.todaysoft.ghealth.mvc.view.DownloadOrderDataView;
import com.todaysoft.ghealth.support.*;
import com.todaysoft.ghealth.utils.JsonUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.todaysoft.ghealth.mgmt.model.OrderReportStreamDTO;
import com.todaysoft.ghealth.model.Order;
import com.todaysoft.ghealth.model.OrderSearcher;
import com.todaysoft.ghealth.model.customer.Customer;
import com.todaysoft.ghealth.model.customer.CustomerSearcher;
import com.todaysoft.ghealth.model.product.AgencyProduct;
import com.todaysoft.ghealth.model.product.AgencyProductDetails;
import com.todaysoft.ghealth.model.product.ProductSearcher;
import com.todaysoft.ghealth.mvc.view.DownloadFileView;
import com.todaysoft.ghealth.service.IAgencyProductService;
import com.todaysoft.ghealth.service.ICustomerService;
import com.todaysoft.ghealth.service.IOrderService;
import com.todaysoft.ghealth.utils.ExportExcel;

@Controller
@RequestMapping("/order")
public class OrderAction extends BaseAction
{
    @Autowired
    private IOrderService orderService;
    
    @Autowired
    private ICustomerService customerService;
    
    @Autowired
    private IAgencyProductService agencyProductService;
    
    @Autowired
    private DownloadFileView downloadFileView;
    
    @Autowired
    private DownloadOrderDataView downloadOrderDataView;
    
    @RequestMapping("/list.jsp")
    public String list(OrderSearcher searcher, PagerArgs pagerArgs, ModelMap model, HttpSession session)
    {
        Pagination<Order> pagination = orderService.search(searcher, pagerArgs.getPageNo(), pagerArgs.getPageSize());
        model.addAttribute("searcher", searcher);
        model.addAttribute("pagination", pagination);
        session.setAttribute("s-searcher", searcher);
        return "order/order_list";
    }
    
    @RequestMapping(value = "/create.jsp", method = RequestMethod.GET)
    public String create(String id, ModelMap model)
    {
        if (!StringUtils.isEmpty(id))
        {
            Order data = orderService.getOrderById(id);
            model.addAttribute("data", data);
            model.addAttribute("customer", JsonUtils.toJson(""));
        }
        return "order/order_create";
    }
    
    @RequestMapping(value = "/create.jsp", method = RequestMethod.POST)
    public String create(Order order, ModelMap model, HttpSession session)
    {
        orderService.create(order);
        return redirectList(model, session);
    }
    
    @RequestMapping(value = "/modify.jsp", method = RequestMethod.POST)
    public String modify(Order order, ModelMap model, HttpSession session)
    {
        orderService.modify(order);
        return redirectList(model, session);
    }
    
    @RequestMapping(value = "/cancel.jsp", method = RequestMethod.POST)
    public String cancel(Order order, ModelMap model, HttpSession session)
    {
        orderService.cancel(order);
        return redirectList(model, session);
    }
    
    @RequestMapping(value = "/place.jsp")
    public String place(Order data, ModelMap model, HttpSession session)
    {
        orderService.place(data);
        return redirectList(model, session);
    }
    
    @RequestMapping(value = "/redirectCreate.jsp")
    public String redirectCreate(Order data, ModelMap model, HttpSession session)
    {
        orderService.place(data);
        return "order/order_create";
    }
    
    @RequestMapping(value = "/display.jsp", method = RequestMethod.GET)
    public String display(String id, ModelMap model)
    {
        model.addAttribute("data", orderService.getOrderById(id));
        model.addAttribute("orderHistories", orderService.getOrderHistoriesByOrderId(id));
        return "order/order_details";
    }
    
    @ResponseBody
    @RequestMapping("/validateCode.do")
    public Boolean isUniqueCode(String id, String code)
    {
        return orderService.isUniqueCode(id, code);
    }
    
    @RequestMapping("/agencyProduct_json.do")
    @ResponseBody
    public List<AgencyProduct> getAgencyProductsJson(ProductSearcher searcher)
    {
        Pagination<AgencyProduct> pagination = agencyProductService.searcher(searcher, 1, 10);
        if (CollectionUtils.isEmpty(pagination.getRecords()))
        {
            return Collections.emptyList();
        }
        return pagination.getRecords();
    }
    
    @RequestMapping("/customer_json.do")
    @ResponseBody
    public List<Customer> getCustomersJson(String name)
    {
        CustomerSearcher searcher = new CustomerSearcher();
        searcher.setName(name);
        return customerService.list(searcher);
    }
    
    @RequestMapping("/redirect")
    public String redirectList(ModelMap model, HttpSession session)
    {
        model.clear();
        new ModelResolver(session.getAttribute("s-searcher"), model).resolve();
        return "redirect:/order/list.jsp";
    }
    
    @RequestMapping("/getAgencyProductById.do")
    @ResponseBody
    public AgencyProductDetails getTestingProductById(String id)
    {
        return agencyProductService.getAgencyProductDetails(id);
    }
    
    @RequestMapping("/downloadReports.jsp")
    public String download(OrderSearcher searcher, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        List<Order> orders = orderService.list(searcher);
        String fileName = "订单列表" + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()).toString() + "_"
            + new SimpleDateFormat("HHmm").format(new Date()).toString() + ".xlsx";
        new ExportExcel(null, Order.class).setDataList(orderService.setOtherVal(orders)).write(response, fileName).dispose();
        return "redirect:/order/list.jsp";
    }
    
    @RequestMapping(value = "/parse.jsp", method = RequestMethod.POST)
    public String parse(@RequestParam MultipartFile uploadData, ModelMap model)
    {
        OrderUploadData datas = orderService.parse(uploadData);
        model.addAttribute("uploadDatas", datas);
        return "order/order_upload";
    }
    
    @RequestMapping(value = "/upload.jsp", method = RequestMethod.POST)
    public String upload(String data, ModelMap model, HttpSession session)
    {
        List<OrderUploadRequest> datas = JsonUtils.fromJson(data, new TypeReference<List<OrderUploadRequest>>()
        {
        });
        orderService.upload(datas);
        return redirectList(model, session);
    }
    
    @RequestMapping("/report/download.jsp")
    public View download(String id, String type, ModelMap model)
    {
        Order order = orderService.getOrderById(id);
        OrderReportStreamDTO report = orderService.getReport(id, type);
        model.put("name", order.getCode() + report.getSuffix());
        model.addAttribute("inputStream", new ByteArrayInputStream(report.getContent()));
        return downloadFileView;
    }
    
    @RequestMapping("/redirectMobileHtml.do")
    public String redirectMobileHtml(AgencyAccountDetail data, ModelMap model)
    {
        ProductSearcher productSearcher = new ProductSearcher();
        BeanUtils.copyProperties(data, productSearcher);
        productSearcher.setIsLogin(false);
        List<AgencyProduct> agencyProducts = agencyProductService.list(productSearcher);
        List<Map> map = agencyProductService.getSimpleAgencyProducts(agencyProducts);
        model.addAttribute("datas", JsonUtils.toJson(map));
        model.addAttribute("agencyId", data.getAgencyId());
        model.addAttribute("agencyAccountId", data.getAgencyAccountId());
        return "order/order_create_mobile";
    }
    
    @RequestMapping("/crateOrderAtMoble.do")
    @ResponseBody
    public String crateOrderAtMobile(Order data)
    {
        orderService.createOrderAtMobile(data);
        return StringUtils.isEmpty(data.getAgency().getId()) ? data.getAgencyAccountId() : data.getAgency().getId();
    }
    
    @RequestMapping("/redirectDownloadHtml.do")
    public String redirectDownloadHtml(OrderSearcher searcher, ModelMap model)
    {
        searcher.setLogin(false);
        ProductSearcher productSearcher = new ProductSearcher();
        productSearcher.setIsLogin(false);
        productSearcher.setName(searcher.getProductName());
        productSearcher.setAgencyId(searcher.getAgencyId());
        Pagination<Order> pagination = orderService.search(searcher, 1, 50);
        model.addAttribute("agencyProducts", agencyProductService.list(productSearcher));
        model.addAttribute("searcher", searcher);
        model.addAttribute("pagination", pagination);
        return "order/order_download";
    }
    
    @RequestMapping("/downloadOrderDatas.do")
    public View downloadOrderDatas(String orderIds, ModelMap model)
    {
        model.addAttribute("orderIds", orderIds);
        return downloadOrderDataView;
    }
}
