package com.sms.service.impl;

import com.sms.dao.AddRouteDetailsDao;
import com.sms.model.AddRouteDetails;
import com.sms.model.RouteGroupDto;
import com.sms.service.AddRouteDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddRouteDetailsServiceImpl implements AddRouteDetailsService {
    @Autowired
    private AddRouteDetailsDao addRouteDetailsDao;

    @Override
    public String addBulkRoute(List<AddRouteDetails> addRouteDetailsList, String schoolCode) throws Exception {
        return addRouteDetailsDao.addBulkRoute(addRouteDetailsList, schoolCode);
    }

    @Override
    public String addBulkRouteWithoutAcceptingHashValueInUpdate(List<AddRouteDetails> addRouteDetailsList, String schoolCode) throws Exception {
        return addRouteDetailsDao.addBulkRouteWithoutAcceptingHashValueInUpdate(addRouteDetailsList, schoolCode);
    }

    @Override
    public List<AddRouteDetails> getAllRouteDetails(String schoolCode) throws Exception {
        return addRouteDetailsDao.getAllRouteDetails(schoolCode);
    }

    @Override
    public String deleteRouteDetails(List<Integer> routeIds, String schoolCode) throws Exception {
        return addRouteDetailsDao.deleteRouteDetails(routeIds, schoolCode);
    }

    @Override
    public List<AddRouteDetails> getRouteDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return addRouteDetailsDao.getRouteDetailsBySearchText(searchText, schoolCode);
    }

    @Override
    public RouteGroupDto getGroupedRoutes(String hashValue, String schoolCode) throws Exception {
        return addRouteDetailsDao.getGroupedRoutes(hashValue, schoolCode);
    }

    @Override
    public RouteGroupDto updateRouteByHash(RouteGroupDto dto, String schoolCode) throws Exception {
        return addRouteDetailsDao.updateRouteByHash(dto, schoolCode);
    }
}
