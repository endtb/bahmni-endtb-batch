package org.bahmni.batch.exports;

import org.bahmni.batch.helper.FreeMarkerEvaluator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.PassThroughFieldExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


public class BaseExportStep {

    @Value("${restrictByTreatmentInitiationCohort:false}")
    public Boolean restrictByTreatmentInitiationCohort;

    private DataSource dataSource;

    private StepBuilderFactory stepBuilderFactory;

    private String ftlFilename;

    private Resource outputFolder;

    private String exportName;

    private String headers;

    @Autowired
    private FreeMarkerEvaluator<Map<String, Object>> freeMarkerEvaluator;

    public BaseExportStep(StepBuilderFactory stepBuilderFactory, DataSource dataSource, String ftlFilename, Resource outputFolder, String exportName, String headers) {
        this.dataSource = dataSource;
        this.stepBuilderFactory = stepBuilderFactory;
        this.ftlFilename = ftlFilename;
        this.outputFolder = outputFolder;
        this.exportName = exportName;
        this.headers = headers;
    }

    public Step getStep() {
        return stepBuilderFactory
                .get(exportName)
                .<String, String>chunk(50)
                .reader(jdbcItemReader())
                .writer(flatFileItemWriter())
                .build();
    }

    private JdbcCursorItemReader jdbcItemReader() {

        Map<String, Object> input = new HashMap<String,Object>();
        input.put("restrictByTreatmentInitiationCohort", restrictByTreatmentInitiationCohort);
        String sql = freeMarkerEvaluator.evaluate(ftlFilename, input);

        JdbcCursorItemReader reader = new JdbcCursorItemReader();
        reader.setDataSource(dataSource);
        reader.setSql(sql);
        reader.setRowMapper(new ColumnMapRowMapper());
        return reader;
    }

    private FlatFileItemWriter<String> flatFileItemWriter() {
        FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
        writer.setResource(outputFolder);
        DelimitedLineAggregator delimitedLineAggregator = new DelimitedLineAggregator();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(new PassThroughFieldExtractor());
        writer.setLineAggregator(delimitedLineAggregator);
        writer.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write(getHeaders());
            }
        });
        return writer;
    }

    public String getHeaders() {
        return headers;
    }

}
