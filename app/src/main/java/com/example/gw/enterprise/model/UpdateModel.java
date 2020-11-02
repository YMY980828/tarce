package com.example.gw.enterprise.model;

/**
 * Created by gw on 15/9/21.
 */
public class UpdateModel {
    public String url;//下载地址
    public String content;//更新内容
    public String hasNew;//是否有新版本 1-有 0-否
    public String isForce;//是否强制更新 1-是 0-否
    public String newVersion;//待更新的版本号

}
