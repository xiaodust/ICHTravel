package com.icht.backfront.api;

import com.icht.backfront.model.Comment;
import com.icht.backfront.model.Result;
import com.icht.backfront.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/comment")
public class CommentApi {
    @Autowired
    private CommentService commentService;

    @GetMapping("/{noteId}")
    public Result<List<Comment>> getComment(@PathVariable String noteId) {
        Result<List<Comment>> result = new Result<>();
        result.setSuccess(true);
        result.setData(commentService.findByNoteId(noteId));
        return result;
    }
}
