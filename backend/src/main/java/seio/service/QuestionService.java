package seio.service;

import org.springframework.stereotype.Service;
import seio.model.Question;
import seio.repository.QuestionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question save(Question question) {
        return QuestionRepository.save(question);
    }

    public List<Question> findAll() {
        return QuestionRepository.findAll();
    }

    public Optional<Question> findById(Long id) {
        return QuestionRepository.findById(id);
    }
}
