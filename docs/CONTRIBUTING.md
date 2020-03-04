# Contributing

When contributing to this repository, please first discuss the change you wish to make via issue, before making a change. 

## Pull Request Process

1. Ensure any install or build dependencies/leavings are removed before creating a Pull Request.
2. Update the README.md with details of changes to the interface.
3. You may merge the Pull Request in once you have the sign-off of at least one other developer, or if you 
   do not have permission to do that, you may request the second reviewer to merge it for you.

## Plugin Publishing Process

1. Merge in all Pull Requests to the `master` branch.
2. Update build version (`build.gradle`) and any example version in README.md > Configuration.
3. Commit new version and create new tag using [SemVer](http://semver.org/).
4. Make sure your local `gradle.properties` are correct ([more details here](https://guides.gradle.org/publishing-plugins-to-gradle-plugin-portal/#create_an_account_on_the_gradle_plugin_portal)).
5. Publish plugin by running `./gradlew publishPlugins`.

> Here you can find [more details about plugin publishing](https://guides.gradle.org/publishing-plugins-to-gradle-plugin-portal/#publish_your_plugin).
