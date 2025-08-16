package seio.repository;

import org.springframework.data.jpa.repository.Query;
import seio.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Question save(Question question);

    @Query("SELECT q FROM Question q")
    List<Question> findAll();

    @Query("SELECT q FROM Question q WHERE q.id = :id")
    Optional<Question> findById(Long id);
}
