# ABN AMRO BACKEND ASSESSMENT
## Recipe API
This is an api to create, search, update and delete recipes

## Tools
- Java 11
- Maven
- SpringBoot
- File based database (H2)
- JPA/Hibernate

### Recipe Data Model

```json
{
  "name": "Vegan Cake",
  "ingredient": [
    {
      "name": "butter",
      "variation": "Non sticky",
      "quantity": 1.5,
      "unit": "kg"
    },
    {
      "name": "flour",
      "variation": "Wheat",
      "quantity": 3,
      "unit": "kg"
    }
  ],
  "noOfServing": 10,
  "servingSize": 4,
  "type": "VEGAN",
  "instructions": "Turn butter, mix flour, bake for 40 Minutes"
}
```
### Run
- mvn clean verify
- mvn spring-boot:run
### Available Endpoints
- #### Azure URL: https://recipeapiabn.azurewebsites.net/
- #### [Postman Collection File](Recipe.postman_collection.json)
- #### [Postman Collection Link](https://www.getpostman.com/collections/abefa0738dc6b7b3ae99)
- #### Search Endpoint: http://localhost:8080/recipe/search?search_term=name:Searchname
Search_term has a special notation 
- *property_name* 
- *operation* 
- *value*
##### Property name
This corresponds to the name of the recipe property to search exactly as on the JSON.
For ingredient sub properties the *property_name* should be separated by underscore e.g. ingredient_quantity

Valid property names: name, noOfServing, servingSize, ingredient_name, ingredient_unit e.t.c.

##### Operation
This is the search operation you wish to perform. Example

- : = Equals
- ! = Not Equals
- '>' = Greater than
- '>' = Less than
- ~ = Like
##### Value
This is the value of the search you are making. 

Put asterisks(*) around a value while using equality operation to search is a string field contains the search term e.g. name:*omlet*

Put asterisks(*) in front of a value while using equality operation to search is a string field ENDS WITH the search term e.g. name:*let

Put asterisks(*) at the back of a value while using equality operation to search is a string field STARTS WITH the search term e.g. name:om*
