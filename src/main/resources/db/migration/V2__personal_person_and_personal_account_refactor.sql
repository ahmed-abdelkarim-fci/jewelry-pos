CREATE TABLE personal_person (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    address VARCHAR(500) NOT NULL,
    notes VARCHAR(1000),
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP
);

CREATE INDEX idx_personal_person_name ON personal_person(name);

DROP TABLE IF EXISTS personal_account;

CREATE TABLE personal_account (
    id VARCHAR(26) PRIMARY KEY,
    person_id VARCHAR(26) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    statement VARCHAR(1000) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    weight DECIMAL(10,3) DEFAULT 0,
    money DECIMAL(12,2) DEFAULT 0,
    number_of_pieces INTEGER,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES personal_person(id)
);

CREATE INDEX idx_personal_account_person ON personal_account(person_id);
CREATE INDEX idx_personal_account_date ON personal_account(transaction_date DESC);
