# Spring Boot Microservice Example (Eureka Server, Config Server, API Gateway, Services , Zipkin, Redis, Docker, Kubernetes)

<img src="screenshots/springbootmicroservices.drawio_image.png" alt="Main Information" width="800" height="900">

# About the project
<ul style="list-style-type:disc">
  <li>This project is based Spring Boot Microservices with the usage of Docker and Kubernetes</li>
  <li>User can register and login through auth service by user role (ADMIN or USER) through api gateway</li>
  <li>User can send any request to relevant service through api gateway with its bearer token</li>
</ul>

7 services whose name are shown below have been devised within the scope of this project.

- Config Server
- Eureka Server
- API Gateway
- Auth Service
- Order Service
- Payment Service
- Product Service

### Explore Rest APIs

<table style="width:100%">
  <tr>
      <th>Method</th>
      <th>Url</th>
      <th>Description</th>
      <th>Valid Request Body</th>
      <th>Valid Request Params</th>
      <th>Valid Request Params and Body</th>
  </tr>
  <tr>
      <td>POST</td>
      <td>authenticate/signup</td>
      <td>Signup for User and Admin</td>
      <td><a href="README.md#signup">Info</a></td>
      <td></td>
      <td></td>
  </tr>
  <tr>
      <td>POST</td>
      <td>authenticate/login</td>
      <td>Login for User and Admin</td>
      <td><a href="README.md#login">Info</a></td>
      <td></td>
      <td></td>
  </tr>
  <tr>
      <td>POST</td>
      <td>authenticate/refreshtoken</td>
      <td>Refresh Token for User and Admin</td>
      <td><a href="README.md#refreshtoken">Info</a></td>
      <td></td>
      <td></td>
  </tr>
  <tr>
      <td>POST</td>
      <td>/product</td>
      <td>Add Product</td>
      <td><a href="README.md#addproduct">Info</a></td>
      <td></td>
      <td></td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/product/{product_id}</td>
      <td>Get Product By Id</td>
      <td></td>
      <td></td>
      <td><a href="README.md#getProductById">Info</a></td>
  </tr>
  <tr>
      <td>PUT</td>
      <td>/reduceQuantity/{product_id}?quantity={quantity_value}</td>
      <td>Reduce Quantity of Product</td>
      <td></td>
      <td><a href="README.md#reduceQuantityOfProduct">Info</a></td>
      <td></td>
  </tr>
  <tr>
      <td>DELETE</td>
      <td>/product/{product_id}</td>
      <td>Delete Prodcut By Id</td>
      <td></td>
      <td></td>
      <td><a href="README.md#deleteProductById">Info</a></td>
  </tr>
  <tr>
      <td>POST</td>
      <td>/order/placeorder</td>
      <td>Place Order</td>
      <td><a href="README.md#placeOrder">Info</a></td>
      <td></td>
      <td></td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/order/{order_id}</td>
      <td>Get Order By Id</td>
      <td></td>
      <td></td>
      <td><a href="README.md#getOrderById">Info</a></td>
  </tr>
  <tr>
      <td>GET</td>
      <td>/payment/order/{order_id}</td>
      <td>Get Payment Details by Order Id</td>
      <td></td>
      <td></td>
      <td><a href="README.md#getPaymentDetailsByOrderId">Info</a></td>
  </tr>

</table>

### Used Dependencies
* Core
    * Spring
        * Spring Boot
        * Spring Boot Test (Junit)
        * Spring Security
        * Spring Web
            * RestTemplate
            * FeighClient
        * Spring Data
            * Spring Data JPA
        * Spring Cloud
            * Spring Cloud Gateway Server
            * Spring Cloud Config Server
            * Spring Cloud Config Client
    * Netflix
        * Eureka Server
        * Eureka Client
* Database
    * Mysql
* Redis
* Zipkin
* Docker
* Kubernetes

## Valid Request Body

##### <a id="signup"> Signup for User and Admin
```
    http://localhost:9090/authenticate/signup
    
    {
        "username" : "User",
        "password" : "User",
        "email" : "user@refreshtoken.com",
        "roles" : [
                "ROLE_USER"
        ]
    }
    
    http://localhost:9090/authenticate/signup
    
    {
        "username" : "admin1",
        "password" : "admin1",
        "email" : "admin1@refreshtoken.com",
        "roles" : [
                "ROLE_ADMIN"
        ]
    }
```

##### <a id="login"> Login for User and Admin
```
    http://localhost:9090/authenticate/login
    
    {
        "username" : "User",
        "password" : "User"
    }
    
    http://localhost:9090/authenticate/login
    
    {
        "username" : "UserAdmin",
        "password" : "UserAdmin"
    }
```

##### <a id="refreshtoken"> Refresh Token for User and Admin
```
    http://localhost:9090/authenticate/refreshtoken
    
    {
        "refreshToken" : ""
    }
```

##### <a id="addProduct"> Add Product
```
    http://localhost:9090/product
    
    {
        "name" : "Product 1",
        "price" : 100,
        "quantity" : 1
    }
    
    Bearer Token : User Token
```

