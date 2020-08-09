pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }
    stages {
        stage('build') {
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', excludes: '**/target/original-*.jar', fingerprint: true                    
                }
            }
        }
        stage ('Deploy') {
            when {
                branch "master"
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'GitHubPAToken', usernameVariable: 'USERNAME', passwordVariable: 'GITHUB_TOKEN')]) {
                    sh 'echo "Creating a new release in github"'
                    sh 'github-release release --user DoctorMacc --repo BedrockBackwards --tag v${BUILD_NUMBER} --name "Jenkins build ${BUILD_NUMBER}"'
                    sh 'echo "Uploading the artifacts into github"'
                    sh '''
                        for file in **/target/*.jar; do
                            if [[ $file != *original-* ]]; then
                                echo github-release upload --user DoctorMacc --repo BedrockBackwards --tag v${BUILD_NUMBER} --name "$(basename $file)" --file $file
                            fi
                        done
                    '''
                }
            }
        }
    }
    post {
        always {
            deleteDir()
        }
    }
}
