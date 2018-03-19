package com.dubboclub.dk.admin.service.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.dubboclub.dk.admin.model.Route;
import com.dubboclub.dk.admin.service.AbstractService;
import com.dubboclub.dk.admin.service.RouteService;
import com.dubboclub.dk.admin.sync.util.Pair;
import com.dubboclub.dk.admin.sync.util.SyncUtils;
import com.dubboclub.dk.admin.sync.util.Tool;

import java.util.List;

/**
 *
 * @author bieber
 * @date 2015/7/25
 */
public class RouteServiceImpl extends AbstractService implements RouteService {

    @Override
    public List<Route> listAllRoutes() {
        return filterCategoryData(new ConvertURL2Entity<Route>() {
            @java.lang.Override
            public Route convert(Pair<Long, URL> pair) {
                return SyncUtils.url2Route(pair);
            }
        },Constants.ROUTERS_CATEGORY);
    }

    @Override
    public void createRoute(Route route) {
        add(route.toUrl());
    }

    @Override
    public void deleteRoute(Long id) {
        delete(id, Constants.ROUTERS_CATEGORY);
    }

    @Override
    public void updateRoute(Route route) {
        URL oldUrl = getOneById(Constants.ROUTERS_CATEGORY,route.getId());
        update(oldUrl,route.toUrl());
    }

    /**
     * 根据serviceKey查询出匹配的Route
     * @param serviceKey 查询参数
     * @return Route
     */
    @Override
    public List<Route> listByServiceKey(final String serviceKey) {
        return filterCategoryData(new ConvertURL2Entity<Route>() {

            @Override
            public Route convert(Pair<Long, URL> pair) {
                if(pair.getValue().getPath().equals(Tool.getInterface(serviceKey))){
                    return SyncUtils.url2Route(pair);
                }else{
                    return null;
                }
            }
        }, Constants.ROUTERS_CATEGORY, Constants.VERSION_KEY, Tool.getVersion(serviceKey),Constants.GROUP_KEY,Tool.getGroup(serviceKey));
    }

    @Override
    public Route getRoute(Long id) {
        return SyncUtils.url2Route(new Pair<Long, URL>(id,getOneById(Constants.ROUTERS_CATEGORY,id)));
    }

    @Override
    public void enable(Long id) {
        Route route = getRoute(id);
        if(route.isEnabled()){
            return ;
        }
        route.setEnabled(true);
        updateRoute(route);
    }

    @Override
    public void disable(Long id) {
        Route route = getRoute(id);
        if(!route.isEnabled()){
            return ;
        }
        route.setEnabled(false);
        updateRoute(route);
    }
}
