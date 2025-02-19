timeout(5) {
    node('maven') {
        stage('Prepare') {
            def yamlConfig = params.YAML_CONFIG?.trim() ?: '''
            test_types:
              - ui
              - api
            '''
            
            def testTypes = []
            yamlConfig.eachLine { line ->
                if (line.trim().startsWith('-')) {
                    def testType = line.trim().substring(1).trim()
                    testTypes.add(testType)
                }
            }
            
            echo "Starting test execution for types: ${testTypes}"
            
            def jobs = [:]
            
            if (testTypes.contains('ui')) {
                jobs['ui'] = {
                    build(job: 'ui-tests', propagate: false)
                }
            }
            
            if (testTypes.contains('api')) {
                jobs['api'] = {
                    build(job: 'api-tests', propagate: false)
                }
            }

            parallel jobs
        }

        stage('Results') {
            echo "All selected test jobs completed"
        }
    }
}