##### <a id="placeorder"> Place Order
```
    http://localhost:9090/order/placeorder
    
    {
        "productId" : 1,
        "totalAmount" : 100,
        "quantity" : 1,
        "paymentMode" : "CASH"
    }
    
    Bearer Token : User Token
```

## Valid Request Params

##### <a id="reduceQuantityOfProduct">Reduce Quantity of Product
```
    http://localhost:9090/product/reduceQuantity/1?quantity=1
    
    Bearer Token : User Token
```

## Valid Request Params and Body

##### <a id="getProductById">Get Product By Id
```
    http://localhost:9090/product/{prodcutId}
    
    Bearer Token : User Token
```

##### <a id="deleteProductById">Delete Product By Id
```
    http://localhost:9090/product/{prodcutId}
    
    Bearer Token : Admin Token
```

##### <a id="deleteProductById">Delete Product By Id
```
    http://localhost:9090/order/{order_id}
    
    Bearer Token : User Token
```

##### <a id="getPaymentDetailsByOrderId">Get Payment Details by Order Id
```
    http://localhost:9090/payment/order/{order_id}
    
    Bearer Token : User Token
```

### 🔨 Run the App

<b>Local</b>

<b>1 )</b> Download your project from this link `https://github.com/Rapter1990/springbootmicroservicedailybuffer`

<b>2 )</b> Go to the project's home directory :  `cd springbootmicroservicedailybuffer`

<b>3 )</b> Run Service Registry (Eureka Server)

<b>4 )</b> Run config server

<b>5 )</b> Run zipkin and redis through these commands shown below on docker
```
    docker run -d -p 9411:9411 openzipkin/zipkin
    docker run -d --name redis -p 6379:6379 redis
```

<b>6 )</b> Run api gateway

<b>7 )</b> Run other services (auth-service, orderservice, paymentservice and lastly productservice)

<b>Docker</b>

<b>1 )</b> Install <b>Docker Desktop</b>. Here is the installation <b>link</b> : https://docs.docker.com/docker-for-windows/install/

<b>2 )</b> Build jar file for all services shown below

<table style="width:100%">
  <tr>
    <th>Service</th>
    <th>Command</th>
  </tr>
  <tr>
    <td>service-registry</td>
    <td>mvn clean install</td>
  </tr>
  <tr>
    <td>configserver</td>
    <td>mvn clean install</td>
  </tr>
  <tr>
    <td>apigateway</td>
    <td>mvn clean install -D skipTests</td>
  </tr>
  <tr>
    <td>auth-service</td>
    <td>mvn clean install -D skipTests</td>
  </tr>
  <tr>
    <td>orderservice</td>
    <td>mvn clean install -D skipTests</td>
  </tr>
  <tr>
    <td>productservice</td>
    <td>mvn clean install -D skipTests</td>
  </tr>
  <tr>
    <td>paymentservice</td>
    <td>mvn clean install -D skipTests</td>
  </tr>
</table>

<b>2 )</b> Run all Containers through this command shown below under main folder
```
    docker-compose up -d
```
<b>3 )</b> Send request to any service by using request collections under <b>postman_collection</b> 

<b>Kubernetes</b>

<b>1 )</b> While running all container on Docker, push docker image of each service to Docker Hub after login your Docker account
```
    1 ) Create serviceregistry
        docker tag microservicedailybuffer/serviceregistry:0.0.1 <your-docker-hub-account-name>/serviceregistry
        docker push <your-docker-hub-account-name>/serviceregistry
        
    2 ) Create configserver
        docker tag microservicedailybuffer/configserver:0.0.1 <your-docker-hub-account-name>/configserver
        docker push <your-docker-hub-account-name>/configserver
    
    3 ) Create apigateway
        docker tag microservicedailybuffer/apigateway:0.0.1 <your-docker-hub-account-name>/apigateway
        docker push <your-docker-hub-account-name>/apigateway
        
    4 ) Create orderservice
        docker tag microservicedailybuffer/orderservice:0.0.1 <your-docker-hub-account-name>/orderservice
        docker push <your-docker-hub-account-name>/orderservice
    
    5 ) Create authservice
        docker tag microservicedailybuffer/orderservice:0.0.1 <your-docker-hub-account-name>/authservice
        docker push <your-docker-hub-account-name>/authservice
        
    6 ) Create productservice
        docker tag microservicedailybuffer/productservice:0.0.1 <your-docker-hub-account-name>/productservice
        docker push <your-docker-hub-account-name>/productservice
        
    7 ) Create paymentservice
        docker tag microservicedailybuffer/paymentservice:0.0.1 <your-docker-hub-account-name>/paymentservice
        docker push <your-docker-hub-account-name>/paymentservice
```

<b>2 )</b> Install <b>minikube</b> to access this link https://minikube.sigs.k8s.io/docs/start/

<b>3 )</b> Open <b>command prompt</b> and install <b>kubectl</b> through this command shown below 
```
    minikube kubectl --
```

<b>4 )</b> Start <b>minikube</b> through this command shown below.
```
    minikube start
```

<b>5 )</b> Open <b>minikube dashboard</b> through this command shown below.
```
    minikube dashboard
```

<b>6 )</b> Run all <b>images</b> coming from Docker hub on Kubernetes through this command shown below.
```
    kubectl apply -f k8s
```
