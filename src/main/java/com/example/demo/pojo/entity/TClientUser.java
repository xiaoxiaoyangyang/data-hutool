package com.example.demo.pojo.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author: guozhiyang
 * @Date: 2019/6/17 11:51
 */
@Data
public class TClientUser
{
    private byte[] guid;
    private String uuid;
    private String wechatUnionId;
    private String wechatOpenId;
    private Integer gender;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headImg;
    private String nickName;
    private String phoneNumer;
    private Integer deleted;
    private Integer disabled;
    private Integer locked;
    private Timestamp lockedExpiredAt;
    private String userReferenceCode;
    private String inviterUserReferenceCode;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
