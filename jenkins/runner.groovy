timeout(time: 10, unit: 'MINUTES') {
    node('maven') {

        dir('jenkins') {  
            utils = load './utils.groovy'
        }

        utils.prepare_yaml_config()

        def jobs = [:]
        
        for(def test_type: env.TESTS) {
            jobs["${test_type}-tests"] = {
                stage("Running ${test_type} tests") {
                    def parameters = ["SRCSPEC", "CONFIG"]
                    build job: "${test_type}-tests", parameters: parameters, propagate: false
                }
            }
        }

        parallel jobs
    }
}