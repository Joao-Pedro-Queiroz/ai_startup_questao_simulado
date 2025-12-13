package ai.startup.questaosimulado.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {
    
    @Autowired
    private ReportRepository repository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Cria um novo report, salva no banco e envia notificação por email
     */
    public ReportDTO criarReport(ReportCreateDTO dto) {
        // Cria e salva o report no banco
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
        
        // Envia email de notificação para a equipe
        try {
            emailService.sendQuestionReportNotification(
                    dto.questao_id(),
                    dto.simulado_id(),
                    dto.usuario_id(),
                    dto.mensagem(),
                    dto.modulo(),
                    dto.posicao(),
                    dto.topico(),
                    dto.modo()
            );
            System.out.println("✅ Email de notificação de report enviado com sucesso");
        } catch (IOException e) {
            System.err.println("❌ Erro ao enviar email de notificação de report: " + e.getMessage());
            // Não lança exceção - o report já foi salvo no banco
            // O email é secundário, o importante é que o report foi registrado
        }
        
        return toDTO(saved);
    }
    
    public ReportDTO toDTO(Report r) {
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
    
    public List<ReportDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }
    
    public List<ReportDTO> listarPorUsuario(String userId) {
        return repository.findByUsuarioId(userId).stream()
                .map(this::toDTO)
                .toList();
    }
    
    public List<ReportDTO> listarPorQuestao(String questaoId) {
        return repository.findByQuestaoId(questaoId).stream()
                .map(this::toDTO)
                .toList();
    }
    
    public List<ReportDTO> listarPorStatus(String status) {
        return repository.findByStatus(status).stream()
                .map(this::toDTO)
                .toList();
    }
    
    public ReportDTO atualizarStatus(String id, String status) {
        return repository.findById(id)
                .map(report -> {
                    report.setStatus(status);
                    return toDTO(repository.save(report));
                })
                .orElse(null);
    }
    
    public void deletarReport(String id) {
        repository.deleteById(id);
    }
}

