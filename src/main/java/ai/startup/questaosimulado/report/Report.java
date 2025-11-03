package ai.startup.questaosimulado.report;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("reports")
public class Report {
    @Id
    private String id;
    
    private String questaoId;
    private String simuladoId;
    private String usuarioId;
    private String mensagem;
    private Integer modulo;
    private Integer posicao;
    private String topico;
    private String modo;
    private LocalDateTime data;
    private String status; // "NOVO", "ANALISADO", "RESOLVIDO"
}

