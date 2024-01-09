Feature: Account creation
  As a customer
  I want to be able to create different types of accounts
  So that my banking needs are met

  Scenario Outline: Successful account creation
    Given a customer named <customer_first_name> <customer_last_name>
    When the customer opens a new account of type <account_type> in bank <bank>
    Then account creation results in status <status>

  Examples:
    | bank | account_type | customer_first_name | customer_last_name | status |
    | C1   | checking     | John                | Doe                | 201    |