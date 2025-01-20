// Define the function at the top level
def prepare_yaml_config(config) {
    def yaml_config = readYaml text: config
    yaml_config.each { k, v ->
        env[k] = v.toString()
    }
}

// Main pipeline
timeout(time: 10, unit: 'MINUTES') {
    node('maven') {
        def jobs = [:]
        
        // Call the function with proper parameter
        prepare_yaml_config(env.CONFIG)
        
        def testTypes = env.TESTS instanceof String ? env.TESTS.split(',') : env.TESTS
        
        testTypes.each { test_type ->
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

        parallel jobs
    }
}