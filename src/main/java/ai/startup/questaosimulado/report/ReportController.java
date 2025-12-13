package ai.startup.questaosimulado.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportDTO> criarReport(@RequestBody ReportCreateDTO dto) {
        ReportDTO saved = reportService.criarReport(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<ReportDTO>> listarTodos() {
        return ResponseEntity.ok(reportService.listarTodos());
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<ReportDTO>> listarPorUsuario(@PathVariable String userId) {
        return ResponseEntity.ok(reportService.listarPorUsuario(userId));
    }

    @GetMapping("/questao/{questaoId}")
    public ResponseEntity<List<ReportDTO>> listarPorQuestao(@PathVariable String questaoId) {
        return ResponseEntity.ok(reportService.listarPorQuestao(questaoId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReportDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(reportService.listarPorStatus(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReportDTO> atualizarStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateDTO dto) {
        ReportDTO updated = reportService.atualizarStatus(id, dto.status());
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    
    public record StatusUpdateDTO(String status) {}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarReport(@PathVariable String id) {
        reportService.deletarReport(id);
        return ResponseEntity.noContent().build();
    }
}

