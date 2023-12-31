package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.handler.NotFoundException;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto saveComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с идентификатором : " + userId + " не найден"));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с идентификатором : " + userId + " не найдено"));
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(user, event, newCommentDto)));
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto commentDto) {
        //А комментарий может отредактировать кто угодно, даже не автор? Будет логично проверять авторство оригинального комментария
        checkUser(userId);
        final Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден"));
        comment.setText(commentDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateCommentByAdmin(Long commentId, NewCommentDto commentDto) {
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден"));

        comment.setText(commentDto.getText());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }


    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        checkUser(userId);
        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден")));
    }

    @Override
    public List<CommentDto> getCommentsUser(Long userId, PageRequest page) {
        checkUser(userId);
        return commentRepository.findAllByAuthorId(userId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsEvent(Long userId, Long eventId, PageRequest page) {
        checkUser(userId);
        if (!eventRepository.existsById(eventId))
            throw new NotFoundException("Событие с идентификатором : " + eventId + " не найдено");
        return commentRepository.findAllByEventId(eventId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundException("Вы не являетесь автором");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId))
            throw new NotFoundException("Комментарий с идентификатором : " + commentId + " не найден");
        commentRepository.deleteById(commentId);
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с идентификатором : " + userId + " не найден");
        }
    }

}
