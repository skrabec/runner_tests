timeout(5) {
    node('maven') {
        stage('Prepare') {
            def yamlConfig = config = readYaml text: env.YAML_CONFIG
            

            if (config != null) {
            for(param in config.entrySet()) {
                env.setProperty(param.getKey(), param.getValue())
            }
        }

        testTypes = env.getProperty("TEST_TYPES").replace("[", "").replace("]", "").split(",\\s*")

        def triggerdJobs = [:]

        for (i = 0; i < testTypes.size(); i+= 1){
            def type = testTypes[i]
            sh "echo add ${type}"
            triggerdJobs[type]={
                build wait: true, job: type, parameters: [text(name: "YAML_CONFIG", value: env.YAML_CONFIG)]
            }
        }

            parallel jobs
        }

        stage('Results') {
            echo "All selected test jobs completed"
        }
    }
}