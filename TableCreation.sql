CREATE DATABASE IF NOT EXISTS holyjobs;
USE holyjobs;

CREATE TABLE `types`
(
  typeId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  typeName VARCHAR(50)
);

CREATE TABLE regions
(
  regionId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  regionName VARCHAR(50)
);
CREATE TABLE jobs
(
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50),
  description LONGTEXT,
  startDate DATE,
  endDate DATE,
  jobType INT,
  region INT,
  hourlyPay REAL,
  workingTime INT,
  email VARCHAR(100),
  img VARCHAR(100),
  CONSTRAINT  FOREIGN KEY (region)
  REFERENCES regions(regionId),
  CONSTRAINT  FOREIGN KEY (jobType)
  REFERENCES `types`(typeId)
);

INSERT INTO `types` (typeName) VALUES ('Computer Science');
INSERT INTO `types` (typeName) VALUES ('Maintenance Job');
INSERT INTO `types` (typeName) VALUES ('Babysitting');
INSERT INTO `types` (typeName) VALUES ('Animals Subsequent');
INSERT INTO `types` (typeName) VALUES ('Sales');

INSERT INTO regions (regionName) VALUES ('Vaud');
INSERT INTO regions (regionName) VALUES ('Geneva');
INSERT INTO regions (regionName) VALUES ('Zurich');
INSERT INTO regions (regionName) VALUES ('Fribourg');

INSERT INTO jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email, img)
VALUES ('Computer Trainee', 'Work Hard get paid.' ,'2016-07-09', '2022-06-30', '1', '1','25','100', 'jobs@logitech.ch', 'job1.jpg');
INSERT INTO jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email, img)
VALUES ('Data Scientist', 'Work Harder get paid but at google.','2016-06-30', '2016-07-27', '1', '3','55', '50', 'jobs@google.ch', 'default.jpg');
INSERT INTO jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email, img)
VALUES ('Floor Technician', 'Clean stuff and be proud. Your mom will be!', '2016-07-1', '2016-09-30', '2', '2','5', '80', 'jobs@cern.ch','default.jpg');
INSERT INTO jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email, img)
VALUES ('Taking Care of the youngsters', 'First year need attention and care, please be patient!','2016-06-30', '2022-07-27', '3', '1','55', '50', 'jobs@heig.ch', 'default.jpg');
INSERT INTO jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email, img)
VALUES ('Redneck Cowboy', 'That cow needs to be milked!', '2016-07-09', '2022-06-30', '4', '1', '12.5', '40', 'jobs@logitech.ch', 'job2.jpg');
INSERT INTO jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email, img)
VALUES ('Toothpaste Seller', 'Time to be useful buddy!', '2016-07-09', '2022-06-30', '5', '1', '5', '300', 'jobs@logitech.ch', 'job3.jpg');
INSERT INTO jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email, img)
VALUES ('Duck Feeder', 'Ducks have hearts too, you know :(.', '2016-07-09', '2022-06-30', '4', '1', '1.5', '5', 'jobs@logitech.ch', 'job4.jpg');
