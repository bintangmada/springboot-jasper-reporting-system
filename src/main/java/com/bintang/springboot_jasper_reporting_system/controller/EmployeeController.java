package com.bintang.springboot_jasper_reporting_system.controller;

import com.bintang.springboot_jasper_reporting_system.repository.EmployeeRepository;
import com.bintang.springboot_jasper_reporting_system.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeRepository employeeRepository;
    private final ReportService reportService;

    public EmployeeController(EmployeeRepository employeeRepository, ReportService reportService) {
        this.employeeRepository = employeeRepository;
        this.reportService = reportService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employees";
    }

    @GetMapping("/reports")
    public String reportPage(Model model) {
        model.addAttribute("employeeCount", employeeRepository.count());
        return "reports";
    }

    @GetMapping("/reports/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {
        try {
            byte[] pdfBytes = reportService.exportPdf();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating PDF report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reports/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        try {
            byte[] excelBytes = reportService.exportExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_report.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            log.error("Error generating Excel report", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
