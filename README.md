# Micro Widget Task

# Start service
```
mvn spring-boot:run
```

# Run tests
```
mvn clean install
```

## 1. Get widget

```js
+ GET /api/widgets/{widgetId}

+ Response
{
  "widgetId": "7783a5e9-1f98-4eed-98a6-a7bf78718ea8", 
  "widgetName": "Widget_1",
  "coordinateX": 10,
  "coordinateY": 2,
  "coordinateZ": 4,
  "width": 2,
  "height": 6,
  "createdAt": 1641641844904,
  "updatedAt": 1641641844904
}
```

## 2. Get widgets

```js
+ GET /api/widgets

+ Response
[
  {
    "widgetId": "7783a5e9-1f98-4eed-98a6-a7bf78718ea8",
    "widgetName": "Widget_1",
    "coordinateX": 40,
    "coordinateY": 50,
    "coordinateZ": 20,
    "width": 50,
    "height": 50,
    "createdAt": 1641641844904,
    "updatedAt": 1641631580474
  },
  {
    "widgetId": "09a27b39-9e2d-4a11-9dd6-ee3bcc316770",
    "widgetName": "Widget_2",
    "coordinateX": 50,
    "coordinateY": 100,
    "coordinateZ": 30,
    "width": 100,
    "height": 100,
    "createdAt": 1641642786267,
    "updatedAt": 1641642786267
  }
]
```

## 3. Create widget

```js
+ POST /api/widgets

+ Header
  Content-Type: application/json

+ Body
{
  "widgetName": "Widget_1", 
  "coordinateX": "10",
  "coordinateY": "2",
  "coordinateZ": "4",
  "width": "2",
  "height": "6"
}

+ Response
{
  "widgetId": "7783a5e9-1f98-4eed-98a6-a7bf78718ea8",
  "widgetName": "Widget_1",
  "coordinateX": 10,
  "coordinateY": 2,
  "coordinateZ": 4,
  "width": 2,
  "height": 6,
  "createdAt": 1641641844904,
  "updatedAt": 1641641844904
}
```
## 4. Update widget

```js
+ PATCH /api/widgets/{widgetId}

+ Header
  Content-Type: application/json

+ Body
{
  "widgetName": "Widget_1", 
  "coordinateX": "10",
  "coordinateY": "2",
  "coordinateZ": "4",
  "width": "2",
  "height": "6"
}

+ Response
{
  "widgetId": "7783a5e9-1f98-4eed-98a6-a7bf78718ea8",
  "widgetName": "Widget_1",
  "coordinateX": 10,
  "coordinateY": 2,
  "coordinateZ": 4,
  "width": 2,
  "height": 6,
  "createdAt": 1641641844904,
  "updatedAt": 1641641844904
}
```
## 5. Delete widget

```js
+ DELETE /api/widgets/{widgetId}

+ Response 200 OK
```