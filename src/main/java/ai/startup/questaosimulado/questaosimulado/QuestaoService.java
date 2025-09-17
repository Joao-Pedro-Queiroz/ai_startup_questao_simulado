package ai.startup.questaosimulado.questaosimulado;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestaoService {

    private final QuestaoRepository repo;

    public QuestaoService(QuestaoRepository repo) {
        this.repo = repo;
    }

    // Cadastro em massa
    public List<QuestaoDTO> criarEmLote(List<QuestaoCreateDTO> itens) {
        var entidades = itens.stream().map(this::fromCreate).toList();
        return repo.saveAll(entidades).stream().map(this::toDTO).toList();
    }

    public List<QuestaoDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public List<QuestaoDTO> listarPorSimulado(String idSimulado) {
        return repo.findByIdFormulario(idSimulado).stream().map(this::toDTO).toList();
    }

    public List<QuestaoDTO> listarPorUsuario(String idUsuario) {
        return repo.findByIdUsuario(idUsuario).stream().map(this::toDTO).toList();
    }

    public QuestaoDTO obter(String id) {
        return repo.findById(id).map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Questão não encontrada."));
    }

    public QuestaoDTO atualizar(String id, QuestaoUpdateDTO dto) {
        var q = repo.findById(id).orElseThrow(() -> new RuntimeException("Questão não encontrada."));

        if (dto.id_formulario()  != null) q.setIdFormulario(dto.id_formulario());
        if (dto.id_usuario()     != null) q.setIdUsuario(dto.id_usuario());
        if (dto.topic()          != null) q.setTopic(dto.topic());
        if (dto.subskill()       != null) q.setSubskill(dto.subskill());
        if (dto.difficulty()     != null) q.setDifficulty(dto.difficulty());
        if (dto.question()       != null) q.setQuestion(dto.question());
        if (dto.options()        != null) q.setOptions(dto.options());
        if (dto.correct_option() != null) q.setCorrectOption(dto.correct_option());
        if (dto.solution()       != null) q.setSolution(dto.solution());
        if (dto.structure()      != null) q.setStructure(dto.structure());
        if (dto.format()         != null) q.setFormat(dto.format());
        if (dto.representation() != null) q.setRepresentation(dto.representation());
        if (dto.hint()           != null) q.setHint(dto.hint());
        if (dto.target_mistakes()!= null) q.setTargetMistakes(dto.target_mistakes());
        if (dto.source()         != null) q.setSource(dto.source());

        // ---- novos (parcial) ----
        if (dto.alternativa_marcada() != null) q.setAlternativaMarcada(dto.alternativa_marcada());
        if (dto.dica()                != null) q.setDica(dto.dica());
        if (dto.solucao()             != null) q.setSolucao(dto.solucao());
        if (dto.modulo()              != null) q.setModulo(dto.modulo());

        return toDTO(repo.save(q));
    }

    public void deletar(String id) {
        if (!repo.existsById(id)) throw new RuntimeException("Questão não encontrada.");
        repo.deleteById(id);
    }

    // mappers
    private Questao fromCreate(QuestaoCreateDTO d) {
        return Questao.builder()
                .idFormulario(d.id_formulario())
                .idUsuario(d.id_usuario())
                .topic(d.topic())
                .subskill(d.subskill())
                .difficulty(d.difficulty())
                .question(d.question())
                .options(d.options())
                .correctOption(d.correct_option())
                .solution(d.solution())
                .structure(d.structure())
                .format(d.format())
                .representation(d.representation())
                .hint(d.hint())
                .targetMistakes(d.target_mistakes())
                .source(d.source())
                // novos
                .alternativaMarcada(d.alternativa_marcada())
                .dica(d.dica())
                .solucao(d.solucao())
                .modulo(d.modulo())
                .build();
    }

    private QuestaoDTO toDTO(Questao q) {
        return new QuestaoDTO(
                q.getId(),
                q.getIdFormulario(),
                q.getIdUsuario(),
                q.getTopic(),
                q.getSubskill(),
                q.getDifficulty(),
                q.getQuestion(),
                q.getOptions(),
                q.getCorrectOption(),
                q.getSolution(),
                q.getStructure(),
                q.getFormat(),
                q.getRepresentation(),
                q.getHint(),
                q.getTargetMistakes(),
                q.getSource(),
                // novos
                q.getAlternativaMarcada(),
                q.getDica(),
                q.getSolucao(),
                q.getModulo()
        );
    }
}