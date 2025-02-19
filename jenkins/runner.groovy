timeout(5) {
    node('maven') {
        stage('Prepare') {
            def config = params.YAML_CONFIG?.trim() ?: '''
test_types:
  - ui
  - api'''
            
            def testTypes = []
            config.split('\n').each { line ->
                def trimmed = line.trim()
                if (trimmed.startsWith('-')) {
                    testTypes << trimmed.substring(1).trim()
                }
            }
            
            echo "Starting test execution for types: ${testTypes}"
            
            def jobs = [:]
            
            testTypes.each { type ->
                if (type == 'ui') {
                    jobs['ui'] = {
                        build(job: 'ui-tests', propagate: false)
                    }
                }
                if (type == 'api') {
                    jobs['api'] = {
                        build(job: 'api-tests', propagate: false)
                    }
                }
            }

            if (jobs.size() > 0) {
                parallel jobs
            }
        }

        stage('Results') {
            echo "All selected test jobs completed"
        }
    }
}