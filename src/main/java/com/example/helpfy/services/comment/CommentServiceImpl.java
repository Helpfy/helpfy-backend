package com.example.helpfy.services.comment;

import com.example.helpfy.exceptions.Constants;
import com.example.helpfy.exceptions.NotFoundException;
import com.example.helpfy.models.Answer;
import com.example.helpfy.models.Comment;
import com.example.helpfy.models.Question;
import com.example.helpfy.models.User;
import com.example.helpfy.repositories.AnswerRepository;
import com.example.helpfy.repositories.CommentRepository;
import com.example.helpfy.repositories.QuestionRepository;
import com.example.helpfy.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public Comment addCommentAnswer(Comment comment, Long userId, Long answerId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(Constants.USER_NOT_FOUND);
        });

        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> {
            throw new NotFoundException(Constants.ANSWER_NOT_FOUND);
        });

        comment.setAuthor(user);
        commentRepository.save(comment);
        answer.getComments().add(comment);
        answerRepository.save(answer);
        return comment;
    }

    @Override
    public Comment getCommentAnswer(Long commentId, Long answerId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException(Constants.COMMENT_NOT_FOUND);
        });
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> {
            throw new NotFoundException(Constants.ANSWER_NOT_FOUND);
        });

        if (!answer.getComments().contains(comment)){
            throw new NotFoundException(Constants.COMMENT_NOT_FOUND);
        }

        return comment;
    }

    public Comment addCommentQuestion(Comment comment, Long userId, Long questionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException(Constants.USER_NOT_FOUND);
        });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new NotFoundException(Constants.QUESTION_NOT_FOUND);
        });

        comment.setAuthor(user);
        commentRepository.save(comment);
        question.getComments().add(comment);
        questionRepository.save(question);

        return comment;
    }

    @Override
    public Comment getCommentQuestion(Long commentId, Long questionId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new NotFoundException(Constants.COMMENT_NOT_FOUND);
        });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new NotFoundException(Constants.ANSWER_NOT_FOUND);
        });

        if (!question.getComments().contains(comment)){
            throw new NotFoundException(Constants.COMMENT_NOT_FOUND);
        }

        return comment;
    }

}
