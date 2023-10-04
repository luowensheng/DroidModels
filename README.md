# Droid Models

### Installation

1. gradle
```

Step 1. Update settings.gradle by adding jitack repo:

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        
        maven { url 'https://jitpack.io' }
    }
}
	
Step 2. Add the dependency in your root build.gradle at the end of repositories:

	dependencies {
	
	        implementation 'com.github.luowensheng:DroidModels:0.0.7'
	}
```
