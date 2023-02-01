pipeline {
    agent any
    tools {
        maven "Maven 3.6.3"
    }
    stages {
        stage("build project") {
            steps {
                // git 'https://github.com/Rapter1990/springbootmicroservicedailybuffer'
                echo "Java VERSION"
                sh 'java --version'
                echo "Maven VERSION"
                sh 'mvn --version'
                echo 'building project...'

                dir("service-registry") {
                  sh "mvn clean install"
                }

                dir("configserver") {
                  sh "mvn clean install"
                }

                dir("apigateway") {
                  sh "mvn clean install -DskipTests"
                }

                dir("auth-service") {
                  sh "mvn clean install -DskipTests"
                }

                dir("productservice") {
                  sh "mvn clean install -DskipTests"
                }

                dir("orderservice") {
                  sh "mvn clean install -DskipTests"
                }

                dir("paymentservice") {
                  sh "mvn clean install -DskipTests"
                }
            }
        }
    }
}