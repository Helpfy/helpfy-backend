package com.example.helpfy.services.question;

import com.example.helpfy.exceptions.Constants;
import com.example.helpfy.exceptions.NotFoundException;
import com.example.helpfy.models.Question;
import com.example.helpfy.models.User;
import com.example.helpfy.repositories.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question getQuestionById(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        return optionalQuestion.orElseThrow(() -> new NotFoundException(Constants.QUESTION_NOT_FOUND));
    }

    @Transactional
    @Override
    public Question saveQuestion(Question question, User user) {
        question.setAuthor(user);
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long id, Question newQuestion) {
        var question = getQuestionById(id);

        var updatedQuestion = updateAllInformationQuestion(newQuestion, question);

        return questionRepository.saveAndFlush(updatedQuestion);
    }

    private Question updateAllInformationQuestion(Question newQuestion, Question question) {
        if (newQuestion.getTitle() != null) {
            question.setTitle(newQuestion.getTitle());
        }
        if (newQuestion.getBody() != null) {
            question.setBody(newQuestion.getBody());
        }
        if (newQuestion.getTags() != null) {
            question.setTags(newQuestion.getTags());
        }
        question.setAnswered(newQuestion.isAnswered());

        return question;
    }

    @Override
    public void deleteQuestion(Long id) {
        var question = getQuestionById(id);
        questionRepository.delete(question);
    }
}
