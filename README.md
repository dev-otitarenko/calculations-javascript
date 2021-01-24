# Description

Running simple jscript functions on server side.

Input in JSON format:
```
{
"Field1": 100,
"Field2": 20,
"Field3": 300
}
```
Example calculation formulas:
```sh
Field100 = Field1 +^Field2
Field101 = Field1 - Field2
Field102 = Field1*Field3 + Field2
Field103 = (Field1 + Field2) / Field3
Field200 = Min.MAX(Field1, Field2, Field3)
Field201 = Min.MIN(Field1, Field2, Field3)
Field202 = Min.SUM(Field1, Field2, Field3)
Field203 = Min.AVG(Field1, Field2, Field3)
```