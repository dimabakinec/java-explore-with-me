package ru.practicum.comments.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {


    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByAdmin(@PathVariable Long commentId, @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.updateCommentByAdmin(commentId, newCommentDto);
    }


    @DeleteMapping("/{commentId}")
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }


}