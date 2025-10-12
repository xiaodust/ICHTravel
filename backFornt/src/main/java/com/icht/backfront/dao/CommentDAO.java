package com.icht.backfront.dao;

import com.icht.backfront.dataobject.CommentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDAO {
    int insert(CommentDO commentDO);

    int delete(String id);

    CommentDO selectById(String id);

    List<CommentDO> selectByNoteId(int noteId);

    CommentDO update(CommentDO commentDO);

}
