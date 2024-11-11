# Infra


## CICD
### Jenkins
#### Pipeline
script
```
pipeline {
    agent any
    environment {
        DOCKER_CREDENTIALS = credentials('DOCKER_USER')
        DOCKER_PROJECT = 'welight-backend-spring'
        EC2_SERVER_IP = credentials('EC2_SERVER_IP')
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Starting Checkout Stage..."
                
                git branch: 'develop', 
                credentialsId: 'gitlab',
                url: 'https://lab.ssafy.com/s11-final/S11P31D209.git'
                
                echo "Checkout completed."
            }
        }
        
        stage('Copy') {
            steps {
                echo "Starting Copy Stage..."
                
                withCredentials([file(credentialsId: 'APPFILE', variable: 'APPFILE')]) {
                    script {
                        sh 'rm /var/jenkins_home/workspace/test-pipeline/Backend/src/main/resources/application.yml'
                        sh 'cp $APPFILE /var/jenkins_home/workspace/test-pipeline/Backend/src/main/resources'
                    }
                }
                
                withCredentials([file(credentialsId: 'DBFILE', variable: 'DBFILE')]) {
                    script {
                        sh 'cp $DBFILE /var/jenkins_home/workspace/test-pipeline/Backend/src/main/resources'
                    }
                }
                
                withCredentials([file(credentialsId: 'S3FILE', variable: 'S3FILE')]) {
                    script {
                        sh 'cp $S3FILE /var/jenkins_home/workspace/test-pipeline/Backend/src/main/resources'
                    }
                }
                
                withCredentials([file(credentialsId: 'SECURITYFILE', variable: 'SECURITYFILE')]) {
                    script {
                        sh 'cp $SECURITYFILE /var/jenkins_home/workspace/test-pipeline/Backend/src/main/resources'
                    }
                }
                
                
                echo "Copy completed."
            }
        }

        stage('Build') {
            steps {
                echo "Starting Build Stage..."
                
                dir('/var/jenkins_home/workspace/test-pipeline/Backend/'){
                    sh 'pwd'
                    sh 'ls -al'
                    sh 'chmod +x ./gradlew'
                    sh 'chmod +x ./gradlew.bat'
                    sh 'java --version'
                    sh './gradlew clean build'
                }
 
                echo "Build completed."
            }
        }

        stage('Test') {
            steps {
                echo "Starting Test Stage..."
                echo "Test completed."
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Starting Deploy Stage..."
                
                sh '''
                    echo $DOCKER_CREDENTIALS_PSW | docker login -u $DOCKER_CREDENTIALS_USR --password-stdin
                '''
                
                sh """
                    cd ./Backend
                    docker build -t ${DOCKER_CREDENTIALS_USR}/${DOCKER_PROJECT}:latest .
                    docker push ${DOCKER_CREDENTIALS_USR}/${DOCKER_PROJECT}:latest
                """
                
                sshagent(['SSH_KEY']) {
                    sh '''
                        chmod 600 ~/.ssh/id_rsa
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_SERVER_IP} "sudo sh deploy.sh"
                    '''
                }
                
                echo "Deploy completed."
            }
        }
    }
    post {
        always {
            script {
                def Author_ID = sh(script: "git show -s --pretty=%an", returnStdout: true).trim()
                def Author_Name = sh(script: "git show -s --pretty=%ae", returnStdout: true).trim()
                def Commit_Message = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                def Build_Status = currentBuild.result ?: 'SUCCESS'
                def Status_Color = Build_Status == 'SUCCESS' ? 'good' : (Build_Status == 'UNSTABLE' ? 'warning' : 'danger')
                def Status_Text = Build_Status == 'SUCCESS' ? '빌드 성공' : (Build_Status == 'UNSTABLE' ? '빌드 불안정' : '빌드 실패')
                def branchName = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                def previousCommit = env.GIT_PREVIOUS_SUCCESSFUL_COMMIT ?: 'HEAD~1'
                def allCommits = sh(script: "git log --pretty=format:'%h - %s (%an)' $previousCommit..HEAD", returnStdout: true).trim()
                def formattedCommits = allCommits.split('\\n').collect { line ->
                    def escapedLine = line.replaceAll("([\\[\\]\\(\\)])", '\\\\$1')
                    "• ${escapedLine}"
                }.join('\\n')
                def message = """
                    #### BE $Status_Text
                    **빌드 번호:** $env.JOB_NAME #$env.BUILD_NUMBER
                    **브랜치:** $branchName
                    **작성자:** $Author_ID ($Author_Name)
                    **빌드 URL:** [Details]($env.BUILD_URL)
                    **포함된 커밋:**
                    $formattedCommits
                """.stripIndent()
                mattermostSend(
                    color: Status_Color,
                    message: message,
                    endpoint: 'https://meeting.ssafy.com/hooks/1uxrhh9h5tyupcqizrukfpb94e',
                    channel: 'D209-Jenkins-BOT',
                )
            }
        }
    }
}

```