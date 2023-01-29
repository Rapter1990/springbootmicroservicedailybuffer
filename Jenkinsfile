pipeline {
    agent any
    tools {
        maven "Maven 3.6.3"
    }
    stages {
        stage("build project") {
            steps {
                git 'https://github.com/Rapter1990/springbootmicroservicedailybuffer'
                echo "Java VERSION"
                sh 'java --version'
                echo "Maven VERSION"
                sh 'mvn --version'
                echo 'building project...'
                sh "mvn clean --file *.pom"
                sh "mvn clean install -D skipTests"
            }
        }
    }
}