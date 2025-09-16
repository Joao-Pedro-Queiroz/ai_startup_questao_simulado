package ai.startup.questaosimulado.questaosimulado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class QuestaoController {

    private final QuestaoService service;

    public QuestaoController(QuestaoService service) {
        this.service = service;
    }

    // Cadastro em massa (lista de questões)
    // Se quiser público, remova @SecurityRequirement e libere no seu SecurityFilter deste serviço.
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cadastrar lista de questões (bulk)")
    @PostMapping("/questoes")
    public ResponseEntity<List<QuestaoDTO>> criarEmLote(@RequestBody List<QuestaoCreateDTO> lista) {
        return ResponseEntity.ok(service.criarEmLote(lista));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/questoes")
    public ResponseEntity<List<QuestaoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @SecurityRequirement(name = "bearerAuth")
    // alias: aceita “simulado” ou “formulario” na URL, ambas chamam o mesmo método
    @GetMapping({"/questoes/by-simulado/{id}"})
    public ResponseEntity<List<QuestaoDTO>> listarPorSimulado(@PathVariable String id) {
        return ResponseEntity.ok(service.listarPorSimulado(id));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/questoes/by-usuario/{id}")
    public ResponseEntity<List<QuestaoDTO>> listarPorUsuario(@PathVariable String id) {
        return ResponseEntity.ok(service.listarPorUsuario(id));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/questoes/{id}")
    public ResponseEntity<QuestaoDTO> obter(@PathVariable String id) {
        return ResponseEntity.ok(service.obter(id));
    }

    // update parcial
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/questoes/{id}")
    public ResponseEntity<QuestaoDTO> atualizar(@PathVariable String id, @RequestBody QuestaoUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/questoes/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}