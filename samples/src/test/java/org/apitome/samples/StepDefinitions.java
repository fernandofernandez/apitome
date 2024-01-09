package org.apitome.samples;

import io.cucumber.java.en.*;

import org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    @Given("a customer named {word} {word}")
    public void aCustomerScenario(String customerFirstName, String customerLastName) {

    }

    @When("the customer opens a new account of type {word} in bank {word}")
    public void openNewAccount(String accountType, String bankName) {

    }

    @Then("account creation results in status {int}")
    public void accountCreation(int status) {

    }

    @Given("an example scenario")
    public void anExampleScenario() {
    }

    @When("all step definitions are implemented")
    public void allStepDefinitionsAreImplemented() {
    }

    @Then("the scenario passes")
    public void theScenarioPasses() {
    }
}
