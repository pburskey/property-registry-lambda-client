Feature: Sunny Day

  Background:
    Given an AWS Stage: "dev"
#    Given a property save uri: "https://zeb8w5qk26.execute-api.us-east-2.amazonaws.com/Stage/save"
#    Given a property get by id uri: "https://zeb8w5qk26.execute-api.us-east-2.amazonaws.com/Stage/"
#    Given a property get by category and name uri: "https://zeb8w5qk26.execute-api.us-east-2.amazonaws.com/Stage/find"
    Given an environment provided AWS Bucket Name
    Given an environment provided IAM Access Key
    Given an environment provided IAM Secret Access Key
    Given an AWS Region: "us-east-2"
    When I bootstrap the URIs
    Then I have a save uri
    Then I have a find by id uri
    Then I have a find by category and name uri
    Given an AWS Client

  Scenario: Simple Save Property
    Given that I want to save a property
      | Name  | Value  | Description  | Category  |
      | aName | aValue | aDescription | aCategory |
    When I ask the service to save the property
    Then the service responds with status code: 200
    And the property has an id


  Scenario: Simple Find Property By ID
    Given a saved property
    When I ask the service to find the property by ID
    Then the service responds with status code: 200
    And the property exists

  Scenario: Simple Find Property By category and name
    Given a saved property
    When I ask the service to find the property by category and name
    Then the service responds with status code: 200
    And the property exists


  Scenario: Simple Upload json to S3
    Given an AWS Region: "us-east-2"

    Given that I want to upload a json file containing
      | Name | Value | Description | Category |
      | a    | 1     | a1          | test     |
      | b    | 2     | b2          | test     |
      | c    | 3     | c3          | test     |
      | d    | 4     | d4          | test     |
      | e    | 5     | e5          | test     |
    When I ask the service to upload the file with name: "test.json"
    Then the file exists
    When I wait for 10 seconds
    When I ask the service to find the properties by category: "test"
    Then the service responds with status code: 200
    And the service has responded with: 5 properties
    And each property can be found using category and name


  @me
  Scenario: Simple Save Empty Property
    Given that I want to save a property
      | Name | Value | Description | Category |
    When I ask the service to save the property
    Then the service responds with status code: 400
    Then the response has a message: "Name is missing"


  @me
  Scenario: Simple Save Empty Category
    Given that I want to save a property
      | Name | Value | Description | Category |
      | a    | a     | a           |          |
    When I ask the service to save the property
    Then the service responds with status code: 400
    Then the response has a message: "Category is missing"


  @me
  Scenario: Simple Save Empty Value
    Given that I want to save a property
      | Name | Value | Description | Category |
      | a    |       | a           | a        |
    When I ask the service to save the property
    Then the service responds with status code: 400
    Then the response has a message: "Value is missing"