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
  startDate DATE,
  endDate DATE,
  jobType INT,
  region INT,
  CONSTRAINT  FOREIGN KEY (region)
  REFERENCES Regions(regionId),
  CONSTRAINT  FOREIGN KEY (jobType)
  REFERENCES Types(typeId)
);

INSERT INTO Types (typeName) VALUES ('Computer Science');

INSERT INTO Regions (regionName) VALUES ('Vaud');

INSERT INTO Jobs (name, startDate, endDate, jobType, region)
VALUES ('Computer Trainee', '2016-06-08', '2022-06-30', '1', '1');
