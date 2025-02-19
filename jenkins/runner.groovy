timeout(5) {
    node('maven') {
        stage('Prepare') {
            def config = readYaml text: params.YAML_CONFIG ?: '''
                test_types:
                  - ui
                  - api
                '''
            echo "Starting test execution for types: ${config.test_types}"
        

        def jobs = [:]
        
        if (params.TEST_TYPE in ['all', 'ui']) {
                jobs['ui'] = {
                    build(job: 'ui-tests', propagate: false)
                }
            }
            
        if (params.TEST_TYPE in ['all', 'api']) {
                jobs['api'] = {
                    build(job: 'api-tests', propagate: false)
                }
            }

        // Run both test suites in parallel
        parallel jobs
        }
        stage('Results') {
            echo "All test jobs completed"
        }
    }
}