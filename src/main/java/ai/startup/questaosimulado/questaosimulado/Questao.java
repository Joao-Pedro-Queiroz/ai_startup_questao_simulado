package ai.startup.questaosimulado.questaosimulado;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("questao_simulado")
public class Questao {
    @Id
    private String id;

    private String idFormulario;   // id_formulario
    private String idUsuario;      // id_usuario

    private String topic;
    private String subskill;
    private String difficulty;

    private String question;       // pode conter LaTeX
    private Map<String, String> options; // A,B,C,D...
    private String correctOption;

    private List<String> solution; // passos da solução
    private String structure;
    private String format;
    private String representation;
    private String hint;

    private List<String> targetMistakes;
    private String source;

    private String  alternativaMarcada;
    private Boolean dica;
    private Boolean solucao;
    private Integer modulo;
}