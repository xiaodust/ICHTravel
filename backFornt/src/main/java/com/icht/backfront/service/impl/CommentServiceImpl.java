package com.icht.backfront.service.impl;

import com.icht.backfront.dao.CommentDAO;
import com.icht.backfront.dao.UserDAO;
import com.icht.backfront.dataobject.CommentDO;
import com.icht.backfront.dataobject.UserDO;
import com.icht.backfront.model.Comment;
import com.icht.backfront.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDAO commentDAO;
    @Autowired
    private UserDAO userDAO;
    @Override
    public int save(Comment comment) {
        comment.setGmtCreated(LocalDateTime.now());
        comment.setGmtModified(LocalDateTime.now());
        if (StringUtils.isBlank(comment.getUserName()) && StringUtils.isNotBlank(comment.getUserId())) {
            UserDO userDO = userDAO.findById(comment.getUserId());
            if (userDO != null) {
                String nick = userDO.getNickName();
                comment.setUserName(StringUtils.isNotBlank(nick) ? nick : userDO.getName());
            }
        }
       if(StringUtils.isBlank(comment.getId())){
           comment.setId(UUID.randomUUID().toString());
       }
       comment.setGmtCreated(LocalDateTime.now());
       comment.setGmtModified(LocalDateTime.now());
       return commentDAO.insert(new CommentDO(comment));
    }

    @Override
    public int delete(String id) {
        if(StringUtils.isBlank(id)){
            return 0;
        }
        CommentDO commentDO = commentDAO.selectById(id);
        if(commentDO == null){
            return 0;
        }
        return commentDAO.delete(commentDO.getId());
    }

    @Override
    public List<Comment> findByNoteId(String noteId) {
        if(StringUtils.isBlank(noteId)){
            return new ArrayList<>();
        }
        List<CommentDO> commentDOList = commentDAO.selectByNoteId(noteId);
        List<Comment> comments = new ArrayList<>();
        if (commentDOList == null || commentDOList.isEmpty()) {
            return comments;
        }
        for(CommentDO commentDO : commentDOList){
           Comment comment = commentDO.ToModel();
           comments.add(comment);
        }
        return comments;
    }

    @Override
    public Comment likeComment(String id) {
       if(StringUtils.isBlank(id)){
           return null;
       }
       CommentDO commentDO = commentDAO.selectById(id);
       if(commentDO == null){
           return null;
       }
       commentDO.setLiked(commentDO.getLiked() + 1);
        return commentDAO.update(commentDO).ToModel();
    }

    @Override
    public Comment unlikeComment(String id) {
        if(StringUtils.isBlank(id)){
            return null;
        }
        CommentDO commentDO = commentDAO.selectById(id);
        if(commentDO == null){
            return null;
        }
        commentDO.setLiked(commentDO.getLiked() - 1);
        return commentDAO.update(commentDO).ToModel();
    }
}
