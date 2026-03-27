package com.bintang.springboot_jasper_reporting_system.repository;

import com.bintang.springboot_jasper_reporting_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
