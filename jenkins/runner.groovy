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
        
        // UI Tests job
        jobs['ui'] = {
            build(
                job: 'ui-tests',
                propagate: false
            )
        }
        
        // API Tests job
        jobs['api'] = {
            build(
                job: 'api-tests',
                propagate: false
            )
        }

        // Run both test suites in parallel
        parallel jobs
        }
        stage('Results') {
            echo "All test jobs completed"
        }
    }
}