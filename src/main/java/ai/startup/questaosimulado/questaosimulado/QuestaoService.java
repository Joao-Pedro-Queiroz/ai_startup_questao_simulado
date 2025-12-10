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
            // Limpar duplicações de LaTeX e equações matemáticas
            String cleanedQuestion = TextCleaner.cleanText(item.question());
            Map<String, String> cleanedOptions = TextCleaner.cleanOptions(item.options());
            String cleanedHint = TextCleaner.cleanText(item.hint());
            String cleanedHintEnglish = TextCleaner.cleanText(item.hint_english());
            String cleanedHintPortugues = TextCleaner.cleanText(item.hint_portugues());
            List<String> cleanedSolution = TextCleaner.cleanStringList(item.solution());
            List<String> cleanedSolutionEnglish = TextCleaner.cleanStringList(item.solution_english());
            List<String> cleanedSolutionPortugues = TextCleaner.cleanStringList(item.solution_portugues());
            
            Questao q = Questao.builder()
                    .idFormulario(item.id_formulario())
                    .idUsuario(item.id_usuario())
                    .topic(item.topic())
                    .subskill(item.subskill())
                    .difficulty(item.difficulty())
                    .question(cleanedQuestion)
                    .options(cleanedOptions)
                    .correctOption(item.correct_option())          // Object
                    .solution(cleanedSolution)                     // legado
                    .solutionEnglish(cleanedSolutionEnglish)
                    .solutionPortugues(cleanedSolutionPortugues)
                    .structure(item.structure())
                    .format(item.format())
                    .representation(item.representation())
                    .hint(cleanedHint)                             // legado
                    .hintEnglish(cleanedHintEnglish)
                    .hintPortugues(cleanedHintPortugues)
                    .targetMistakes(item.target_mistakes())
                    .figure(item.figure())
                    .source(item.source())
                    .alternativaMarcada(item.alternativa_marcada())
                    .dica(item.dica() == null ? false : item.dica())
                    .solucao(item.solucao() == null ? false : item.solucao())
                    .modulo(item.modulo())
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
        if (d.question()          != null) q.setQuestion(TextCleaner.cleanText(d.question()));
        if (d.options()           != null) q.setOptions(TextCleaner.cleanOptions(d.options()));
        if (d.correct_option()    != null) q.setCorrectOption(d.correct_option());
        if (d.solution()          != null) q.setSolution(TextCleaner.cleanStringList(d.solution()));              // legado
        if (d.structure()         != null) q.setStructure(d.structure());
        if (d.format()            != null) q.setFormat(d.format());
        if (d.representation()    != null) q.setRepresentation(d.representation());
        if (d.hint()              != null) q.setHint(TextCleaner.cleanText(d.hint()));                      // legado
        if (d.target_mistakes()   != null) q.setTargetMistakes(d.target_mistakes());
        if (d.source()            != null) q.setSource(d.source());

        if (d.solution_english()  != null) q.setSolutionEnglish(TextCleaner.cleanStringList(d.solution_english()));
        if (d.solution_portugues()!= null) q.setSolutionPortugues(TextCleaner.cleanStringList(d.solution_portugues()));
        if (d.hint_english()      != null) q.setHintEnglish(TextCleaner.cleanText(d.hint_english()));
        if (d.hint_portugues()    != null) q.setHintPortugues(TextCleaner.cleanText(d.hint_portugues()));
        if (d.figure()            != null) q.setFigure(d.figure());

        if (d.alternativa_marcada()!= null) q.setAlternativaMarcada(d.alternativa_marcada());
        if (d.dica()              != null) q.setDica(d.dica());
        if (d.solucao()           != null) q.setSolucao(d.solucao());
        if (d.modulo()            != null) q.setModulo(d.modulo());

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
        try {
            // Normalmente um simulado tem ~44 questões, então não precisa de limite
            // Mas adicionamos limite de segurança para evitar problemas
            List<QuestaoDTO> resultado = new ArrayList<>();
            List<Questao> questoes = repo.findByIdFormulario(idFormulario);
            
            for (Questao q : questoes) {
                try {
                    resultado.add(toDTO(q));
                } catch (Exception e) {
                    log.warning(String.format("Erro ao converter questão %s para DTO: %s", q.getId(), e.getMessage()));
                    // Continua processando outras questões mesmo se uma falhar
                }
            }
            
            return resultado.stream().limit(100).toList(); // Limite de segurança
        } catch (Exception e) {
            log.severe(String.format("Erro ao listar questões por simulado %s: %s", idFormulario, e.getMessage()));
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro ao buscar questões do simulado: " + e.getMessage(), e);
        }
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
        if (q == null) {
            throw new IllegalArgumentException("Questão não pode ser null");
        }
        
        // Aplica limpeza também na leitura como camada de segurança
        // Isso garante que mesmo questões antigas sejam limpas ao serem retornadas
        // Usa try-catch individual para cada campo para evitar que um campo corrompido quebre tudo
        try {
            return new QuestaoDTO(
                    q.getId(),
                    q.getIdFormulario(),
                    q.getIdUsuario(),
                    q.getTopic(),
                    q.getSubskill(),
                    q.getDifficulty(),
                    safeCleanText(q.getQuestion()),
                    safeCleanOptions(q.getOptions()),
                    q.getCorrectOption(),
                    safeCleanStringList(q.getSolution()),
                    safeCleanStringList(q.getSolutionEnglish()),
                    safeCleanStringList(q.getSolutionPortugues()),
                    q.getStructure(),
                    q.getFormat(),
                    q.getRepresentation(),
                    safeCleanText(q.getHint()),
                    safeCleanText(q.getHintEnglish()),
                    safeCleanText(q.getHintPortugues()),
                    q.getTargetMistakes(),
                    q.getFigure(),
                    q.getSource(),
                    q.getAlternativaMarcada(),
                    q.getDica(),
                    q.getSolucao(),
                    q.getModulo()
            );
        } catch (Exception e) {
            log.severe(String.format("Erro ao converter questão %s para DTO: %s", q.getId(), e.getMessage()));
            throw new RuntimeException("Erro ao converter questão para DTO: " + e.getMessage(), e);
        }
    }
    
    // Métodos auxiliares seguros que nunca lançam exceção
    private String safeCleanText(String text) {
        try {
            return TextCleaner.cleanText(text);
        } catch (Exception e) {
            log.warning("Erro ao limpar texto, retornando original: " + e.getMessage());
            return text; // Retorna original se falhar
        }
    }
    
    private Map<String, String> safeCleanOptions(Map<String, String> options) {
        try {
            return TextCleaner.cleanOptions(options);
        } catch (Exception e) {
            log.warning("Erro ao limpar opções, retornando original: " + e.getMessage());
            return options; // Retorna original se falhar
        }
    }
    
    private List<String> safeCleanStringList(List<String> list) {
        try {
            return TextCleaner.cleanStringList(list);
        } catch (Exception e) {
            log.warning("Erro ao limpar lista de strings, retornando original: " + e.getMessage());
            return list; // Retorna original se falhar
        }
    }

    /**
     * Limpa todas as questões existentes no banco (endpoint administrativo)
     * ATENÇÃO: Esta operação atualiza todas as questões e pode ser lenta
     */
    public int limparTodasQuestoes() {
        List<Questao> todas = repo.findAll();
        int atualizadas = 0;
        
        for (Questao q : todas) {
            boolean precisaAtualizar = false;
            
            // Verifica se há duplicações e limpa
            String questionOriginal = q.getQuestion();
            String questionLimpa = TextCleaner.cleanText(questionOriginal);
            if (!questionOriginal.equals(questionLimpa)) {
                q.setQuestion(questionLimpa);
                precisaAtualizar = true;
            }
            
            Map<String, String> optionsOriginal = q.getOptions();
            Map<String, String> optionsLimpa = TextCleaner.cleanOptions(optionsOriginal);
            if (optionsOriginal != null && !optionsOriginal.equals(optionsLimpa)) {
                q.setOptions(optionsLimpa);
                precisaAtualizar = true;
            }
            
            String hintOriginal = q.getHint();
            String hintLimpa = TextCleaner.cleanText(hintOriginal);
            if (hintOriginal != null && !hintOriginal.equals(hintLimpa)) {
                q.setHint(hintLimpa);
                precisaAtualizar = true;
            }
            
            String hintEnglishOriginal = q.getHintEnglish();
            String hintEnglishLimpa = TextCleaner.cleanText(hintEnglishOriginal);
            if (hintEnglishOriginal != null && !hintEnglishOriginal.equals(hintEnglishLimpa)) {
                q.setHintEnglish(hintEnglishLimpa);
                precisaAtualizar = true;
            }
            
            String hintPortuguesOriginal = q.getHintPortugues();
            String hintPortuguesLimpa = TextCleaner.cleanText(hintPortuguesOriginal);
            if (hintPortuguesOriginal != null && !hintPortuguesOriginal.equals(hintPortuguesLimpa)) {
                q.setHintPortugues(hintPortuguesLimpa);
                precisaAtualizar = true;
            }
            
            List<String> solutionOriginal = q.getSolution();
            List<String> solutionLimpa = TextCleaner.cleanStringList(solutionOriginal);
            if (solutionOriginal != null && !solutionOriginal.equals(solutionLimpa)) {
                q.setSolution(solutionLimpa);
                precisaAtualizar = true;
            }
            
            List<String> solutionEnglishOriginal = q.getSolutionEnglish();
            List<String> solutionEnglishLimpa = TextCleaner.cleanStringList(solutionEnglishOriginal);
            if (solutionEnglishOriginal != null && !solutionEnglishOriginal.equals(solutionEnglishLimpa)) {
                q.setSolutionEnglish(solutionEnglishLimpa);
                precisaAtualizar = true;
            }
            
            List<String> solutionPortuguesOriginal = q.getSolutionPortugues();
            List<String> solutionPortuguesLimpa = TextCleaner.cleanStringList(solutionPortuguesOriginal);
            if (solutionPortuguesOriginal != null && !solutionPortuguesOriginal.equals(solutionPortuguesLimpa)) {
                q.setSolutionPortugues(solutionPortuguesLimpa);
                precisaAtualizar = true;
            }
            
            if (precisaAtualizar) {
                repo.save(q);
                atualizadas++;
            }
        }
        
        return atualizadas;
    }
}
