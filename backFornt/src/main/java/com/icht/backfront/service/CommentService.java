package com.icht.backfront.service;

import com.icht.backfront.model.Comment;

import java.util.List;

public interface CommentService {
    public int save(Comment comment);

    public int delete(String id);

    public List<Comment> findByNoteId(String noteId);
}
