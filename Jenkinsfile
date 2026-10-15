pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Holt den aktuellen Code aus dem Repository
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                // Führt den Maven-Build inklusive Unit-Tests aus
                // (Unter Windows ggf. 'mvn.cmd clean package' nutzen)
                sh './mvnw clean package'
            }
        }

        stage('Docker Build') {
            steps {
                // Baut das Docker-Image basierend auf deinem Dockerfile
                sh 'docker build -t hemolytix-backend:latest .'
            }
        }
    }

    post {
        success {
            echo 'Pipeline erfolgreich durchgelaufen: Build, Tests und Docker-Image sind fertig!'
        }
        failure {
            echo 'Fehler im Build-Prozess oder bei den Tests.'
        }
    }
}