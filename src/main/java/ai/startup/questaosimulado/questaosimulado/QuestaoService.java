package ai.startup.questaosimulado.questaosimulado;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class QuestaoService {

    private static final Logger log = Logger.getLogger(QuestaoService.class.getName());
    private final QuestaoRepository repo;

    public QuestaoService(QuestaoRepository repo) {
        this.repo = repo;
    }

    // ===== CRUD =====
    public List<QuestaoDTO> listar() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "idFormulario", "modulo"))
                .stream().map(this::toDTO).toList();
    }

    public QuestaoDTO obter(String id) {
        return repo.findById(id).map(this::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questão não encontrada."));
    }

    /** Criação em lote */
    public List<QuestaoDTO> criarEmLote(List<QuestoesCreateItemDTO> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lista de questões vazia ou inválida.");
        }

        List<Questao> salvar = new ArrayList<>();
        for (var item : itens) {
            Questao q = Questao.builder()
                    .idFormulario(item.id_formulario())
                    .idUsuario(item.id_usuario())
                    .topic(item.topic())
                    .subskill(item.subskill())
                    .difficulty(item.difficulty())
                    .question(item.question())
                    .options(item.options())
                    .correctOption(item.correct_option())          // Object
                    .solution(item.solution())                     // legado
                    .solutionEnglish(item.solution_english())
                    .solutionPortugues(item.solution_portugues())
                    .structure(item.structure())
                    .format(item.format())
                    .representation(item.representation())
                    .hint(item.hint())                             // legado
                    .hintEnglish(item.hint_english())
                    .hintPortugues(item.hint_portugues())
                    .targetMistakes(item.target_mistakes())
                    .figure(item.figure())
                    .source(item.source())
                    .alternativaMarcada(item.alternativa_marcada())
                    .dica(item.dica() == null ? false : item.dica())
                    .solucao(item.solucao() == null ? false : item.solucao())
                    .modulo(item.modulo())
                    .ordem(item.ordem())
                    .build();
            salvar.add(q);
        }
        return repo.saveAll(salvar).stream().map(this::toDTO).toList();
    }

    /** Update parcial */
    public QuestaoDTO atualizar(String id, QuestaoUpdateDTO d) {
        var q = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Questão não encontrada."));

        if (d.id_formulario()     != null) q.setIdFormulario(d.id_formulario());
        if (d.id_usuario()        != null) q.setIdUsuario(d.id_usuario());
        if (d.topic()             != null) q.setTopic(d.topic());
        if (d.subskill()          != null) q.setSubskill(d.subskill());
        if (d.difficulty()        != null) q.setDifficulty(d.difficulty());
        if (d.question()          != null) q.setQuestion(d.question());
        if (d.options()           != null) q.setOptions(d.options());
        if (d.correct_option()    != null) q.setCorrectOption(d.correct_option());
        if (d.solution()          != null) q.setSolution(d.solution());              // legado
        if (d.structure()         != null) q.setStructure(d.structure());
        if (d.format()            != null) q.setFormat(d.format());
        if (d.representation()    != null) q.setRepresentation(d.representation());
        if (d.hint()              != null) q.setHint(d.hint());                      // legado
        if (d.target_mistakes()   != null) q.setTargetMistakes(d.target_mistakes());
        if (d.source()            != null) q.setSource(d.source());

        if (d.solution_english()  != null) q.setSolutionEnglish(d.solution_english());
        if (d.solution_portugues()!= null) q.setSolutionPortugues(d.solution_portugues());
        if (d.hint_english()      != null) q.setHintEnglish(d.hint_english());
        if (d.hint_portugues()    != null) q.setHintPortugues(d.hint_portugues());
        if (d.figure()            != null) q.setFigure(d.figure());

        if (d.alternativa_marcada()!= null) q.setAlternativaMarcada(d.alternativa_marcada());
        if (d.dica()              != null) q.setDica(d.dica());
        if (d.solucao()           != null) q.setSolucao(d.solucao());
        if (d.modulo()            != null) q.setModulo(d.modulo());
        if (d.ordem()             != null) q.setOrdem(d.ordem());

        return toDTO(repo.save(q));
    }

    public void deletar(String id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Questão não encontrada.");
        }
        repo.deleteById(id);
    }

    /** Atualização em lote - OTIMIZAÇÃO CRÍTICA para performance */
    public List<QuestaoDTO> atualizarEmLote(List<QuestaoBulkUpdateDTO.QuestaoBulkUpdateItem> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lista de questões vazia.");
        }
        
        // Busca todas as questões de uma vez
        var ids = itens.stream().map(QuestaoBulkUpdateDTO.QuestaoBulkUpdateItem::id).toList();
        var questoes = repo.findAllById(ids);
        
        if (questoes.size() != itens.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Algumas questões não foram encontradas.");
        }
        
        // Cria mapa para lookup rápido
        var itemMap = itens.stream()
            .collect(java.util.stream.Collectors.toMap(
                QuestaoBulkUpdateDTO.QuestaoBulkUpdateItem::id,
                item -> item
            ));
        
        // Atualiza todas de uma vez
        for (var q : questoes) {
            var item = itemMap.get(q.getId());
            if (item != null) {
                if (item.alternativa_marcada() != null) {
                    q.setAlternativaMarcada(item.alternativa_marcada());
                }
                if (item.dica() != null) {
                    q.setDica(item.dica());
                }
                if (item.solucao() != null) {
                    q.setSolucao(item.solucao());
                }
            }
        }
        
        // Salva todas de uma vez (batch save)
        return repo.saveAll(questoes).stream().map(this::toDTO).toList();
    }

    // ===== Listagens auxiliares =====
    public List<QuestaoDTO> listarPorSimulado(String idFormulario) {
        return repo.findByIdFormulario(idFormulario).stream()
                .sorted((q1, q2) -> {
                    Integer ordem1 = q1.getOrdem() != null ? q1.getOrdem() : Integer.MAX_VALUE;
                    Integer ordem2 = q2.getOrdem() != null ? q2.getOrdem() : Integer.MAX_VALUE;
                    return ordem1.compareTo(ordem2);
                })
                .map(this::toDTO).toList();
    }

    public List<QuestaoDTO> listarPorUsuario(String idUsuario) {
        try {
            // OTIMIZAÇÃO CRÍTICA: Limita a últimos 1000 questões para evitar retornar milhares
            // Isso é usado no finalizarAtualizandoTudo() e pode ser muito lento
            List<QuestaoDTO> resultado = new ArrayList<>();
            List<Questao> questoes = repo.findByIdUsuario(idUsuario);
            
            for (Questao q : questoes) {
                try {
                    resultado.add(toDTO(q));
                } catch (Exception e) {
                    log.warning(String.format("Erro ao converter questão %s para DTO: %s", q.getId(), e.getMessage()));
                    // Continua processando outras questões mesmo se uma falhar
                }
            }
            
            return resultado.stream().limit(1000).toList(); // Limite para performance
        } catch (Exception e) {
            log.severe(String.format("Erro ao listar questões por usuário %s: %s", idUsuario, e.getMessage()));
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro ao buscar questões do usuário: " + e.getMessage(), e);
        }
    }

    // ===== Mapper =====
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
                q.getSolutionEnglish(),
                q.getSolutionPortugues(),
                q.getStructure(),
                q.getFormat(),
                q.getRepresentation(),
                q.getHint(),
                q.getHintEnglish(),
                q.getHintPortugues(),
                q.getTargetMistakes(),
                q.getFigure(),
                q.getSource(),
                q.getAlternativaMarcada(),
                q.getDica(),
                q.getSolucao(),
                q.getModulo(),
                q.getOrdem()
        );
    }
}
