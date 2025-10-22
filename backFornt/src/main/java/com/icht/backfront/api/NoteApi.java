package com.icht.backfront.api;

import com.icht.backfront.model.Note;
import com.icht.backfront.model.Paging;
import com.icht.backfront.model.Result;
import com.icht.backfront.param.BasePageParam;
import com.icht.backfront.service.FileStorageService;
import com.icht.backfront.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/note")
public class NoteApi {
    @Autowired
    private NoteService noteService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/page")
    @ResponseBody
    public Result<Paging<Note>> page(BasePageParam basePageParam) {
        Result<Paging<Note>> result = new Result<>();
        result.setSuccess(true);
        result.setCode("200");
        result.setMessage("查询成功");
        result.setData(noteService.pageQueryNote(basePageParam));
        return result;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result<Note> get(@PathVariable("id") String id) {
        Result<Note> result = new Result<>();
        result.setSuccess(true);
        result.setData(noteService.getById(id));
        result.setCode("200");
        result.setMessage("查询成功");
        return result;
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    public Result<List<Note>> getByUserId(@PathVariable("userId") String userId) {
        Result<List<Note>> result = new Result<>();
        result.setSuccess(true);
        result.setData(noteService.getByUserId(userId));
        result.setCode("200");
        result.setMessage("查询成功");
        return result;
    }

    @PostMapping("/like")
    @ResponseBody
    public Result<Note> like(@RequestParam String id) {
        Result<Note> result = new Result<>();
        result.setSuccess(true);
        result.setData(noteService.likeNote(id));
        result.setCode("200");
        result.setMessage("点赞成功");
        return result;
    }

    @PostMapping("/unlike")
    @ResponseBody
    public Result<Note> unlike(@RequestParam String id) {
        Result<Note> result = new Result<>();
        result.setSuccess(true);
        result.setData(noteService.unlikeNote(id));
        result.setCode("200");
        result.setMessage("取消点赞成功");
        return result;
    }

    @PostMapping("/add")
    @ResponseBody
    public Result add(@RequestBody Note note) {
        Result result = new Result<>();
        result.setSuccess(true);
        if (note==null){
            result.setSuccess(false);
            result.setCode("601");
            result.setMessage("note is null");
            return result;
        }
        result.setCode("200");
        result.setData(noteService.add(note));
        result.setMessage("添加成功");
        return result;
    }

}
