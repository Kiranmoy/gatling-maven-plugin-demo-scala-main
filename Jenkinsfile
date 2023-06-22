pipeline {
    agent any
    stages {        
        stage('CLONE REPO'){
            steps{                
                bat "if exist gatling-maven-plugin-demo-scala-main rmdir /s /q gatling-maven-plugin-demo-scala-main"
                bat "git clone https://github.com/Kiranmoy/gatling-maven-plugin-demo-scala-main.git"
            }        
        }
        stage("CONFIGURE") {
            steps{
                bat "mkdir $BUILD_NUMBER"
            }        
        } 
        stage("RUN TEST & PUBLISH RESULT"){
            steps{
                dir("gatling-maven-plugin-demo-scala-main"){
                    bat "mvn gatling:test -DUSERS=100 -DRAMP_DURATION=20 -DWORKLOAD_MODEL=o"
                }                
            }
            post {
                always {
                    gatlingArchive()
                }
            }
        }
    }
}    

