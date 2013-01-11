CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  userName VARCHAR(50) NOT NULL,
  password VARCHAR(128) NOT NULL,
  firstName VARCHAR(50) NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  employeeId VARCHAR(50) UNIQUE,
  jobTitle VARCHAR(50),
  primaryNotificationMethod VARCHAR(50),
  officePhone VARCHAR(30),
  cellPhone VARCHAR(30),
  email VARCHAR(50) NOT NULL,
  supervisorId INTEGER references users(id),

  facilityId INT REFERENCES facilities(id)
);

CREATE UNIQUE INDEX uc_users_userName ON users(LOWER(userName));
CREATE UNIQUE INDEX uc_users_email ON users(LOWER(email));