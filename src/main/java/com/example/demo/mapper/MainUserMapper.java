package com.example.demo.mapper;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.demo.pojo.entity.TClientUser;
import com.example.demo.pojo.entity.ThreateUser;
import com.example.demo.pojo.entity.WfUser;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: guozhiyang
 * @Date: 2019/6/18 10:55
 */
@Mapper
public interface MainUserMapper extends BaseMapper<WfUser> {

    int insertBatch(List<WfUser> list);

    int updateMainUser(TClientUser tClient);

    int updateBatchMainUser(List<ThreateUser> list);

    List<WfUser> selectListUserId();

    List<WfUser> selectUserList();


    int updateUserIdUnionId(WfUser wf);

    int deleteByOrinChannel(Long userId);

    WfUser selectOneUser(WfUser wf);
}
