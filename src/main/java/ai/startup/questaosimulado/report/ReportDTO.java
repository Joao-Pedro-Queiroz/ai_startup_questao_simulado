package ai.startup.questaosimulado.report;

import java.time.LocalDateTime;

public record ReportDTO(
        String id,
        String questao_id,
        String simulado_id,
        String usuario_id,
        String mensagem,
        Integer modulo,
        Integer posicao,
        String topico,
        String modo,
        LocalDateTime data,
        String status
) {}

