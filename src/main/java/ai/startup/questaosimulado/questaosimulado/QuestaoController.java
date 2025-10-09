package ai.startup.questaosimulado.questaosimulado;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class QuestaoController {

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

    // ===== Listagens auxiliares =====
    @GetMapping("/questoes/by-simulado/{idFormulario}")
    public ResponseEntity<List<QuestaoDTO>> listarPorSimulado(@PathVariable String idFormulario) {
        return ResponseEntity.ok(service.listarPorSimulado(idFormulario));
    }

    @GetMapping("/questoes/by-usuario/{idUsuario}")
    public ResponseEntity<List<QuestaoDTO>> listarPorUsuario(@PathVariable String idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }
}