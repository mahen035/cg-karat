pipeline {
    agent any
    tools{
        maven 'Maven-3.9'
    }
    stages {
        stage('Checkout') {
            steps {
               checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/mahen035/cg-karat.git']])
               echo "Code Checked our successfully"
            }
        }
         stage('Build') {
            steps {
              dir('product-catalog-service'){
                  sh 'mvn clean install'
              }
            }
        }
        stage('Build Docker Image') {
            steps {
              dir('product-catalog-service'){
                  sh 'docker build -t orbit/product-catalog-service:latest .'
              }
            }
        }
        stage('Run Docker Containers') {
            steps {
              dir('product-catalog-service'){
                  sh 'docker compose up -d'
              }
            }
        }
    }
}
