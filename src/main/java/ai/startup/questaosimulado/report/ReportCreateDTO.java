package ai.startup.questaosimulado.report;

public record ReportCreateDTO(
        String questao_id,
        String simulado_id,
        String usuario_id,
        String mensagem,
        Integer modulo,
        Integer posicao,
        String topico,
        String modo
) {}

