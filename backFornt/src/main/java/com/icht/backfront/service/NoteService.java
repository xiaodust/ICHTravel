package com.icht.backfront.service;

import com.icht.backfront.model.Note;
import com.icht.backfront.model.Paging;
import com.icht.backfront.param.BasePageParam;

import java.util.List;

public interface NoteService {
    public int save(Note note);

    public int delete(String id);

    public Paging<Note> pageQueryNote(BasePageParam basePageParam);

    public List<Note> getByUserId(String userId);
}
