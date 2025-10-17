package com.icht.backfront.api;

import com.icht.backfront.model.Comment;
import com.icht.backfront.model.Result;
import com.icht.backfront.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/comment")
public class CommentApi {
    @Autowired
    private CommentService commentService;

    @GetMapping("/{noteId}")
    @ResponseBody
    public Result<List<Comment>> getComment(@PathVariable String noteId) {
        Result<List<Comment>> result = new Result<>();
        result.setSuccess(true);
        result.setData(commentService.findByNoteId(noteId));
        result.setMessage("获取成功");
        result.setCode("200");
        return result;
    }

    @PostMapping("/addcomment")
    @ResponseBody
    public Result addComment(@RequestBody Comment comment) {
        Result result = new Result<>();
        result.setSuccess(true);
        result.setData(commentService.save(comment));
        result.setMessage("添加成功");
        result.setCode("200");
        return result;
    }

    @PostMapping("/like")
    @ResponseBody
    public Result likeComment(@RequestParam String id) {
        Result result = new Result<>();
        result.setSuccess(true);
        result.setData(commentService.likeComment(id));
        result.setCode("200");
        result.setMessage("点赞成功");
        return result;

    }

    @PostMapping("/unlike")
    @ResponseBody
    public Result unlikeComment(@RequestParam String id) {
        Result result = new Result<>();
        result.setSuccess(true);
        result.setData(commentService.unlikeComment(id));
        result.setCode("200");
        result.setMessage("取消点赞成功");
        return result;
    }
}
