package com.sms.service;

import com.sms.model.AddInventoryItemsDetails;

import java.util.List;

public interface AddInventoryItemsService {
    public AddInventoryItemsDetails addItems(AddInventoryItemsDetails addItemsDetails, String schoolCode) throws Exception;
    public AddInventoryItemsDetails getItemsById(int addItemsId, String schoolCode) throws Exception;
    public List<AddInventoryItemsDetails> getAllItems(String schoolCode) throws Exception;
    public AddInventoryItemsDetails updateItems(AddInventoryItemsDetails addItemsDetails, int addItemsId, String schoolCode) throws Exception;
    public boolean softDeleteItems(int addItemsId, String schoolCode) throws Exception;
}
