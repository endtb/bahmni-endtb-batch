package org.bahmni.batch.exports;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class NonTBDrugOrderBaseExportStep extends BaseExportStep {

    @Autowired
    public NonTBDrugOrderBaseExportStep(StepBuilderFactory stepBuilderFactory,
                                        DataSource dataSource,
                                        @Value("${outputFolder}/nonTbDrugOrder.csv") Resource outputFolder,
                                        @Value("${nonTbDrugOrderHeaders}") String headers ) {
        super(stepBuilderFactory, dataSource, "nonTbDrugOrder.sql.ftl", outputFolder, "nonTbDrugOrder", headers);
    }
}
