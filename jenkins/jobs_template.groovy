timeout(5) {
    node("maven") {
        stage("Checkout") {
            checkout scm
        }
        stage("Deploy changes to jenkins") {
            dir("jenkins") {
                sh "jenkins-jobs --conf ./jobs.ini update ./jobs"
            }
        }
    }
}