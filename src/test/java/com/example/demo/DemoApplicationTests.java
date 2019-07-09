package com.example.demo;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.sql.SqlExecutor;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.example.demo.mapper.EvaluationMapper;
import com.example.demo.mapper.MainUserMapper;
import com.example.demo.mapper.ThreateUserMapper;
import com.example.demo.pojo.entity.EvaluationOrder;
import com.example.demo.pojo.entity.TClientUser;
import com.example.demo.pojo.entity.ThreateUser;
import com.example.demo.pojo.entity.WfUser;
import com.example.demo.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sun.awt.windows.WFontConfiguration;

import java.nio.ByteBuffer;
import java.security.acl.LastOwnerException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Autowired
    private ThreateUserMapper threateUserMapper;
    @Autowired
    private MainUserMapper mainUserMapper;


    @Test
    public void contextLoads() throws SQLException, InterruptedException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setKeepAlive(true);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxActive(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setUrl("jdbc:mysql://woof.ctrvsh6q54n2.rds.cn-north-1.amazonaws.com.cn:3306/woof_js_prd?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
        druidDataSource.setUsername("woofjs");
        druidDataSource.setPassword("brysjhhrhl");
        Db mysql = Db.use(druidDataSource);
        int total = mysql.count(new Entity("t_client_user"));
        log.info("迁移的总条数{}",total);
        int ceil =(int) Math.ceil((double)total / 1000);
        List<WfUser> wfUsers = mainUserMapper.selectListUserId();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "16");
        for(int i=0;i<ceil;i++){
            log.info("添加第i次{}",i);
            List<TClientUser> tClientUser = mysql.query("select id,guid,uuid,wechat_union_id,wechat_open_id,gender,language,city,province,country,headImg,nick_name,phone_numer,deleted,disabled,locked,locked_expired_at,user_reference_code,inviter_user_reference_code,created_at,updated_at from t_client_user " +
                    "limit 1000 offset "+i*1000+"" , TClientUser.class);

            //获取差值 用户信息的
            List<TClientUser> collect = tClientUser.stream().filter(new Predicate<TClientUser>() {
                @Override
                public boolean test(TClientUser tClientUser) {
                    return wfUsers.stream().parallel().noneMatch(wfUser1 -> tClientUser.getWechatUnionId().equals(wfUser1.getUnionId()));
                }
            }).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(collect)){
                List<ThreateUser> listTh=new ArrayList<ThreateUser>();
                List<WfUser> listMain = new ArrayList<>();
                collect.stream().forEach(new Consumer<TClientUser>() {
                    @Override
                    public void accept(TClientUser tClientUser) {
                        Long aLong=SnowflakeIdWorker.generateId();
                        ThreateUser entity = convertTheatreEntity(tClientUser, aLong);
                        listTh.add(entity);
                        WfUser wfUser = convertMainEntity(tClientUser, aLong);
                        listMain.add(wfUser);
                    }
                });
                if(CollectionUtils.isNotEmpty(listMain)&&CollectionUtils.isNotEmpty(listTh)){
                    threateUserMapper.insertBatch(listTh);
                    mainUserMapper.insertBatch(listMain);
                }
            }

            //获取交集
            List<ThreateUser> list = new ArrayList<>();
            for (TClientUser tc:tClientUser) {
                wfUsers.stream().parallel().forEach(new Consumer<WfUser>() {
                    @Override
                    public void accept(WfUser wfUser) {
                        boolean equals = tc.getWechatUnionId().equals(wfUser.getUnionId());
                        if(equals){
                            ThreateUser threateUser = convertTheatreEntity(tc, wfUser.getUserId());
                            list.add(threateUser);
                        }
                    }
                });
            }

            if(list.size()>0){
                //批量更新电话号码
                mainUserMapper.updateBatchMainUser(list);
                threateUserMapper.batchInsertUpdate(list);
            }
            log.info("数据添加完成------------>{}",i);
        }
    }

    public ThreateUser convertTheatreEntity(TClientUser tClientUser,Long aLong){
        ThreateUser threateUser = new ThreateUser();
        threateUser.setUserId(aLong);
        threateUser.setNickName(tClientUser.getNickName());
        threateUser.setUnionId(tClientUser.getWechatUnionId());
        threateUser.setOpenId(tClientUser.getWechatOpenId());
        threateUser.setUpdateAt(tClientUser.getUpdatedAt());
        threateUser.setCreateAt(tClientUser.getCreatedAt());
        threateUser.setLocked(0);
        threateUser.setOrinChannel(15);
        if(tClientUser.getGender()==null){
            threateUser.setSex(3);
        }else if(tClientUser.getGender()==1){
            threateUser.setSex(1);
        }else if(tClientUser.getGender()==0){
            threateUser.setSex(2);
        }else {
            threateUser.setSex(3);
        }
        threateUser.setDisabled(tClientUser.getDisabled());
        threateUser.setLockedExpiredAt(tClientUser.getLockedExpiredAt());
        threateUser.setUserReferenceCode(tClientUser.getUserReferenceCode());
        threateUser.setInviterUserReferenceCode(tClientUser.getInviterUserReferenceCode());
        threateUser.setGuid(tClientUser.getGuid());
        threateUser.setLanguage(tClientUser.getLanguage());
        threateUser.setCity(tClientUser.getCity());
        threateUser.setProvince(tClientUser.getProvince());
        threateUser.setCountry(tClientUser.getCountry());
        threateUser.setPhone(tClientUser.getPhoneNumer());
        threateUser.setBrightGuid(convertByteToString((byte[]) tClientUser.getGuid()));
        threateUser.setAvatar(tClientUser.getHeadImg());
        threateUser.setUuid(tClientUser.getUuid());
        return threateUser;
    }

    public WfUser convertMainEntity(TClientUser tClientUser,Long aLong){
        WfUser wfUser = new WfUser();
        wfUser.setUserId(aLong);
        wfUser.setUnionId(tClientUser.getWechatUnionId());
        wfUser.setPhone(tClientUser.getPhoneNumer());
        wfUser.setUpdateAt(tClientUser.getUpdatedAt());
        wfUser.setCreateAt(tClientUser.getCreatedAt());
        wfUser.setLevel(10);
        wfUser.setLocked(0);
        wfUser.setDeleted(0);
        wfUser.setOrinChannel(15);
        wfUser.setIsOldUser(1);
        return wfUser;
    }

    public  String convertByteToString(byte[] GUIDBytes) {
        if (GUIDBytes != null) {
            ByteBuffer bb = ByteBuffer.wrap(GUIDBytes);
            UUID u = new UUID(bb.getLong(), bb.getLong());
            return StringUtils.deleteWhitespace(u.toString());
        } else{
            return null;
        }
    }

    @Test
    public void increment() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setKeepAlive(true);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxActive(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setUrl("jdbc:mysql://woof.ctrvsh6q54n2.rds.cn-north-1.amazonaws.com.cn:3306/woof_js_prd?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
        druidDataSource.setUsername("woofjs");
        druidDataSource.setPassword("brysjhhrhl");
        Db mysql = Db.use(druidDataSource);
        List<WfUser> wfUsers = mainUserMapper.selectListUserId();
//        Number number = mysql.queryNumber("select count(*) from t_client_user where created_at >?;", "2019-6-27 00:00:00");
//        int ceil =(int) Math.ceil(number.doubleValue() / 1000);
//        for(int i=0;i<ceil;i++){
//            List<TClientUser> tClientUser = mysql.query("select id,guid,uuid,wechat_union_id,wechat_open_id,gender,language,city,province,country,headImg,nick_name,phone_numer,deleted,disabled,locked,locked_expired_at,user_reference_code,inviter_user_reference_code,created_at,updated_at from t_client_user where created_at >\"2019-6-27 00:00:00\"" +
//                    "limit 1000 offset "+i*1000+"" ,TClientUser.class);
            List<TClientUser> tClientUser = mysql.query("select id,guid,uuid,wechat_union_id,wechat_open_id,gender,language,city,province,country,headImg,nick_name,phone_numer,deleted,disabled,locked,locked_expired_at,user_reference_code,inviter_user_reference_code,created_at,updated_at from t_client_user where created_at >\"2019-7-02 00:00:00\"" +
                    "limit 1000 offset 0" ,TClientUser.class);
            //获取差值 用户信息的
            List<TClientUser> collect = tClientUser.stream().filter(new Predicate<TClientUser>() {
                @Override
                public boolean test(TClientUser tClientUser) {
                    return wfUsers.stream().noneMatch(wfUser1 -> tClientUser.getWechatUnionId().equals(wfUser1.getUnionId()));
                }
            }).collect(Collectors.toList());
            if(collect.size()!=0){
                List<ThreateUser> listTh=new ArrayList<ThreateUser>();
                List<WfUser> listMain = new ArrayList<>();
                for (TClientUser tc : collect) {
                    Long aLong=SnowflakeIdWorker.generateId();
                    ThreateUser entity = convertTheatreEntity(tc, aLong);
                    listTh.add(entity);
                    WfUser wfUser = convertMainEntity(tc, aLong);
                    listMain.add(wfUser);
                }
                collect.clear();
                if(listTh.size()!=0&&listMain.size()!=0){
                    threateUserMapper.insertBatch(listTh);
                    mainUserMapper.insertBatch(listMain);
                }
                listMain.clear();
                listTh.clear();
            }

            //获取交集
            List<ThreateUser> list = new ArrayList<>();
            for (TClientUser tc:tClientUser) {
                wfUsers.stream().forEach(new Consumer<WfUser>() {
                    @Override
                    public void accept(WfUser wfUser) {
                        boolean equals = tc.getWechatUnionId().equals(wfUser.getUnionId());
                        if(equals){
                            ThreateUser threateUser = convertTheatreEntity(tc, wfUser.getUserId());
                            list.add(threateUser);
                        }
                    }
                });
            }

            if(list.size()>0){
                //批量更新电话号码
                mainUserMapper.updateBatchMainUser(list);
                threateUserMapper.batchInsertUpdate(list);
                list.clear();
            }
            log.info("数据增量更新完成{}");
//        }
    }

    @Test
//    @Transactional(rollbackFor = Exception.class)
    public void kjkjk() throws SQLException {
        List<WfUser> wfUsers = mainUserMapper.selectUserList();
        for (WfUser wf:wfUsers) {
            WfUser wfUser = mainUserMapper.selectOneUser(wf);
            if(StringUtils.isNotBlank(wfUser.getTheatreUnionId())){
                log.info(wfUser.getTheatreUnionId());
                //删除一条信息，把user_id
                int i = mainUserMapper.deleteByOrinChannel(wf.getUserId());
                //保留一条更新user_id
                int i1 = mainUserMapper.updateUserIdUnionId(wf);
                //更新发售剧场的user_id
                int i2 = threateUserMapper.updateByUnionId(wf);
            }
        }
    }
}
