package ai.startup.questaosimulado.questaosimulado;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class QuestaoController {

    private static final Logger log = Logger.getLogger(QuestaoController.class.getName());
    private final QuestaoService service;

    public QuestaoController(QuestaoService service) {
        this.service = service;
    }

    // ===== CRUD =====
    @PostMapping("/questoes")
    public ResponseEntity<List<QuestaoDTO>> criarEmLote(@RequestBody List<QuestoesCreateItemDTO> itens) {
        return ResponseEntity.ok(service.criarEmLote(itens));
    }

    @GetMapping("/questoes")
    public ResponseEntity<List<QuestaoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/questoes/{id}")
    public ResponseEntity<QuestaoDTO> obter(@PathVariable String id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @PutMapping("/questoes/{id}")
    public ResponseEntity<QuestaoDTO> atualizar(@PathVariable String id,
                                                @RequestBody QuestaoUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/questoes/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // ===== OTIMIZAÇÃO: Atualização em lote =====
    @PutMapping("/questoes/bulk-update")
    public ResponseEntity<List<QuestaoDTO>> atualizarEmLote(
            @RequestBody QuestaoBulkUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizarEmLote(dto.questoes()));
    }

    // ===== Listagens auxiliares =====
    @GetMapping("/questoes/by-simulado/{idFormulario}")
    public ResponseEntity<List<QuestaoDTO>> listarPorSimulado(@PathVariable String idFormulario) {
        try {
            return ResponseEntity.ok(service.listarPorSimulado(idFormulario));
        } catch (ResponseStatusException e) {
            throw e; // Re-lança exceções de status já tratadas
        } catch (Exception e) {
            log.severe("Erro ao listar questões por simulado " + idFormulario + ": " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro ao buscar questões do simulado: " + e.getMessage(), e);
        }
    }

    @GetMapping("/questoes/by-usuario/{idUsuario}")
    public ResponseEntity<List<QuestaoDTO>> listarPorUsuario(@PathVariable String idUsuario) {
        try {
            return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
        } catch (ResponseStatusException e) {
            throw e; // Re-lança exceções de status já tratadas
        } catch (Exception e) {
            log.severe("Erro ao listar questões por usuário " + idUsuario + ": " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Erro ao buscar questões do usuário: " + e.getMessage(), e);
        }
    }
}