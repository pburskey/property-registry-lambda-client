package com.burskey.property;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber.html"}
        , dryRun = false
//        , tags = "@me"
)
public class CucumberRunnerIT {
}
