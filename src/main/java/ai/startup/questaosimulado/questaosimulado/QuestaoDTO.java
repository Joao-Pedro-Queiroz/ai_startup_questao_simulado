package ai.startup.questaosimulado.questaosimulado;

import java.util.List;
import java.util.Map;

public record QuestaoDTO(
        String id,
        String id_formulario,
        String id_usuario,
        String topic,
        String subskill,
        String difficulty,
        String question,
        Map<String,String> options,
        Object correct_option,                 // pode ser "A"/"B"... ou -1 (free_response)
        List<String> solution,                 // legado (se vier)
        List<String> solution_english,
        List<String> solution_portugues,
        String structure,
        String format,
        String representation,
        String hint,                           // legado (se vier)
        String hint_english,
        String hint_portugues,
        List<String> target_mistakes,
        Map<String,Object> figure,
        String source,

        // campos do app
        String alternativa_marcada,
        Boolean dica,
        Boolean solucao,
        Integer modulo,
        Integer ordem
) {}