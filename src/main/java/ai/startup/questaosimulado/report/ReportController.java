package ai.startup.questaosimulado.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportRepository repository;

    private ReportDTO toDTO(Report r) {
        return new ReportDTO(
                r.getId(),
                r.getQuestaoId(),
                r.getSimuladoId(),
                r.getUsuarioId(),
                r.getMensagem(),
                r.getModulo(),
                r.getPosicao(),
                r.getTopico(),
                r.getModo(),
                r.getData(),
                r.getStatus()
        );
    }

    @PostMapping
    public ResponseEntity<ReportDTO> criarReport(@RequestBody ReportCreateDTO dto) {
        Report report = Report.builder()
                .questaoId(dto.questao_id())
                .simuladoId(dto.simulado_id())
                .usuarioId(dto.usuario_id())
                .mensagem(dto.mensagem())
                .modulo(dto.modulo())
                .posicao(dto.posicao())
                .topico(dto.topico())
                .modo(dto.modo())
                .data(LocalDateTime.now())
                .status("NOVO")
                .build();

        Report saved = repository.save(report);
        return ResponseEntity.ok(toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<ReportDTO>> listarTodos() {
        return ResponseEntity.ok(
                repository.findAll().stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<ReportDTO>> listarPorUsuario(@PathVariable String userId) {
        return ResponseEntity.ok(
                repository.findByUsuarioId(userId).stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/questao/{questaoId}")
    public ResponseEntity<List<ReportDTO>> listarPorQuestao(@PathVariable String questaoId) {
        return ResponseEntity.ok(
                repository.findByQuestaoId(questaoId).stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReportDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(
                repository.findByStatus(status).stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReportDTO> atualizarStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateDTO dto) {
        return repository.findById(id)
                .map(report -> {
                    report.setStatus(dto.status());
                    return ResponseEntity.ok(toDTO(repository.save(report)));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    public record StatusUpdateDTO(String status) {}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarReport(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

