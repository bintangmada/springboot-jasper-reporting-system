package com.bintang.springboot_jasper_reporting_system.service;

import com.bintang.springboot_jasper_reporting_system.entity.Employee;
import com.bintang.springboot_jasper_reporting_system.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.*;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
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

    private JasperReport buildReport() throws JRException {
        // Create report design programmatically
        JasperDesign design = new JasperDesign();
        design.setName("employee_report");
        design.setPageWidth(842);
        design.setPageHeight(595);
        design.setOrientation(OrientationEnum.LANDSCAPE);
        design.setColumnWidth(802);
        design.setLeftMargin(20);
        design.setRightMargin(20);
        design.setTopMargin(20);
        design.setBottomMargin(20);

        // Parameters
        JRDesignParameter paramTitle = new JRDesignParameter();
        paramTitle.setName("reportTitle");
        paramTitle.setValueClass(String.class);
        design.addParameter(paramTitle);

        JRDesignParameter paramDate = new JRDesignParameter();
        paramDate.setName("generatedDate");
        paramDate.setValueClass(String.class);
        design.addParameter(paramDate);

        // Fields
        addField(design, "employeeCode", String.class);
        addField(design, "name", String.class);
        addField(design, "department", String.class);
        addField(design, "position", String.class);
        addField(design, "salary", BigDecimal.class);
        addField(design, "joinDate", java.time.LocalDate.class);

        // Variables
        JRDesignVariable totalSalary = new JRDesignVariable();
        totalSalary.setName("totalSalary");
        totalSalary.setValueClass(BigDecimal.class);
        totalSalary.setCalculation(CalculationEnum.SUM);
        totalSalary.setExpression(new JRDesignExpression("$F{salary}"));
        design.addVariable(totalSalary);

        JRDesignVariable rowNum = new JRDesignVariable();
        rowNum.setName("rowNumber");
        rowNum.setValueClass(Integer.class);
        rowNum.setCalculation(CalculationEnum.COUNT);
        rowNum.setExpression(new JRDesignExpression("Boolean.TRUE"));
        design.addVariable(rowNum);

        // Title Band
        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(80);
        addStaticText(titleBand, 0, 0, 802, 35, "EMPLOYEE REPORT", 18, true, HorizontalTextAlignEnum.CENTER, null, null);
        addTextField(titleBand, 0, 35, 802, 20, "$P{reportTitle}", 11, false, HorizontalTextAlignEnum.CENTER, null, null);
        addTextField(titleBand, 0, 55, 802, 20, "\"Generated: \" + $P{generatedDate}", 9, false, HorizontalTextAlignEnum.CENTER, null, null);
        design.setTitle(titleBand);

        // Column Header Band
        Color headerBg = new Color(68, 114, 196);
        JRDesignBand columnHeaderBand = new JRDesignBand();
        columnHeaderBand.setHeight(30);
        int[] widths = {50, 90, 162, 130, 130, 120, 120};
        String[] headers = {"No", "Code", "Name", "Department", "Position", "Salary (IDR)", "Join Date"};
        int xPos = 0;
        for (int i = 0; i < headers.length; i++) {
            addStaticText(columnHeaderBand, xPos, 0, widths[i], 30, headers[i], 10, true, HorizontalTextAlignEnum.CENTER, headerBg, Color.WHITE);
            xPos += widths[i];
        }
        design.setColumnHeader(columnHeaderBand);

        // Detail Band
        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(25);
        Color borderColor = new Color(204, 204, 204);
        xPos = 0;
        addTextField(detailBand, xPos, 0, widths[0], 25, "$V{rowNumber}", 9, false, HorizontalTextAlignEnum.CENTER, null, null);
        xPos += widths[0];
        addTextField(detailBand, xPos, 0, widths[1], 25, "$F{employeeCode}", 9, false, HorizontalTextAlignEnum.CENTER, null, null);
        xPos += widths[1];
        addTextField(detailBand, xPos, 0, widths[2], 25, "$F{name}", 9, false, HorizontalTextAlignEnum.LEFT, null, null);
        xPos += widths[2];
        addTextField(detailBand, xPos, 0, widths[3], 25, "$F{department}", 9, false, HorizontalTextAlignEnum.LEFT, null, null);
        xPos += widths[3];
        addTextField(detailBand, xPos, 0, widths[4], 25, "$F{position}", 9, false, HorizontalTextAlignEnum.LEFT, null, null);
        xPos += widths[4];
        JRDesignTextField salaryField = addTextField(detailBand, xPos, 0, widths[5], 25, "$F{salary}", 9, false, HorizontalTextAlignEnum.RIGHT, null, null);
        salaryField.setPattern("#,##0");
        xPos += widths[5];
        JRDesignTextField dateField = addTextField(detailBand, xPos, 0, widths[6], 25, "java.sql.Date.valueOf($F{joinDate})", 9, false, HorizontalTextAlignEnum.CENTER, null, null);
        dateField.setPattern("dd-MM-yyyy");
        ((JRDesignSection) design.getDetailSection()).addBand(detailBand);

        // Summary Band
        Color summaryBg = new Color(226, 239, 218);
        JRDesignBand summaryBand = new JRDesignBand();
        summaryBand.setHeight(40);
        addStaticText(summaryBand, 0, 5, 562, 30, "TOTAL SALARY:", 11, true, HorizontalTextAlignEnum.RIGHT, summaryBg, null);
        JRDesignTextField totalField = addTextField(summaryBand, 562, 5, 120, 30, "$V{totalSalary}", 11, true, HorizontalTextAlignEnum.RIGHT, summaryBg, null);
        totalField.setPattern("#,##0");
        addStaticText(summaryBand, 682, 5, 120, 30, "", 9, false, HorizontalTextAlignEnum.CENTER, summaryBg, null);
        design.setSummary(summaryBand);

        // Page Footer
        JRDesignBand pageFooter = new JRDesignBand();
        pageFooter.setHeight(25);
        addTextField(pageFooter, 350, 0, 100, 25, "\"Page \" + $V{PAGE_NUMBER}", 8, false, HorizontalTextAlignEnum.CENTER, null, null);
        design.setPageFooter(pageFooter);

        return JasperCompileManager.compileReport(design);
    }

    private void addField(JasperDesign design, String name, Class<?> clazz) throws JRException {
        JRDesignField field = new JRDesignField();
        field.setName(name);
        field.setValueClass(clazz);
        design.addField(field);
    }

    private void addStaticText(JRDesignBand band, int x, int y, int w, int h, String text, int fontSize, boolean bold, HorizontalTextAlignEnum align, Color bgColor, Color fgColor) {
        JRDesignStaticText st = new JRDesignStaticText();
        st.setX(x);
        st.setY(y);
        st.setWidth(w);
        st.setHeight(h);
        st.setText(text);
        st.setFontSize((float) fontSize);
        st.setBold(bold);
        st.setHorizontalTextAlign(align);
        st.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        if (bgColor != null) {
            st.setMode(ModeEnum.OPAQUE);
            st.setBackcolor(bgColor);
        }
        if (fgColor != null) {
            st.setForecolor(fgColor);
        }
        band.addElement(st);
    }

    private JRDesignTextField addTextField(JRDesignBand band, int x, int y, int w, int h, String expression, int fontSize, boolean bold, HorizontalTextAlignEnum align, Color bgColor, Color fgColor) {
        JRDesignTextField tf = new JRDesignTextField();
        tf.setX(x);
        tf.setY(y);
        tf.setWidth(w);
        tf.setHeight(h);
        tf.setExpression(new JRDesignExpression(expression));
        tf.setFontSize((float) fontSize);
        tf.setBold(bold);
        tf.setHorizontalTextAlign(align);
        tf.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        if (bgColor != null) {
            tf.setMode(ModeEnum.OPAQUE);
            tf.setBackcolor(bgColor);
        }
        if (fgColor != null) {
            tf.setForecolor(fgColor);
        }
        band.addElement(tf);
        return tf;
    }

    private JasperPrint generateJasperPrint() throws Exception {
        JasperReport jasperReport = buildReport();

        List<Employee> employees = employeeRepository.findAll();
        log.info("Found {} employees for report", employees.size());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("reportTitle", "PT. Bintang Mada Corporation - Employee Data");
        parameters.put("generatedDate",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss")));

        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }

    public byte[] exportPdf() throws Exception {
        log.info("Generating Employee Report (PDF)...");
        JasperPrint jasperPrint = generateJasperPrint();
        byte[] result = JasperExportManager.exportReportToPdf(jasperPrint);
        log.info("PDF report generated successfully, size: {} bytes", result.length);
        return result;
    }

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
