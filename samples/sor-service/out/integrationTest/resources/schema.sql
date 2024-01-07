CREATE TABLE account (
                        account_number SERIAL PRIMARY KEY,
                        account_type CHAR(3) NOT NULL,
                        balance NUMERIC(15,3),
                        customer_id INTEGER NOT NULL,
                        creation_date timestamp NOT NULL);
