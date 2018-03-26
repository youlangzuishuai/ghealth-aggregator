package com.todaysoft.ghealth.mvc;

import com.todaysoft.ghealth.base.response.model.AuthorityNode;
import com.todaysoft.ghealth.model.agencyAccount.AgencyAccount;
import com.todaysoft.ghealth.model.agencyAccount.AgencyAccountSearcher;
import com.todaysoft.ghealth.model.role.Role;
import com.todaysoft.ghealth.model.role.RoleSearcher;
import com.todaysoft.ghealth.service.IAgencyAccountService;
import com.todaysoft.ghealth.service.IRoleService;
import com.todaysoft.ghealth.support.BaseAction;
import com.todaysoft.ghealth.support.ModelResolver;
import com.todaysoft.ghealth.support.PagerArgs;
import com.todaysoft.ghealth.support.Pagination;
import com.todaysoft.ghealth.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleAction extends BaseAction {

    @Autowired
    private IRoleService service;

    @Autowired
    private IAgencyAccountService agencyAccountService;

    @RequestMapping("/list.jsp")
    public String paginations(RoleSearcher searcher, PagerArgs pagerArgs, ModelMap model, HttpSession session)
    {
        Pagination<Role> pagination = service.pagination(searcher, pagerArgs.getPageNo(), pagerArgs.getPageSize());
        model.addAttribute("pagination", pagination);
        model.addAttribute("searcher", searcher);
        session.setAttribute("s-searcher", searcher);
        return "role/role_list";
    }

    @RequestMapping(value = "/create.jsp", method = RequestMethod.GET)
    public String create(Role data, ModelMap model)
    {
        if (StringUtils.isNotEmpty(data.getId()))
        {
            model.addAttribute("data", service.get(data));
        }
        List<AuthorityNode> authorityNodes = service.getAuthorityNodes();
        String jsonArray = JsonUtils.toJson(authorityNodes);
        model.addAttribute("nodes", jsonArray);
        return "role/role_form";
    }

    @RequestMapping(value = "/create.jsp", method = RequestMethod.POST)
    public String create(Role data, ModelMap model, HttpSession session)
    {
        service.create(data);
        return redirectList(model, session, "/role/list.jsp");
    }

    @RequestMapping(value = "/modify.jsp", method = RequestMethod.GET)
    public String modify(Role data, ModelMap model)
    {
        if (StringUtils.isNotEmpty(data.getId()))
        {
            model.addAttribute("data", service.get(data));
        }
        List<AuthorityNode> authorityNodes = service.getAuthorityNodes();
        String jsonArray = JsonUtils.toJson(authorityNodes);
        model.addAttribute("nodes", jsonArray);
        return "role/role_form";
    }


    @RequestMapping(value = "/modify.jsp", method = RequestMethod.POST)
    public String modify(Role data, ModelMap model, HttpSession session)
    {
        service.modify(data);
        return redirectList(model, session, "/role/list.jsp");
    }

    @RequestMapping("/delete.jsp")
    @ResponseBody
    public boolean delete(String id, ModelMap model, HttpSession session)
    {
        return service.delete(id);
    }

    @RequestMapping(value = "/reload.do")
    public String reload(ModelMap model, HttpSession session)
    {
        return redirectList(model, session);
    }

    private String redirectList(ModelMap model, HttpSession session)
    {
        model.clear();
        new ModelResolver(session.getAttribute("s-searcher"), model).resolve();
        return "redirect:/role/list.jsp";
    }

    @RequestMapping("/isNameUnique.do")
    @ResponseBody
    public boolean isNameUnique(String id,String name)
    {
        return service.isNameUnique(id,name);
    }

    @RequestMapping("/addUser_form.jsp")
    public String addUser(String roleId, ModelMap model)
    {
        Role data = new Role();
        data.setId(roleId);
        List<AgencyAccount> list = service.removeRepaeted(service.get(data).getAgencyAccounts(), agencyAccountService.list(new AgencyAccountSearcher()));
        model.addAttribute("agencyAccounts", list);
        model.addAttribute("roleId", roleId);
        return "role/role_addUser";
    }

    @RequestMapping(value = "/addUser.do", method = RequestMethod.POST)
    @ResponseBody
    public void addUser(Role data, ModelMap model, HttpSession session)
    {
        service.addUser(data);
    }

    @RequestMapping("json_list.do")
    @ResponseBody
    public List<Role> jsonList(RoleSearcher searcher)
    {
        Pagination<Role> pagination = service.pagination(searcher, 1, 10);
        if (CollectionUtils.isEmpty(pagination.getRecords()))
        {
            return Collections.emptyList();
        }

        List<Role> roleList = pagination.getRecords();
        return roleList;
    }


}
