CREATE TABLE Users(
   Id_Users SERIAL,
   name VARCHAR(100)  NOT NULL,
   first_name VARCHAR(100)  NOT NULL,
   user_name VARCHAR(100)  NOT NULL,
   email VARCHAR(100)  NOT NULL,
   password TEXT NOT NULL,
   phone_number VARCHAR(50)  NOT NULL,
   role VARCHAR(50),
   PRIMARY KEY(Id_Users),
   UNIQUE(user_name)
);

CREATE TABLE Parking(
   Id_Parking SERIAL,
   label VARCHAR(100)  NOT NULL,
   hourly_rate NUMERIC(15,2)   NOT NULL,
   description TEXT NOT NULL,
   localisation GEOGRAPHY NOT NULL,
   Id_Users INTEGER,
   PRIMARY KEY(Id_Parking),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users)
);

CREATE TABLE Vehicles(
   Id_Vehicles SERIAL,
   types VARCHAR(50)  NOT NULL,
   icon VARCHAR(50)  NOT NULL,
   PRIMARY KEY(Id_Vehicles)
);

CREATE TABLE Parking_vehicles(
   Id_Parking_vehicles SERIAL,
   numbers INTEGER NOT NULL,
   Id_Vehicles INTEGER,
   Id_Parking INTEGER,
   PRIMARY KEY(Id_Parking_vehicles),
   FOREIGN KEY(Id_Vehicles) REFERENCES Vehicles(Id_Vehicles),
   FOREIGN KEY(Id_Parking) REFERENCES Parking(Id_Parking)
);

CREATE TABLE Announcements(
   Id_Announcements SERIAL,
   description TEXT NOT NULL,
   creation_date TIMESTAMP NOT NULL,
   PRIMARY KEY(Id_Announcements)
);

CREATE TABLE Days_week(
   Id_Days_week SERIAL,
   day_name VARCHAR(50)  NOT NULL,
   PRIMARY KEY(Id_Days_week)
);

CREATE TABLE Reservation_status(
   Id_Reservation_status SERIAL,
   label VARCHAR(50)  NOT NULL,
   value_ INTEGER NOT NULL,
   PRIMARY KEY(Id_Reservation_status)
);

CREATE TABLE Announcements_vehicles(
   Id_Announcements_vehicles SERIAL,
   numbers INTEGER NOT NULL,
   Id_Announcements INTEGER,
   Id_Parking_vehicles INTEGER,
   PRIMARY KEY(Id_Announcements_vehicles),
   FOREIGN KEY(Id_Announcements) REFERENCES Announcements(Id_Announcements),
   FOREIGN KEY(Id_Parking_vehicles) REFERENCES Parking_vehicles(Id_Parking_vehicles)
);

CREATE TABLE Reservation_delay(
   Id_Reservation_delay SERIAL,
   delay_in_hours INTEGER NOT NULL,
   delay_in_minutes INTEGER NOT NULL,
   creation_date TIMESTAMP,
   PRIMARY KEY(Id_Reservation_delay)
);

CREATE TABLE Global_commission(
   Id_Global_commission SERIAL,
   rate DOUBLE PRECISION,
   creation_date TIMESTAMP,
   PRIMARY KEY(Id_Global_commission)
);

CREATE TABLE Commission_vehicles(
   Id_Commission_vehicles SERIAL,
   rate DOUBLE PRECISION,
   creation_date TIMESTAMP,
   Id_Vehicles INTEGER,
   PRIMARY KEY(Id_Commission_vehicles),
   FOREIGN KEY(Id_Vehicles) REFERENCES Vehicles(Id_Vehicles)
);

CREATE TABLE Commission_partners(
   Id_Commission_partners SERIAL,
   rate DOUBLE PRECISION NOT NULL,
   creation_date TIMESTAMP,
   Id_Users INTEGER,
   PRIMARY KEY(Id_Commission_partners),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users)
);

CREATE TABLE Profil_types(
   Id_Profil_types SERIAL,
   types VARCHAR(50)  NOT NULL,
   PRIMARY KEY(Id_Profil_types)
);

