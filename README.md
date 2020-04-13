# Jenkins Shared Libraries

## What's possible with the current version (`544f1f4`) in the `master` ?

1. In your Jenkins instance, create a new folder and setup the `Pipeline Libraries' section to load the shared libraries from this repo.

- Name of the library should be, `jenkins-shared-libs`.

2. Setup a new pipeline job in your Jenkins instance.

- Configure the `Pipeline script from SCM` section to load this repo, https://github.com/mramanathan/jenkins-pipeline-class

- In the same job, change the `Script Path` to pick up the pipeline script from the location, `16_shared_library/Jenkinsfile`.

3. Ensure you have a build agent setup to handle Docker builds.

- Assign label `docker` to that agent.

4. With this you should be all set to try out the shared library by triggering a fresh job for the build that was setup in step 2.