package br.com.sw2you.realmeet.report.validator;

import static br.com.sw2you.realmeet.util.Constants.ALLOCATION_REPORT_MAX_MONTHS_INTERVAL;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.DATE_FROM;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.DATE_TO;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EXCEEDS_INTERVAL;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.INCONSISTENT;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.validateRequired;
import static java.time.Period.between;

import br.com.sw2you.realmeet.report.model.AbstractReportData;
import br.com.sw2you.realmeet.report.model.AllocationReportData;
import br.com.sw2you.realmeet.validator.ValidationErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AllocationReportValidator extends AbstractReportValidator {
    private final int maxMonthInteval;

    public AllocationReportValidator(@Value(ALLOCATION_REPORT_MAX_MONTHS_INTERVAL) int maxMonthInteval) {
        this.maxMonthInteval = maxMonthInteval;
    }

    @Override
    protected void validate(AbstractReportData abstractReportData, ValidationErrors validationErrors) {
        var allocationReportData = (AllocationReportData) abstractReportData;

        validateRequired(allocationReportData.getDateFrom(), DATE_FROM, validationErrors);
        validateRequired(allocationReportData.getDateTo(), DATE_TO, validationErrors);

        if (!validationErrors.hasErrors()) {
            if (allocationReportData.getDateFrom().isAfter(allocationReportData.getDateTo())) {
                validationErrors.add(DATE_FROM, DATE_FROM + INCONSISTENT);
            } else if (
                between(allocationReportData.getDateFrom(), allocationReportData.getDateTo()).getMonths() >
                maxMonthInteval
            ) {
                validationErrors.add(DATE_TO, DATE_TO + EXCEEDS_INTERVAL);
            }
        }
    }
}
