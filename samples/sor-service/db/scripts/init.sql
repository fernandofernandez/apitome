\c sordb


CREATE TABLE account (
                        account_number SERIAL PRIMARY KEY,
                        account_type CHAR(3) NOT NULL,
                        balance NUMERIC(15,3),
                        customer_id INTEGER NOT NULL,
                        creation_date timestamp NOT NULL);

INSERT INTO
    account(account_type, balance, customer_id, creation_date)
VALUES
    ('CHK', 12345.67, 777, '2022-01-21 12:10:00'),
    ('CHK', 1000000.00, 777, '2022-01-21 13:25:00'),
    ('SAV', 50000.00, 38, '2021-12-11 09:15:00');