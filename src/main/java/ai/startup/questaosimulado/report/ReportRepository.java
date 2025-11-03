package ai.startup.questaosimulado.report;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByUsuarioId(String usuarioId);
    List<Report> findByQuestaoId(String questaoId);
    List<Report> findByStatus(String status);
}

