package ai.startup.questaosimulado.questaosimulado;

import java.util.List;
import java.util.Map;

public record QuestoesCreateItemDTO(
        String id_formulario,
        String id_usuario,
        String topic,
        String subskill,
        String difficulty,
        String question,
        Map<String,String> options,
        Object correct_option,                // <- agora Object
        List<String> solution,                // legado (se vier)
        String structure,
        String format,
        String representation,
        String hint,                          // legado (se vier)
        List<String> target_mistakes,
        String source,

        // Novos campos:
        List<String> solution_english,
        List<String> solution_portugues,
        String hint_english,
        String hint_portugues,
        Map<String,Object> figure,

        // Campos do app:
        String alternativa_marcada,
        Boolean dica,
        Boolean solucao,
        Integer modulo,
        Integer ordem
) {}