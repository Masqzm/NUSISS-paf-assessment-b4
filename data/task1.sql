CREATE DATABASE bedandbreakfast;

USE bedandbreakfast;

CREATE USER 'fred'@'%'
identified BY 'qweasd';

GRANT ALL PRIVILEGES ON bedandbreakfast.* 
TO 'fred'@'%';

flush PRIVILEGES;


CREATE TABLE users (
	email varchar(128) NOT NULL,
	name varchar(128) NOT NULL,
	CONSTRAINT pk_email PRIMARY KEY (email)
);

CREATE TABLE bookings (
	booking_id char(8) NOT NULL,
	listing_id varchar(20) NOT NULL,
	duration int NOT NULL,
	email varchar(128) NOT NULL,
	CONSTRAINT pk_booking_id PRIMARY KEY (booking_id),
	CONSTRAINT fk_email FOREIGN KEY (email) REFERENCES users(email)
);

CREATE TABLE reviews (
	id int NOT NULL AUTO_INCREMENT,
	date datetime NOT NULL,
	listing_id varchar(20) NOT NULL,
	reviewer_name varchar(64) NOT NULL,
	comments text NOT NULL,
	CONSTRAINT pk_review_id PRIMARY KEY (id)
);

INSERT INTO users(email, name) VALUES 
('fred@gmail.com', 'Fred Flintstone'),
('barney@gmail.com', 'Barney Rubble'),
('fry@planetexpress.com', 'Philip J Fry'),
('hlmer@gmail.com', 'Homer Simpson');


# Find location to upload users.csv so we can place it there to import the data
SHOW VARIABLES LIKE 'secure_file_priv';

#DELETE FROM users;		# Clears users table
#LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/users.csv' INTO TABLE users
LOAD DATA INFILE 'D:/Work stuff/Tools/MySQL/data/Uploads/users.csv' 
	INTO TABLE users
  	FIELDS TERMINATED BY ', ' #ENCLOSED BY '"'
  	LINES TERMINATED BY '\n'
  	IGNORE 1 LINES;


SELECT * FROM users;


SELECT * FROM bookings;
SELECT * FROM reviews;

# Task 6
INSERT INTO users(email, name) VALUES ('test@gmail.com', 'test');

# Error test - fails as User with email (test@gmail.com) doesnt exist in referenced table Users 
INSERT INTO bookings(booking_id, listing_id, duration, email) VALUES ('12345678', '55555', 2, 'test@gmail.com');
# Passes as User with email (fred@gmail.com) exists in referenced table Users  
INSERT INTO bookings(booking_id, listing_id, duration, email) VALUES ('12345678', '55555', 2, 'fred@gmail.com');