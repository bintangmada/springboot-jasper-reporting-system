package com.bintang.springboot_jasper_reporting_system.service;

import com.bintang.springboot_jasper_reporting_system.entity.Employee;
import com.bintang.springboot_jasper_reporting_system.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    private final EmployeeRepository employeeRepository;

    public ReportService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    private JasperPrint generateJasperPrint() throws Exception {
        // Load and compile the .jrxml template using ClassPathResource
        InputStream reportStream = new ClassPathResource("reports/employee_report.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Fetch data from database
        List<Employee> employees = employeeRepository.findAll();
        log.info("Found {} employees for report", employees.size());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);

        // Set parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", "PT. Bintang Mada Corporation - Employee Data");
        parameters.put("generatedDate",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss")));

        // Fill the report
        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }

    /**
     * Export employee report as PDF bytes.
     */
    public byte[] exportPdf() throws Exception {
        log.info("Generating Employee Report (PDF)...");
        JasperPrint jasperPrint = generateJasperPrint();
        byte[] result = JasperExportManager.exportReportToPdf(jasperPrint);
        log.info("PDF report generated successfully, size: {} bytes", result.length);
        return result;
    }

    /**
     * Export employee report as Excel (.xlsx) bytes.
     */
    public byte[] exportExcel() throws Exception {
        log.info("Generating Employee Report (Excel)...");
        JasperPrint jasperPrint = generateJasperPrint();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setDetectCellType(true);
        configuration.setWhitePageBackground(false);
        exporter.setConfiguration(configuration);

        exporter.exportReport();

        byte[] result = outputStream.toByteArray();
        log.info("Excel report generated successfully, size: {} bytes", result.length);
        return result;
    }
}
