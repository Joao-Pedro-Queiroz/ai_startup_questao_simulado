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

    // Soluções bilíngues
    private List<String> solutionEnglish;
    private List<String> solutionPortugues;

    private String structure;
    private String format;

    // Dicas bilíngues
    private String hintEnglish;
    private String hintPortugues;

    // Lista de erros-alvo
    private List<String> targetMistakes;

    // Figura (gráficos/linhas), estruturado ou livre
    private Map<String, Object> figure;

    private String source;
    private String exampleId;

    // Campos do app
    private String alternativaMarcada;
    private Boolean dica;     // usou dica?
    private Boolean solucao;  // abriu solução?
    private Integer modulo;   // 1 ou 2
    private Integer ordem;    // ordem da questão no simulado
}