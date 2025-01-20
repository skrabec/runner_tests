timeout(time: 10, unit: 'MINUTES') {
    node('maven') {
        

        def prepare_yaml_config() {
            def config = readYaml text: $CONFIG

            config.each( k, v -> {
                env.setProperty(k,v)
            })
        }

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