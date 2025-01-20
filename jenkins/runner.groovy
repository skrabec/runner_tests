timeout(time: 10, unit: 'MINUTES') {
    node('maven') {
        def prepare_yaml_config() {
            // Parse YAML config to map
            def config = readYaml text: env.CONFIG

            // Iterate using proper Groovy map syntax and environment variable setting
            config.each { k, v ->
                env[k] = v.toString()
            }
        }

        def jobs = [:]
        
        // Need to ensure env.TESTS is properly initialized and split if it's a string
        def testTypes = env.TESTS instanceof String ? env.TESTS.split(',') : env.TESTS
        
        testTypes.each { test_type ->
            // Create proper parameter list with values
            jobs["${test_type}-tests"] = {
                stage("Running ${test_type} tests") {
                    def parameters = [
                        [$class: 'StringParameterValue', name: 'SRCSPEC', value: env.SRCSPEC ?: ''],
                        [$class: 'StringParameterValue', name: 'CONFIG', value: env.CONFIG ?: '']
                    ]
                    
                    build job: "${test_type}-tests",
                          parameters: parameters,
                          propagate: false
                }
            }
        }

        prepare_yaml_config()
        parallel jobs
    }
}