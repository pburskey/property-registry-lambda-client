package com.burskey.property;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.burskey.property.api.APIGatewayHelper;
import com.burskey.property.api.AWSClientConfig;
import com.burskey.property.api.PropertyClient;
import com.burskey.property.api.UploadToS3;
import com.burskey.property.domain.Property;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class StepDefinitions {

    String region = null;
    String accessKey = null;
    String secretAccessKey = null;
    String bucketName = null;
    String s3Bucket = null;
    String stackName = null;
    String stage = null;
    String saveURI = null;
    String getByIDURI = null;
    Property property = null;
    List<Property> foundProperties = null;
    private ResponseEntity response;
    private PropertyClient client;
    private String getByCategoryAndNameURI;
    private List<Property> listOfPropertiesToUpload;
    private PutObjectResult uploadResult;

    @Given("an AWS Region: {string}")
    public void an_aws_region(String string) {
        this.region = string;
    }

    @Given("an AWS S3 Bucket: {string}")
    public void an_aws_s3_bucket(String string) {
        this.s3Bucket = string;
    }

    @Given("an AWS Stack Name: {string}")
    public void an_aws_stack_name(String string) {
        this.stackName = string;
    }

    @Given("an AWS Stage: {string}")
    public void an_aws_stage(String string) {
        this.stage = string;
    }


    @Given("a property save uri: {string}")
    public void a_property_save_uri(String string) {
        this.saveURI = string;
    }

    @Given("a property get by id uri: {string}")
    public void a_property_get_by_id_uri(String string) {
        this.getByIDURI = string;
    }


    @Given("that I want to save a property")
    public void that_i_want_to_save_a_property(io.cucumber.datatable.DataTable dataTable) {

        if (dataTable != null && !dataTable.isEmpty()) {

            List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
            Property aProperty = new Property();
            for (Map<String, String> columns : rows) {
                aProperty.setName(columns.get("Name"));

                aProperty.setValue(columns.get("Value"));

                aProperty.setDescription(columns.get("Description"));

                aProperty.setCategory(columns.get("Category"));


            }
            this.property = aProperty;


        }

    }

    @When("I ask the service to save the property")
    public void i_ask_the_service_to_save_the_property() {
        this.response = client.save(this.property);
    }

    @Then("the service responds with status code: {int}")
    public void the_service_responds_with_status_code(Integer int1) {
        assertNotNull(this.response);
        int status = this.response.getStatusCode().value();
        assertEquals(int1, Integer.valueOf(status));
    }

    @Then("the property has an id")
    public void the_property_has_an_id() {
        assertNotNull(this.property.getId());
    }


    @Given("a saved property")
    public void a_saved_property() {
        Property property = new Property();
        property.setValue("a value");
        property.setDescription("a description");
        property.setCategory(java.util.UUID.randomUUID().toString());
        property.setName(java.util.UUID.randomUUID().toString());
        this.property = property;
        this.i_ask_the_service_to_save_the_property();
    }

    @When("I ask the service to find the property by ID")
    public void i_ask_the_service_to_find_the_property_by_id() throws JsonProcessingException {

        this.response = client.findByID(this.property.getId());
        ObjectMapper mapper = new ObjectMapper();
        this.property = mapper.readValue((String) (this.response.getBody()), Property.class);
    }

    @Then("the property exists")
    public void the_property_exists() {
        assertNotNull(this.property);
    }

    @When("I ask the service to find the property by category and name")
    public void i_ask_the_service_to_find_the_property_by_category_and_name() throws JsonProcessingException {
        this.response = client.findByCategoryAndName(this.property.getCategory(), this.property.getName());
        ObjectMapper mapper = new ObjectMapper();
        Object anObject = mapper.readValue((String) (this.response.getBody()), List.class);
        if (anObject != null) {
            List aList = (List) anObject;
            for (Object o : aList) {
                Map aMap = (Map) o;
                this.property = mapper.convertValue(aMap, Property.class);
            }
        }
    }

    @Given("an AWS Client")
    public void an_aws_client() {
        PropertyClient client = PropertyClient.Builder()
                .withSave(this.saveURI)
                .withGetByID(this.getByIDURI)
                .withGetByCategoryAndName(this.getByCategoryAndNameURI);
        this.client = client;
    }

    @Given("a property get by category and name uri: {string}")
    public void a_property_get_by_category_and_name_uri(String string) {
        this.getByCategoryAndNameURI = string;
    }

    @When("I bootstrap the URIs")
    public void i_bootstrap_the_uris() {
        assertNotNull(this.stage);

        AWSClientConfig config = new AWSClientConfig(this.accessKey, this.secretAccessKey, this.bucketName, this.region);
        APIGatewayHelper helper = APIGatewayHelper.With(config);
        String baseURI = helper.constructBaseURIForEnvironment("Basic AWS Api Gateway", this.stage);

        LambdaResourceLoader loader = LambdaResourceLoader.BuildUsingBaseURI(baseURI);
        this.saveURI = loader.get(LambdaResourceLoader.Thing.Save);
        this.getByIDURI = loader.get(LambdaResourceLoader.Thing.FindByID);
        this.getByCategoryAndNameURI = loader.get(LambdaResourceLoader.Thing.FindByCategoryAndName);

    }

    @Then("I have a save uri")
    public void i_have_a_save_uri() {
        assertNotNull(this.saveURI);
    }

    @Then("I have a find by id uri")
    public void i_have_a_find_by_id_uri() {
        assertNotNull(this.getByIDURI);
    }

    @Then("I have a find by category and name uri")
    public void i_have_a_find_by_category_and_name_uri() {
        assertNotNull(this.getByCategoryAndNameURI);
    }


    @Given("that I want to upload a json file containing")
    public void that_i_want_to_upload_a_json_file_containing(io.cucumber.datatable.DataTable dataTable) {
        if (dataTable != null && !dataTable.isEmpty()) {
            this.listOfPropertiesToUpload = new ArrayList<>();

            List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

            for (Map<String, String> columns : rows) {
                Property aProperty = new Property();
                aProperty.setName(columns.get("Name"));
                aProperty.setValue(columns.get("Value"));
                aProperty.setDescription(columns.get("Description"));
                aProperty.setCategory(columns.get("Category"));
                this.listOfPropertiesToUpload.add(aProperty);
            }

        }
    }

    @When("I ask the service to find the properties by category: {string}")
    public void i_ask_the_service_to_find_the_properties_by_category(String string) throws Exception {
        this.response = client.findByCategory(string);
        ObjectMapper mapper = new ObjectMapper();
        Object anObject = mapper.readValue((String) (this.response.getBody()), List.class);
        if (anObject != null) {
            this.foundProperties = new ArrayList<Property>();
            List aList = (List) anObject;
            for (Object o : aList) {
                Map aMap = (Map) o;
                this.foundProperties.add(mapper.convertValue(aMap, Property.class));
            }
        }
    }

    @Then("the service has responded with: {int} properties")
    public void the_service_has_responded_with_properties(Integer int1) {
        assertNotNull(this.foundProperties);
        assertTrue(int1.intValue() <= this.foundProperties.size());
    }

    @Then("each property can be found using category and name")
    public void each_property_can_be_found_using_category_and_name() throws Exception {
        assertNotNull(this.foundProperties);
        assertNotNull(this.listOfPropertiesToUpload);
        for (int i = 0; i < this.listOfPropertiesToUpload.size(); i++) {
            Property aProperty = this.listOfPropertiesToUpload.get(i);
            assertNotNull(aProperty);
            ResponseEntity entity = client.findByCategoryAndName(aProperty.getCategory(), aProperty.getName());
            ObjectMapper mapper = new ObjectMapper();
            Object anObject = mapper.readValue((String) (entity.getBody()), List.class);
            assertNotNull(anObject);

        }
    }

    @Given("an AWS Bucket Name: {string}")
    public void an_aws_bucket_name(String string) {
        this.bucketName = string;
    }

    @Given("an IAM Access Key: {string}")
    public void an_iam_access_key(String string) {
        this.accessKey = string;
    }

    @Given("an IAM Secret Access Key: {string}")
    public void an_iam_secret_access_key(String string) {
        this.secretAccessKey = string;
    }


    @When("I ask the service to upload the file with name: {string}")
    public void i_ask_the_service_to_upload_the_file_with_name(String string) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(this.listOfPropertiesToUpload);

        this.uploadResult = UploadToS3.With(this.accessKey, this.secretAccessKey, this.bucketName, this.region).upload(json, string);

    }

    @Then("the file exists")
    public void the_file_exists() {
        assertNotNull(this.uploadResult);
        assertNotNull(this.uploadResult);
    }


    @Given("an environment provided AWS Bucket Name")
    public void an_environment_provided_aws_bucket_name() {
        this.bucketName = System.getenv("AWS_BUCKET_NAME");
    }
    @Given("an environment provided IAM Access Key")
    public void an_environment_provided_iam_access_key() {
        this.accessKey = System.getenv("AWS_ACCESS_KEY");
    }
    @Given("an environment provided IAM Secret Access Key")
    public void an_environment_provided_iam_secret_access_key() {
        this.secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
    }
    @When("I wait for {int} seconds")
    public void i_wait_for_seconds(Integer int1) throws Exception {
        Thread.sleep(int1 * 1000);
    }
    @Then("the response has a message: {string}")
    public void the_response_has_a_message(String string) {
        assertNotNull(this.response);
        assertEquals(string, this.response.getBody());
    }


}
