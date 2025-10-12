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
        return result;
    }

    @PostMapping("/addcomment")
    @ResponseBody
    public Result addComment(@RequestBody Comment comment) {
        Result result = new Result<>();
        result.setSuccess(true);
        result.setData(commentService.save(comment));
        return result;
    }

    @PostMapping("/like")
    @ResponseBody
    public Result likeComment(@RequestParam String id) {
        Result result = new Result<>();
        result.setSuccess(true);
        result.setData(commentService.likeComment(id));
        return result;

    }

    @PostMapping("/unlike")
    @ResponseBody
    public Result unlikeComment(@RequestParam String id) {
        Result result = new Result<>();
        result.setSuccess(true);
        result.setData(commentService.unlikeComment(id));
        return result;
    }
}