CREATE TABLE Parking_note(
   Id_Parking_note SERIAL,
   note NUMERIC(6,2)   NOT NULL,
   Id_Users INTEGER,
   Id_Parking INTEGER,
   PRIMARY KEY(Id_Parking_note),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users),
   FOREIGN KEY(Id_Parking) REFERENCES Parking(Id_Parking)
);

CREATE TABLE Payment_status(
   Id_Payment_status SERIAL,
   label VARCHAR(50)  NOT NULL,
   value_ INTEGER NOT NULL,
   PRIMARY KEY(Id_Payment_status)
);

CREATE TABLE Commission_types(
   Id_Commission_types SERIAL,
   label VARCHAR(50) ,
   PRIMARY KEY(Id_Commission_types)
);

CREATE TABLE Profils(
   Id_Profils SERIAL,
   Id_Profil_types INTEGER,
   Id_Users INTEGER,
   PRIMARY KEY(Id_Profils),
   FOREIGN KEY(Id_Profil_types) REFERENCES Profil_types(Id_Profil_types),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users)
);

CREATE TABLE Reservation(
   Id_Reservation SERIAL,
   total_price NUMERIC(15,2)   NOT NULL,
   creation_date TIMESTAMP NOT NULL,
   payement_date TIMESTAMP,
   start_datetime TIMESTAMP NOT NULL,
   end_datetime TIMESTAMP NOT NULL,
   payment_method VARCHAR(50),
   Id_Users INTEGER,
   Id_Reservation_status INTEGER,
   PRIMARY KEY(Id_Reservation),
   FOREIGN KEY(Id_Users) REFERENCES Users(Id_Users),
   FOREIGN KEY(Id_Reservation_status) REFERENCES Reservation_status(Id_Reservation_status)
);

CREATE TABLE Reservation_vehicles(
   Id_Reservation_vehicles SERIAL,
   numbers INTEGER NOT NULL,
   Id_Reservation INTEGER,
   Id_Announcements_vehicles INTEGER,
   PRIMARY KEY(Id_Reservation_vehicles),
   FOREIGN KEY(Id_Reservation) REFERENCES Reservation(Id_Reservation),
   FOREIGN KEY(Id_Announcements_vehicles) REFERENCES Announcements_vehicles(Id_Announcements_vehicles)
);

CREATE TABLE Commission_received(
   Id_Commission_received SERIAL,
   price NUMERIC(15,2)  ,
   payement_date TIMESTAMP,
   Id_Commission_types INTEGER,
   Id_Reservation INTEGER,
   Id_Payment_status INTEGER,
   PRIMARY KEY(Id_Commission_received),
   FOREIGN KEY(Id_Commission_types) REFERENCES Commission_types(Id_Commission_types),
   FOREIGN KEY(Id_Reservation) REFERENCES Reservation(Id_Reservation),
   FOREIGN KEY(Id_Payment_status) REFERENCES Payment_status(Id_Payment_status)
);

CREATE TABLE Availabilities_date(
   Id_Availabilities_date SERIAL,
   start_hour TIME NOT NULL,
   start_date DATE NOT NULL,
   end_date DATE NOT NULL,
   end_hour TIME NOT NULL,
   Id_Reservation_vehicles INTEGER,
   Id_Announcements_vehicles INTEGER,
   PRIMARY KEY(Id_Availabilities_date),
   FOREIGN KEY(Id_Reservation_vehicles) REFERENCES Reservation_vehicles(Id_Reservation_vehicles),
   FOREIGN KEY(Id_Announcements_vehicles) REFERENCES Announcements_vehicles(Id_Announcements_vehicles)
);

CREATE TABLE Availabilities_frequence(
   Id_Availabilities_frequence SERIAL,
   start_hour TIME NOT NULL,
   end_hour TIME NOT NULL,
   Id_Reservation_vehicles INTEGER,
   Id_Announcements_vehicles INTEGER,
   Id_Days_week INTEGER,
   PRIMARY KEY(Id_Availabilities_frequence),
   FOREIGN KEY(Id_Reservation_vehicles) REFERENCES Reservation_vehicles(Id_Reservation_vehicles),
   FOREIGN KEY(Id_Announcements_vehicles) REFERENCES Announcements_vehicles(Id_Announcements_vehicles),
   FOREIGN KEY(Id_Days_week) REFERENCES Days_week(Id_Days_week)
);
