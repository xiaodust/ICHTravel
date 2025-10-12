package com.icht.backfront.service.impl;

import com.icht.backfront.dao.NoteDAO;
import com.icht.backfront.dataobject.NoteDO;
import com.icht.backfront.dataobject.ProductDO;
import com.icht.backfront.model.Note;
import com.icht.backfront.model.Paging;
import com.icht.backfront.model.Product;
import com.icht.backfront.param.BasePageParam;
import com.icht.backfront.service.NoteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteDAO noteDAO;
    @Override
    public int save(Note note) {
        if (StringUtils.isBlank(note.getId())){
            note.setId(UUID.randomUUID().toString());
            return noteDAO.insert(new NoteDO(note));
        }
        return noteDAO.insert(new NoteDO(note));
    }

    @Override
    public int delete(String id) {
        if (StringUtils.isBlank(id)){
            return 0;
        }
        NoteDO noteDO = noteDAO.selectById(id);
        if (noteDO == null){
            return 0;
        }
        return noteDAO.delete(noteDO.getId());
    }

    @Override
    public Paging<Note> pageQueryNote(BasePageParam basePageParam) {
        Paging<Note> result = new Paging<>();

        if (basePageParam.getPagination()<0){
            basePageParam.setPagination(0);
        }

        if (basePageParam.getPageSize()<0){
            basePageParam.setPageSize(10);
        }

        int count =noteDAO.selectAllCounts();
        if (count<0){
            return  result;
        }
        int totalPage = (int)Math.ceil(count / (basePageParam.getPageSize() * 1.0));
        result.setTotalPage(totalPage);
        result.setTotalCount(count);
        result.setPageNum(basePageParam.getPagination());
        result.setPageSize(basePageParam.getPageSize());

        List<NoteDO> noteDOS=noteDAO.pageQuery(basePageParam);
        List<Note> noteList=new ArrayList<>();
        for (NoteDO noteDO:noteDOS){
           Note note = noteDO.ToModel();
           noteList.add(note);
        }
        result.setData(noteList);

        return result;
    }

    @Override
    public List<Note> getByUserId(String userId) {
        if (StringUtils.isBlank(userId)){
            return null;
        }
         List<NoteDO> noteDOS = noteDAO.selectByUserId(userId);
        if (noteDOS.get(0) == null){
            return null;
        }
        List<Note> noteList=new ArrayList<>();
        for (NoteDO noteDO:noteDOS){
            Note note = noteDO.ToModel();
            noteList.add(note);
        }
        return noteList;
    }

    @Override
    public Note getById(String id) {
        if (StringUtils.isBlank(id)){
            return null;
        }
        NoteDO noteDO = noteDAO.selectById(id);
        if (noteDO == null){
            return null;
        }
        Note note = noteDO.ToModel();
        return note;
    }

    @Override
    public Note likeNote(String id) {
       if (StringUtils.isBlank(id)){
            return null;
       }
       NoteDO noteDO = noteDAO.selectById(id);
       if (noteDO == null){
           return null;
       }
       noteDO.setLiked(noteDO.getLiked() + 1);
      return noteDAO.update(noteDO).ToModel();
    }

    @Override
    public Note unlikeNote(String id) {
        if (StringUtils.isBlank(id)){
            return null;
        }
        NoteDO noteDO = noteDAO.selectById(id);
        if (noteDO == null){
            return null;
        }
        noteDO.setLiked(noteDO.getLiked() - 1);
        return noteDAO.update(noteDO).ToModel();
    }

}
