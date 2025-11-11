-- RUN THIS IN MYSQL WORKBENCH BEFORE LOGIN IN
-- Common Login Credentials:
-- URL: jdbc:mysql://localhost:3306/business_database?useSSL=false&serverTimezone=UTC
-- Username: root
-- Password: 

CREATE DATABASE `business_database`;

USE `business_database`;

--Core

DROP TABLE IF EXISTS `inspector`;
DROP TABLE IF EXISTS `municipality`;

--Transactions
DROP TABLE IF EXISTS `inspection_schedule`;
DROP TABLE IF EXISTS `inspection_result`;

CREATE TABLE `municipality` (
  `municipality_id` INT NOT NULL AUTO_INCREMENT,
  `municipality_name` VARCHAR(35) NOT NULL,
  `province` VARCHAR(35) NOT NULL,
  `region` VARCHAR(35) NOT NULL,
  `classification` VARCHAR(35),
  `contact_no` VARCHAR(15),
  `office_address` VARCHAR(255),
  PRIMARY KEY (`municipality_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `inspector` (
`inspector_id` INT NOT NULL AUTO_INCREMENT, 
`last_name` VARCHAR(35) NOT NULL DEFAULT '',
`first_name`VARCHAR(35) NOT NULL DEFAULT '',
`designation`VARCHAR(35) NOT NULL DEFAULT '',
`license_no`VARCHAR(35) NOT NULL DEFAULT '',
`active` TINYINT(1) NOT NULL DEFAULT 1,
`municipality_id` INT NOT NULL,
PRIMARY KEY (`inspector_id`),
FOREIGN KEY (`municipality_id`) REFERENCES `municipality`(`municipality_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `inspector`
(`last_name`, `first_name`, `designation`, `license_no`, `active`, `municipality_id`)
VALUES
('Dela Cruz', 'Juan', 'Chief Building Inspector', 'LIC-2023-001', 1, 1),
('Reyes', 'Maria', 'Sanitation Inspector', 'LIC-2023-002', 1, 2),
('Santos', 'Jose', 'Electrical Inspector', 'LIC-2023-003', 1, 3),
('Garcia', 'Ana', 'Mechanical Inspector', 'LIC-2023-004', 1, 4),
('Villanueva', 'Carlos', 'Structural Inspector', 'LIC-2023-005', 1, 5),
('Cruz', 'Emilia', 'Plumbing Inspector', 'LIC-2023-006', 1, 6),
('Torres', 'Roberto', 'Fire Safety Inspector', 'LIC-2023-007', 0, 7),
('Domingo', 'Liza', 'Environmental Inspector', 'LIC-2023-008', 1, 8),
('Perez', 'Mark', 'Building Inspector', 'LIC-2023-009', 1, 9),
('Santiago', 'Angela', 'Occupational Safety Inspector', 'LIC-2023-010', 1, 10);


INSERT INTO `municipality` 
(`municipality_name`, `province`, `region`, `classification`, `contact_no`, `office_address`)
VALUES
('Santa Rosa', 'Laguna', 'Region IV-A (CALABARZON)', 'First Class City', '049-530-0017', 'City Hall, J.P. Rizal Blvd., Santa Rosa, Laguna'),
('San Fernando', 'Pampanga', 'Region III (Central Luzon)', 'First Class City', '045-961-6640', 'City Hall, Sto. Niño, San Fernando, Pampanga'),
('Tagum', 'Davao del Norte', 'Region XI (Davao Region)', 'First Class City', '084-655-9595', 'City Hall, Apokon Road, Tagum City, Davao del Norte'),
('Bayawan', 'Negros Oriental', 'Region VII (Central Visayas)', 'Second Class City', '035-430-0246', 'City Hall, Barangay Banga, Bayawan City, Negros Oriental'),
('Ilagan', 'Isabela', 'Region II (Cagayan Valley)', 'First Class City', '078-624-9518', 'City Hall, Barangay Alibagu, Ilagan City, Isabela'),
('Naga', 'Camarines Sur', 'Region V (Bicol Region)', 'First Class City', '054-473-3442', 'City Hall Compound, J. Miranda Ave., Naga City, Camarines Sur'),
('Bayombong', 'Nueva Vizcaya', 'Region II (Cagayan Valley)', 'First Class Municipality', '078-321-2108', 'Municipal Hall, National Road, Bayombong, Nueva Vizcaya'),
('Taytay', 'Rizal', 'Region IV-A (CALABARZON)', 'First Class Municipality', '02-658-7600', 'Municipal Hall, Rizal Ave., Taytay, Rizal'),
('Maribojoc', 'Bohol', 'Region VII (Central Visayas)', 'Fourth Class Municipality', '038-537-9911', 'Municipal Hall, Poblacion, Maribojoc, Bohol'),
('Laoang', 'Northern Samar', 'Region VIII (Eastern Visayas)', 'Second Class Municipality', '055-251-9302', 'Municipal Hall, Barangay Geracdo, Laoang, Northern Samar');


