FORMAT: 1A

# syringe_test[/]

    Test API for syringe
    
# IService [/iService]

## GET [/iService/get]

+ Model

            {
                "content":"get"
            }
### JUST GET[GET]

+ Response 200

    [GET][]
    
## POST [/iService/post]
+ Model

            {
                "content":"post"
            }
### JUST POST[POST]

+ Response 200

    [POST][]
    
## QUERY [/iService/query{?query}]

+ Model

            {
                "content":"query"
            }
### JUST QUERY[GET]
+ Response 200

    [QUERY][]
    
## QUERYMAP [/iService/querymap{?querymap}]

+ Model

            {
                "content":"querymap"
            }
### JUST QUERYMAP[GET]
+ Response 200

    [QUERYMAP][]
      
## FIELD [/iService/field{?field}]

+ Model

            {
                "content":"field"
            }
### JUST FIELD[POST]
+ Response 200

    [FIELD][]
    
## FIELDMAP [/iService/fieldmap{?fieldmap}]

+ Model

            {
                "content":"fieldmap"
            }
### JUST FieldMAP[POST]
+ Response 200

    [FIELDMAP][]      
    
## PATH [/iService/path/{path}]
+ Parameter
    + path(String)
+ Model

            {
                "content":"path"
            }
### JUST PATH[POST]
+ Response 200

    [PATH][]      
    
## PATHS [/iService/path/{pathone}/{pathtwo}]
+ Parameter
    + pathone(String)
    + pathtwo(String)
+ Model

            {
                "content":"paths"
            }
            
### JUST PATHS[POST]
+ Response 200

    [PATHS][]      
    
