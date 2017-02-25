
# Transport System Guide

**Important fact is that it was my first big project and unfortunately it's not created using neither design patterns nor test driven development**

This project is based on **Java 8** + **Spring boot 1.4** + **Hibernate 5.2** + **Maven 3**. Transport System Guide helps
in planning journeys via public transport. It can find both direct and indirect journeys (However this is not the 
best algorithm and requires much work). The main reason to do this project was learning java and I know that there are many
imperfections but I'm still learning and I hope someday I find them and will never ever make them again :)

Live API preview is available here: http://api-rosiakit.rhcloud.com
And here is sample web application using it: http://transport.rosiak.it/ (I'm not the best in front-end :) )

Live preview contains data about Poznań (data gathered by ZTMPoznanCrawler) and Sroda Wielkopolska (data gathered by KombusCrawler).

## Currently available API endpoints

#### /v1/stops
returns all stops stored in database. [Live example](http://api-rosiakit.rhcloud.com/v1/stops)

This endpoint takes following request params:
* `name` - returns all stops that name starts with provided value. [Live example](http://api-rosiakit.rhcloud.com/v1/stops?name=Dworzec)
* `containing` - returns all stops containing provided value. Note: `containing` is ignored when `name` is set. 

#### /v1/stops/{id}
returns details about selected stop [Live example](http://api-rosiakit.rhcloud.com/v1/stops/1)

#### /v1/stops/nearest/{lat}/{lng}
returns all stops in area of 1 kilometer sorted by distance ascending. [Live example](http://api-rosiakit.rhcloud.com/v1/stops/nearest/52.4653/16.9170)

#### /v1/journey/{stop_id_1}/{stop_id_2}
returns all journeys between {stop_id_1} and {stop_id_2}. Please notice that parameters are stop ids **not names**. [Live example](http://api-rosiakit.rhcloud.com/v1/journey/67/107?&h=10&m=30)

This endpoint takes following request params:
* `h` - Hour of departure (must be combined with "m" param)
* `m` - Minutes of departure (must be combined with "h" param) 
* `daytype` - Possible values: `weekday`, `saturday`, `holiday`
* `avoid` - Lines that should be ignored. Value pattern is: `{agency_name}:{line_name}` for example: `ZTM_MPK:14`

