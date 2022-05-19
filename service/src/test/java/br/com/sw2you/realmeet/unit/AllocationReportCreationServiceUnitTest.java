package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.report.enumeration.ReportFormat.PDF;
import static br.com.sw2you.realmeet.util.Constants.EMPTY;
import static br.com.sw2you.realmeet.utils.TestConstants.EMAIL_TO;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.DATE_FROM;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.DATE_TO;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EMAIL;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EXCEEDS_INTERVAL;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.INCONSISTENT;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.MISSING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import br.com.sw2you.realmeet.config.JasperReportsConfiguration;
import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.report.handler.AllocationReportHandler;
import br.com.sw2you.realmeet.report.resolver.ReportHandlerResolver;
import br.com.sw2you.realmeet.report.validator.AllocationReportValidator;
import br.com.sw2you.realmeet.service.ReportCreationService;
import br.com.sw2you.realmeet.service.ReportDispatcherService;
import br.com.sw2you.realmeet.validator.ValidationError;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class AllocationReportCreationServiceUnitTest extends BaseUnitTest {
    private static final int MAX_MONTHS = 2;
    private ReportCreationService victim;

    @Mock
    private ReportHandlerResolver reportHandlerResolver;

    @Mock
    private ReportDispatcherService reportDispatcherService;

    @Mock
    private AllocationRepository allocationRepository;

    @BeforeEach
    void setupEach() {
        victim = new ReportCreationService(reportHandlerResolver, reportDispatcherService);
        given(reportHandlerResolver.resolveReportHandler(any()))
            .willReturn(
                new AllocationReportHandler(
                    new JasperReportsConfiguration().allocationReport(),
                    allocationRepository,
                    new AllocationReportValidator(MAX_MONTHS)
                )
            );
    }

    @Test
    void testCreateAllocationReportSuccess() {
        victim.createAllocationReport(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), EMAIL_TO, PDF.name());
        verify(reportDispatcherService).dispatch(any());
    }

    @Test
    void testCreateAllocationReportNoEMail() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.createAllocationReport(
                    LocalDate.now().minusDays(5),
                    LocalDate.now().minusDays(1),
                    EMPTY,
                    PDF.name()
                )
        );

        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(new ValidationError(EMAIL, EMAIL + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testCreateAllocationReporDateFromAfterDateTo() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.createAllocationReport(
                    LocalDate.now().minusDays(1),
                    LocalDate.now().minusDays(3),
                    EMAIL_TO,
                    PDF.name()
                )
        );

        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(DATE_FROM, DATE_FROM + INCONSISTENT),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testCreateAllocationReporDateIntervalExceedsMax() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.createAllocationReport(
                    LocalDate.now().minusMonths(MAX_MONTHS + 2),
                    LocalDate.now().minusMonths(1),
                    EMAIL_TO,
                    PDF.name()
                )
        );

        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(DATE_TO, DATE_TO + EXCEEDS_INTERVAL),
            exception.getValidationErrors().getError(0)
        );
    }
}
