package com.icht.backfront.api;

import com.icht.backfront.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/note")
public class NoteApi {
    @Autowired
    private NoteService noteService;


}
