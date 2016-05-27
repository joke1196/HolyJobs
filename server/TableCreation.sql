CREATE TABLE Types
(
  typeId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  typeName VARCHAR(50)
);

CREATE TABLE Regions
(
  regionId INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  regionName VARCHAR(50)
);
CREATE TABLE Jobs
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
  CONSTRAINT  FOREIGN KEY (region)
  REFERENCES Regions(regionId),
  CONSTRAINT  FOREIGN KEY (jobType)
  REFERENCES Types(typeId)
);

INSERT INTO Types (typeName) VALUES ('Computer Science');
INSERT INTO Types (typeName) VALUES ('Maintenance Job');
INSERT INTO Types (typeName) VALUES ('Babysitting');


INSERT INTO Regions (regionName) VALUES ('Vaud');
INSERT INTO Regions (regionName) VALUES ('Geneva');
INSERT INTO Regions (regionName) VALUES ('Zurich');
INSERT INTO Regions (regionName) VALUES ('Fribourg');

INSERT INTO Jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email)
VALUES ('Computer Trainee', 'Work Hard get paid.' ,'2016-06-08', '2022-06-30', '1', '1','25','100', 'jobs@logitech.ch');
INSERT INTO Jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email)
VALUES ('Data Scientist', 'Work Harder get paid but at google.','2016-06-30', '2016-07-27', '1', '3','55', '50', 'jobs@google.ch');
INSERT INTO Jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email)
VALUES ('Floor Technician', 'Clean stuff and be proud. Your mom will be!', '2016-07-1', '2016-09-30', '2', '2','5', '80', 'jobs@cern.ch');
INSERT INTO Jobs (name, description, startDate, endDate, jobType, region, hourlyPay, workingTime, email)
VALUES ('Taking Care of the youngsters', 'First year need attention and care, please be patient!','2016-06-30', '2022-07-27', '3', '1','55', '50', 'jobs@heig.ch');
