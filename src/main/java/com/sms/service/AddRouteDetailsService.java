package com.sms.service;

import com.sms.model.AddRouteDetails;
import com.sms.model.RouteGroupDto;

import java.util.List;

public interface AddRouteDetailsService {
    public String addBulkRoute(List<AddRouteDetails> addRouteDetailsList, String schoolCode) throws Exception;
    public String addBulkRouteWithoutAcceptingHashValueInUpdate(List<AddRouteDetails> addRouteDetailsList, String schoolCode) throws Exception;
    public List<AddRouteDetails> getAllRouteDetails(String schoolCode) throws Exception;
    public String deleteRouteDetails(List<Integer> routeIds,String schoolCode) throws Exception;
    public List<AddRouteDetails> getRouteDetailsBySearchText(String searchText, String schoolCode) throws Exception;
    public RouteGroupDto getGroupedRoutes(String hashValue, String schoolCode) throws Exception;
    public RouteGroupDto updateRouteByHash(RouteGroupDto dto, String schoolCode) throws Exception;
}
