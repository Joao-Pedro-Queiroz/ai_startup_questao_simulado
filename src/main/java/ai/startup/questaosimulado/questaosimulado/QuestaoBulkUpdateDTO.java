package ai.startup.questaosimulado.questaosimulado;

import java.util.List;

/**
 * DTO para atualização em lote de questões
 * Otimiza performance ao atualizar múltiplas questões de uma vez
 */
public record QuestaoBulkUpdateDTO(
    List<QuestaoBulkUpdateItem> questoes
) {
    public record QuestaoBulkUpdateItem(
        String id,
        String alternativa_marcada,
        Boolean dica,
        Boolean solucao
    ) {}
}

