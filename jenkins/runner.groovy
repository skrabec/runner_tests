def prepare_yaml_config() {
    def config = readYaml text: '''
        TESTS: unit,integration
        SRCSPEC: main
        # Add other configuration parameters here
    '''
    
    config.each { k, v ->  
        env[k] = v.toString()
    }
}

timeout(time: 10, unit: 'MINUTES') {
    node('maven') {


        prepare_yaml_config()

        def jobs = [:]
        for(def test_type: env.TESTS) {
        
            jobs[test_type] = node ('maven') {
                stage("Running $test_type tests") {

                    def parameters = [
                        "$REFSPEC", 
                        "$CONFIG"
                        ]

                    build (job: "$test_type-tests", 
                    parameters: parameters, propagate: false)
                }
            }
        }

        parallel jobs
    }
}