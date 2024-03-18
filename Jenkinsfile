pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh "./gradlew --no-daemon --console=plain --info build"
            }
        }
    }
}
