package com.dubboclub.dk.admin.service;

import com.dubboclub.dk.admin.model.Route;

import java.util.List;

/**
 *
 * @author bieber
 * @date 2015/6/3
 */
public interface RouteService {

    /**
     * 列出所有的路由信息
     * @return 所有的路由信息
     */
    List<Route> listAllRoutes();

    void createRoute(Route route);

    void deleteRoute(Long id);

    void updateRoute(Route route);

    List<Route> listByServiceKey(String serviceKey);

    Route getRoute(Long id);

    void enable(Long id);

    void disable(Long id);


}
