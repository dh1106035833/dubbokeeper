package com.dubboclub.dk.web.controller;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.dubboclub.dk.admin.model.Provider;
import com.dubboclub.dk.admin.model.Route;
import com.dubboclub.dk.admin.service.ProviderService;
import com.dubboclub.dk.admin.service.RouteService;
import com.dubboclub.dk.web.model.BasicResponse;
import com.dubboclub.dk.web.model.RouteAbstractInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bieber
 * @date 2015/7/25
 */
@Controller
@RequestMapping("/route")
public class RouterController {

    private static Logger logger = LoggerFactory.getLogger(RouterController.class);

    @Autowired
    private RouteService routeService;

    @Autowired
    private ProviderService providerService;

    //点击查看路由规则明细接口
    @RequestMapping("provider/{serviceKey}/list.htm")
    public @ResponseBody List<Route> queryRoutesByServiceKey(@PathVariable("serviceKey")String serviceKey) throws UnsupportedEncodingException {
        serviceKey = URLDecoder.decode(serviceKey, "UTF-8");
        return routeService.listByServiceKey(serviceKey);
    }

    @RequestMapping("create.htm")
    public @ResponseBody
    BasicResponse createRoute(@RequestBody Route route){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        routeService.createRoute(route);
        return response;
    }

    @RequestMapping("batch-{type}.htm")
    public @ResponseBody BasicResponse batchDelete(@RequestParam("ids")String ids,@PathVariable("type") String type){
        BasicResponse response = new BasicResponse();
        String[] idArray = Constants.COMMA_SPLIT_PATTERN.split(ids);
        if("delete".equals(type)){
            for(String id:idArray){
                routeService.deleteRoute(Long.parseLong(id));
            }
        }else if("enable".equals(type)){
            for(String id:idArray){
                routeService.enable(Long.parseLong(id));
            }
        }else if("disable".equals(type)){
            for(String id:idArray){
                routeService.disable(Long.parseLong(id));
            }
        }
        response.setResult(BasicResponse.SUCCESS);
        return response;
    }


    @RequestMapping("{type}_{id}.htm")
    public @ResponseBody BasicResponse delete(@PathVariable("type")String type,@PathVariable("id")Long id){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        if("delete".equals(type)){
            routeService.deleteRoute(id);
        }else if("enable".equals(type)){
            routeService.enable(id);
        }else if("disable".equals(type)){
            routeService.disable(id);
        }else{
            response.setResult(BasicResponse.FAILED);
        }
        return response;
    }

    @RequestMapping("update.htm")
    public @ResponseBody BasicResponse updateRoute(@RequestBody Route route){
        BasicResponse response = new BasicResponse();
        response.setResult(BasicResponse.SUCCESS);
        route.setRule(null);
        routeService.updateRoute(route);
        return response;
    }

//    @ResponseBody
//    @RequestMapping("list.htm")
//    public List<RouteAbstractInfo> list(){
//        //这么写效率低下，并不是所有的provider都有route信息，应该先查route，再查在provider中是否存在
//        List<Provider> providers = providerService.listAllProvider();
//        List<RouteAbstractInfo> routeAbstractInfos = new ArrayList<RouteAbstractInfo>();
//        for(Provider provider :providers){
//            try {
//                int count = routeService.listByServiceKey(provider.getServiceKey()).size();
//                if(count > 0){
//                    RouteAbstractInfo routeAbstractInfo = new RouteAbstractInfo();
//                    routeAbstractInfo.setServiceKey(provider.getServiceKey());
//                    routeAbstractInfo.setApplicationName(provider.getApplication());
//                    routeAbstractInfo.setRouteCount(count);
//                    routeAbstractInfos.add(routeAbstractInfo);
//                }
//            } catch (Exception e) {
//                logger.error(e);
//            }
//        }
//        return routeAbstractInfos;
//    }

    @ResponseBody
    @RequestMapping("list.htm")
    public List<RouteAbstractInfo> list(){
        List<Route> routes = routeService.listAllRoutes();
        Map<String, RouteAbstractInfo> routeAbstractInfoMap = new HashMap<String, RouteAbstractInfo>();
        for (Route route : routes) {
            String routeAbstractInfoKey = route.getApplication() + route.getService();
            RouteAbstractInfo routeAbstractInfo = routeAbstractInfoMap.get(routeAbstractInfoKey);
            if (routeAbstractInfo == null) {
                List<Provider> providers = providerService.listProviderByServiceKey(route.getService());
                if (providers != null && providers.size() > 0) {
                    routeAbstractInfo = new RouteAbstractInfo();
                    routeAbstractInfo.setServiceKey(route.getService());
                    routeAbstractInfo.setApplicationName(providers.get(0).getApplication());
                    routeAbstractInfo.setRouteCount(1);
                    routeAbstractInfoMap.put(routeAbstractInfoKey, routeAbstractInfo);
                }
            } else {
                routeAbstractInfo.setRouteCount(routeAbstractInfo.getRouteCount() + 1);
            }
        }
        return new ArrayList<RouteAbstractInfo>(routeAbstractInfoMap.values());
    }

    //根据ID查询Route
    @RequestMapping("get_{id}.htm")
    public @ResponseBody Route getRoute(@PathVariable("id")Long id){
        return routeService.getRoute(id);
    }

}
