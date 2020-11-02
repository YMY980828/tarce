package com.example.gw.enterprise.task;

import com.example.gw.enterprise.common.base.BaseRequestor;
import com.example.gw.enterprise.common.http.CommnAction;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by gw on 2018/8/30.
 */

public class Task_AddOrganizationInfo extends BaseRequestor {
    public String registeredNumber;
    public String customUserName;
    public String orgName;
    public String address;
    public String contactPersonName;
    public String contactPersonIdCard;
    public String contactPersonMobile;
    public String legalPersonName;
    public String legalPersonIdCard;
    public String legalPersonMobile;
    public String legalPersonIdCardFront;
    public String businessLicense;
    public String orgType;
    public String orgNature;
    public String areaCodeCity;
    public String areaCodeDistrict;
    public String areaCodeSubdistrict;

    @Override
    public Object execute() {
        RequestBody body = new FormBody.Builder()
                .add("registeredNumber", registeredNumber)
                .add("customUserName", customUserName)
                .add("orgName", orgName)
                .add("address", address)
                .add("contactPersonName", contactPersonName)
                .add("contactPersonIdCard", contactPersonIdCard)
                .add("contactPersonMobile", contactPersonMobile)
                .add("legalPersonName", legalPersonName)
                .add("legalPersonIdCard", legalPersonIdCard)
                .add("legalPersonMobile", legalPersonMobile)
                .add("legalPersonIdCardFront", legalPersonIdCardFront)
                .add("businessLicense", businessLicense)
                .add("orgType", orgType)
                .add("orgNature", orgNature)
                .add("areaCodeCity", areaCodeCity)
                .add("areaCodeDistrict", areaCodeDistrict)
                .add("areaCodeSubdistrict", areaCodeSubdistrict)
                .add("orgAttrs", "0")
                .add("industryIds", "0")
                .add("userId", "98")
                .build();
        return CommnAction.request(body, "org/saveOrgForRegister.do");
    }
}