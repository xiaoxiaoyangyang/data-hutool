package com.example.demo.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.example.demo.pojo.entity.ThreateUser;
import com.example.demo.pojo.entity.WfUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: guozhiyang
 * @Date: 2019/6/18 10:31
 */
@Mapper
public interface ThreateUserMapper extends BaseMapper<ThreateUser> {
    int insertBatch(List<ThreateUser> list);

    int batchInsertUpdate(List<ThreateUser> list);

    int updateByUnionId(WfUser wf);
}
