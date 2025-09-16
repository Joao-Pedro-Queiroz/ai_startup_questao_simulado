package ai.startup.questaosimulado.questaosimulado;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestaoRepository extends MongoRepository<Questao, String> {
    List<Questao> findByIdFormulario(String idFormulario);
    List<Questao> findByIdUsuario(String idUsuario);
}