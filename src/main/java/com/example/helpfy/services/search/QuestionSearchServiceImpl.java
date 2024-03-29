package com.example.helpfy.services.search;

import com.example.helpfy.dtos.SearchQuestionsDTO;
import com.example.helpfy.exceptions.BadRequestException;
import com.example.helpfy.models.Question;
import com.example.helpfy.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionSearchServiceImpl implements QuestionSearchService {
    public static final String VOTE = "vote";
    public static final String RELEVANT = "relevant";
    public static final String ANSWERED = "answered";
    public static final String NEW = "new";

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public SearchQuestionsDTO search(String title, Set<String> tags, String filter, Pageable pageable) {
        Page<Question> questions = new PageImpl<>(new ArrayList<>());
        if (!title.isEmpty()){
            questions = questionRepository.findBySimilarity(title, pageable);
        } else {
            if (NEW.equals(filter)) {
                questions = questionRepository.findAllByOrderByCreatedAtDesc(pageable);
            } else if (VOTE.equals(filter)) {
                questions = questionRepository.findAllOrderByVote(pageable);
            } else if (RELEVANT.equals(filter)) {
                questions = questionRepository.findAllOrderByRelevance(pageable);
            } else if (ANSWERED.equals(filter)) {
                questions = questionRepository.findByAnsweredTrue(pageable);
            }
        }

        return new SearchQuestionsDTO(questions.getContent(), questions.getTotalElements());
    }

    private Page<Question> updateQuestionsByFilter(Page<Question> pageQuestions, String filter, Pageable pageable) {
        List<Question> questions = pageQuestions.getContent();
        Set<String> validFilters = Set.of(VOTE, RELEVANT, ANSWERED, NEW);
        if (!validFilters.contains(filter)) {
            throw new BadRequestException("Invalid filter.");
        }

        List<Question> newQuestions = new ArrayList<>(questions);
        if (VOTE.equals(filter)) newQuestions = updateQuestionsByVote(questions);
        else if (RELEVANT.equals(filter)) newQuestions = updateQuestionsByRelevance(questions);
        else if (ANSWERED.equals(filter)) newQuestions = updateQuestionsByAnswers(questions);
        else if (NEW.equals(filter)) newQuestions = updateQuestionsByDate(questions);

        return new PageImpl<>(questions, pageable, pageQuestions.getTotalElements());
    }

    private List<Question> updateQuestionsByVote(List<Question> questions) {
        Comparator<Question> byLike = (Question q1, Question q2) -> q2.getIdsFromUsersLikes().size() - q1.getIdsFromUsersLikes().size();

        List<Question> newQuestions = new ArrayList<>(questions);
        newQuestions.sort(byLike);

        return newQuestions;
    }

    private List<Question> updateQuestionsByRelevance(List<Question> questions) {
        Comparator<Question> byRelevance = (Question q1, Question q2) -> {
            return (q2.getIdsFromUsersLikes().size() - q2.getIdsFromUsersDislikes().size()) - (q1.getIdsFromUsersLikes().size() - q1.getIdsFromUsersDislikes().size());
        };

        List<Question> newQuestions = new ArrayList<>(questions);
        newQuestions.sort(byRelevance);

        return newQuestions;
    }

    private List<Question> updateQuestionsByAnswers(List<Question> questions) {
        return questions.stream().filter(Question::isAnswered).collect(Collectors.toList());
    }

    private List<Question> updateQuestionsByDate(List<Question> questions) {
        Comparator<Question> byDate = (Question q1, Question q2) -> {
            return q2.getCreatedAt().compareTo(q1.getCreatedAt());
        };

        List<Question> newQuestions = new ArrayList<>(questions);
        newQuestions.sort(byDate);

        return newQuestions;
    }
}
