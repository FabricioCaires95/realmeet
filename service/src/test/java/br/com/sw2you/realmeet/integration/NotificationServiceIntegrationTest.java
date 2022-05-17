package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.email.TemplateType.ALLOCATION_CREATED;
import static br.com.sw2you.realmeet.email.TemplateType.ALLOCATION_DELETED;
import static br.com.sw2you.realmeet.email.TemplateType.ALLOCATION_UPDATED;
import static br.com.sw2you.realmeet.util.Constants.ALLOCATION;
import static br.com.sw2you.realmeet.utils.TestDataCreator.allocationBuilder;
import static br.com.sw2you.realmeet.utils.TestDataCreator.roomBuilder;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import br.com.sw2you.realmeet.config.properties.EmailConfigProperties;
import br.com.sw2you.realmeet.config.properties.TemplateConfigProperties;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import br.com.sw2you.realmeet.domain.entity.Allocation;
import br.com.sw2you.realmeet.email.EmailSender;
import br.com.sw2you.realmeet.email.TemplateType;
import br.com.sw2you.realmeet.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class NotificationServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private NotificationService victim;

    @Autowired
    private TemplateConfigProperties templateConfigProperties;

    @Autowired
    private EmailConfigProperties emailConfigProperties;

    @MockBean
    private EmailSender emailSender;

    private Allocation allocation;

    @Override
    protected void setupEach() throws Exception {
        allocation = allocationBuilder(roomBuilder().build()).build();
    }

    @Test
    void testNotifyAllocationCreated() {
        victim.notifyAllocationCreated(allocation);
        testInteractions(ALLOCATION_CREATED);
    }

    @Test
    void testNotifyAllocationUpdated() {
        victim.notifyAllocationUpdated(allocation);
        testInteractions(ALLOCATION_UPDATED);
    }

    @Test
    void testNotifyAllocationDeleted() {
        victim.notifyAllocationDeleted(allocation);
        testInteractions(ALLOCATION_DELETED);
    }

    private void testInteractions(TemplateType templateType) {
        var emailTemplate = templateConfigProperties.getEmailTemplate(templateType);
        verify(emailSender)
            .send(
                argThat(
                    emailInfo ->
                        emailInfo.getSubject().equals(emailTemplate.getSubject()) &&
                        emailInfo.getTo().get(0).equals(allocation.getEmployee().getEmail()) &&
                        emailInfo.getFrom().equals(emailConfigProperties.getFrom()) &&
                        emailInfo.getTemplate().equals(emailTemplate.getTemplateName()) &&
                        emailInfo.getTemplateData().get(ALLOCATION).equals(allocation)
                )
            );
    }
}
