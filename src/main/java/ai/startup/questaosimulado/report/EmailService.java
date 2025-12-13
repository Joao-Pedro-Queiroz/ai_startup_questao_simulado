package ai.startup.questaosimulado.report;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;
    
    @Value("${sendgrid.from.email}")
    private String fromEmail;
    
    @Value("${sendgrid.from.name}")
    private String fromName;
    
    @Value("${support.notification.email}")
    private String supportNotificationEmail;
    
    /**
     * Envia notificação de report de problema em questão
     */
    public void sendQuestionReportNotification(
            String questaoId,
            String simuladoId,
            String usuarioId,
            String mensagem,
            Integer modulo,
            Integer posicao,
            String topico,
            String modo) throws IOException {
        
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(supportNotificationEmail);
        String subject = "Report de problema em questão - BrainWin";
        
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <h2 style="color: #ff1b8d;">⚠️ Report de Problema em Questão</h2>
                <p>Um novo report de problema foi recebido:</p>
                
                <div style="background: #f5f5f5; padding: 15px; border-radius: 8px; margin: 20px 0;">
                    <p style="margin: 5px 0;"><strong>ID da Questão:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>ID do Simulado:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>ID do Usuário:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Módulo:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Posição:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Tópico:</strong> %s</p>
                    <p style="margin: 5px 0;"><strong>Modo:</strong> %s</p>
                </div>
                
                <div style="background: #fff; border-left: 4px solid #ff1b8d; padding: 15px; margin: 20px 0;">
                    <p style="margin: 0 0 10px 0;"><strong>Mensagem do usuário:</strong></p>
                    <p style="margin: 0; white-space: pre-wrap;">%s</p>
                </div>
                
                <p style="color: #666; font-size: 14px; margin-top: 30px;">
                    Acesse o sistema para analisar e resolver o problema reportado.
                </p>
                
                <hr style="margin: 30px 0; border: none; border-top: 1px solid #e0e0e0;">
                <p style="color: #999; font-size: 12px;">
                    BrainWin - Sistema de Reports
                </p>
            </div>
        """.formatted(
            questaoId != null ? questaoId : "N/A",
            simuladoId != null ? simuladoId : "N/A",
            usuarioId != null ? usuarioId : "N/A",
            modulo != null ? modulo.toString() : "N/A",
            posicao != null ? posicao.toString() : "N/A",
            topico != null ? topico : "N/A",
            modo != null ? modo : "N/A",
            mensagem != null ? mensagem : "Sem mensagem"
        );
        
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);
        
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 400) {
                throw new IOException("Erro ao enviar email: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (IOException ex) {
            throw new IOException("Falha ao enviar email via SendGrid: " + ex.getMessage(), ex);
        }
    }
}

