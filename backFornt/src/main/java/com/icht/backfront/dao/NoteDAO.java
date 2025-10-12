package com.icht.backfront.dao;

import com.icht.backfront.dataobject.NoteDO;
import com.icht.backfront.param.BasePageParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoteDAO {
    int insert(NoteDO noteDO);

    int delete(String id);

    NoteDO update(NoteDO noteDO);

    NoteDO selectById(String id);

    List<NoteDO> selectAll();

    List<NoteDO> pageQuery(BasePageParam basePageParam);

    List<NoteDO> selectByUserId(String userId);

    int selectAllCounts();
}
