package com.example.gw.enterprise.model;

/**
 * Created by gw on 2018/10/8.
 */

public class OrgNameForRegisterModel {

    /**
     * address : 上冈镇宋楼村
     * apiFrom : 2
     * areaCodeCity : 3209
     * areaCodeDistrict : 320925
     * areaCodeProvince : 32
     * areaCodeSubdistrict : 320925108
     * contactPerson : {"contactPersonId":"486842949015962189","fax":"0515-86492116","isDel":0,"mobile":"13182107118","name":"秦正银"}
     * contactPersonId : 486842949015962189
     * createTime : 2012-03-09
     * creater : 7777
     * industryIds : 0
     * isLong : 1
     * legalPerson : {"legalPersonId":"486842949011768417"}
     * legalPersonId : 486842949011768417
     * licenseType : 0
     * orgAttrName : 一般主体
     * orgAttrs : 0
     * orgId : 486842949011767360
     * orgName : 江苏绿盛现代农业发展有限公司
     * orgNature : 0
     * orgNatureName : 企业/个体工商户
     * orgType : 0
     * orgTypeName : 生产主体
     * state : 0
     */

    private String address;
    private int apiFrom;
    private String areaCodeCity;
    private String areaCodeDistrict;
    private String areaCodeProvince;
    private String areaCodeSubdistrict;
    private ContactPersonBean contactPerson;
    private String contactPersonId;
    private String createTime;
    private String creater;
    private String industryIds;
    private int isLong;
    private LegalPersonBean legalPerson;
    private String legalPersonId;
    private int licenseType;
    private String orgAttrName;
    private String orgAttrs;
    private String orgId;
    private String orgName;
    private int orgNature;
    private String orgNatureName;
    private int orgType;
    private String orgTypeName;
    private int state;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getApiFrom() {
        return apiFrom;
    }

    public void setApiFrom(int apiFrom) {
        this.apiFrom = apiFrom;
    }

    public String getAreaCodeCity() {
        return areaCodeCity;
    }

    public void setAreaCodeCity(String areaCodeCity) {
        this.areaCodeCity = areaCodeCity;
    }

    public String getAreaCodeDistrict() {
        return areaCodeDistrict;
    }

    public void setAreaCodeDistrict(String areaCodeDistrict) {
        this.areaCodeDistrict = areaCodeDistrict;
    }

    public String getAreaCodeProvince() {
        return areaCodeProvince;
    }

    public void setAreaCodeProvince(String areaCodeProvince) {
        this.areaCodeProvince = areaCodeProvince;
    }

    public String getAreaCodeSubdistrict() {
        return areaCodeSubdistrict;
    }

    public void setAreaCodeSubdistrict(String areaCodeSubdistrict) {
        this.areaCodeSubdistrict = areaCodeSubdistrict;
    }

    public ContactPersonBean getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(ContactPersonBean contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getIndustryIds() {
        return industryIds;
    }

    public void setIndustryIds(String industryIds) {
        this.industryIds = industryIds;
    }

    public int getIsLong() {
        return isLong;
    }

    public void setIsLong(int isLong) {
        this.isLong = isLong;
    }

    public LegalPersonBean getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(LegalPersonBean legalPerson) {
        this.legalPerson = legalPerson;
    }

    public String getLegalPersonId() {
        return legalPersonId;
    }

    public void setLegalPersonId(String legalPersonId) {
        this.legalPersonId = legalPersonId;
    }

    public int getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(int licenseType) {
        this.licenseType = licenseType;
    }

    public String getOrgAttrName() {
        return orgAttrName;
    }

    public void setOrgAttrName(String orgAttrName) {
        this.orgAttrName = orgAttrName;
    }

    public String getOrgAttrs() {
        return orgAttrs;
    }

    public void setOrgAttrs(String orgAttrs) {
        this.orgAttrs = orgAttrs;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getOrgNature() {
        return orgNature;
    }

    public void setOrgNature(int orgNature) {
        this.orgNature = orgNature;
    }

    public String getOrgNatureName() {
        return orgNatureName;
    }

    public void setOrgNatureName(String orgNatureName) {
        this.orgNatureName = orgNatureName;
    }

    public int getOrgType() {
        return orgType;
    }

    public void setOrgType(int orgType) {
        this.orgType = orgType;
    }

    public String getOrgTypeName() {
        return orgTypeName;
    }

    public void setOrgTypeName(String orgTypeName) {
        this.orgTypeName = orgTypeName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static class ContactPersonBean {
        /**
         * contactPersonId : 486842949015962189
         * fax : 0515-86492116
         * isDel : 0
         * mobile : 13182107118
         * name : 秦正银
         */

        private String contactPersonId;
        private String fax;
        private int isDel;
        private String mobile;
        private String name;

        public String getContactPersonId() {
            return contactPersonId;
        }

        public void setContactPersonId(String contactPersonId) {
            this.contactPersonId = contactPersonId;
        }

        public String getFax() {
            return fax;
        }

        public void setFax(String fax) {
            this.fax = fax;
        }

        public int getIsDel() {
            return isDel;
        }

        public void setIsDel(int isDel) {
            this.isDel = isDel;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class LegalPersonBean {
        /**
         * legalPersonId : 486842949011768417
         */

        private String legalPersonId;

        public String getLegalPersonId() {
            return legalPersonId;
        }

        public void setLegalPersonId(String legalPersonId) {
            this.legalPersonId = legalPersonId;
        }
    }
}
