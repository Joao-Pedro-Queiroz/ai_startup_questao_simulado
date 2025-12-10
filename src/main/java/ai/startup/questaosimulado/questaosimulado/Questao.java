package ai.startup.questaosimulado.questaosimulado;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("questoes")
@org.springframework.data.mongodb.core.index.CompoundIndex(name = "formulario_idx", def = "{'idFormulario': 1}")
@org.springframework.data.mongodb.core.index.CompoundIndex(name = "usuario_idx", def = "{'idUsuario': 1}")
public class Questao {
    @Id
    private String id;

    private String idFormulario;
    private String idUsuario;

    private String topic;
    private String subskill;
    private String difficulty;

    private String question;

    // Pode ser vazio em free_response
    private Map<String, String> options;

    // Agora pode ser "A"/"B"... ou -1 (free_response)
    private Object correctOption;

    // Soluções bilíngues (novos campos)
    private List<String> solutionEnglish;
    private List<String> solutionPortugues;

    // Mantém retrocompatibilidade: se só vier "solution" antiga
    private List<String> solution; // opcional

    private String structure;
    private String format;
    private String representation; // se vier

    // Dicas bilíngues
    private String hintEnglish;
    private String hintPortugues;

    // Mantém retrocompatibilidade: se só vier "hint" antiga
    private String hint; // opcional

    // Lista de erros-alvo
    private List<String> targetMistakes;

    // Figura (gráficos/linhas), estruturado ou livre
    private Map<String, Object> figure;

    private String source;

    // Campos do app
    private String alternativaMarcada;
    private Boolean dica;     // usou dica?
    private Boolean solucao;  // abriu solução?
    private Integer modulo;   // 1 ou 2
}