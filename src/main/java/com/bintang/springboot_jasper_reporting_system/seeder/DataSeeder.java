package com.bintang.springboot_jasper_reporting_system.seeder;

import com.bintang.springboot_jasper_reporting_system.entity.Employee;
import com.bintang.springboot_jasper_reporting_system.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final EmployeeRepository employeeRepository;

    public DataSeeder(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() > 0) {
            log.info("Data already exists, skipping seeder.");
            return;
        }

        List<Employee> employees = List.of(
                new Employee("EMP001", "Ahmad Fauzi", "Engineering", "Senior Developer", new BigDecimal("15000000"), LocalDate.of(2020, 3, 15)),
                new Employee("EMP002", "Siti Nurhaliza", "Human Resources", "HR Manager", new BigDecimal("13000000"), LocalDate.of(2019, 7, 1)),
                new Employee("EMP003", "Budi Santoso", "Finance", "Accountant", new BigDecimal("10000000"), LocalDate.of(2021, 1, 10)),
                new Employee("EMP004", "Dewi Lestari", "Engineering", "Junior Developer", new BigDecimal("8000000"), LocalDate.of(2023, 6, 20)),
                new Employee("EMP005", "Rudi Hermawan", "Marketing", "Marketing Lead", new BigDecimal("12000000"), LocalDate.of(2020, 11, 5)),
                new Employee("EMP006", "Rina Wati", "Engineering", "QA Engineer", new BigDecimal("9500000"), LocalDate.of(2022, 4, 12)),
                new Employee("EMP007", "Joko Widodo", "Operations", "Operations Manager", new BigDecimal("14000000"), LocalDate.of(2018, 9, 1)),
                new Employee("EMP008", "Maya Sari", "Finance", "Finance Analyst", new BigDecimal("11000000"), LocalDate.of(2021, 8, 15)),
                new Employee("EMP009", "Agus Pratama", "Engineering", "DevOps Engineer", new BigDecimal("13500000"), LocalDate.of(2022, 2, 28)),
                new Employee("EMP010", "Linda Kusuma", "Human Resources", "Recruiter", new BigDecimal("7500000"), LocalDate.of(2024, 1, 8))
        );

        employeeRepository.saveAll(employees);
        log.info("Successfully seeded {} employees.", employees.size());
    }
}
