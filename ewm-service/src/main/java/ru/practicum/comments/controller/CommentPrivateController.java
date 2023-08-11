package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;
    private final Sort sort = Sort.by("created").descending();

    @PostMapping
    public CommentDto saveComment(@PathVariable Long userId,
                                  @RequestParam Long eventId,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.debug("Пользователь " + userId + " добавил новый комментарий к событию: {}", eventId);
        return commentService.saveComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody NewCommentDto commentDto) {
        log.debug("Пользователь " + userId + " обновил комментарий: {}", commentId);
        return commentService.updateComment(userId, commentId, commentDto);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long userId,
                                 @PathVariable Long commentId) {
        log.debug("Пользователь " + userId + " получил комментарий: {}", commentId);
        return commentService.getCommentById(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getCommentsUser(@PathVariable Long userId,
                                            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from, size, sort);
        log.debug("Пользователь " + userId + " получил все свои комментарий");
        return commentService.getCommentsUser(userId, page);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getCommentsEvent(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from, size, sort);
        log.debug("Получены все комментарии к событию: {}", eventId);
        return commentService.getCommentsEvent(userId, eventId, page);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.debug("Пользователь " + userId + " удалил комментарий: {}", commentId);
        commentService.deleteComment(userId, commentId);
    }
}

