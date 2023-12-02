pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            val retrofit2 = version("retrofit", "2.9.0")
            val daggerHilt = version("dagger-hilt-android", "2.48.1")
            val lifecycle = version("lifecycle", "2.4.0")
            val dataStore = version("datastore", "1.0.0")

            version("core-ktx", "1.9.0")
            version("appcompat", "1.6.1")
            version("material", "1.8.0")
            version("constraintlayout", "2.1.4")
            version("gson", "2.10.1")
            version("fragment", "1.6.2")
            version("activity", "1.6.2")

            library("core-ktx", "androidx.core", "core-ktx").versionRef("core-ktx")

            //ViewModel
            library("lifecycle-viewmodel", "androidx.lifecycle", "lifecycle-viewmodel-ktx").versionRef(lifecycle)
            library("lifecycle-runtime-ktx", "androidx.lifecycle", "lifecycle-runtime-ktx").versionRef(lifecycle)
            library("activity-ktx", "androidx.activity", "activity-ktx").versionRef("activity")
            library("fragment-ktx", "androidx.fragment", "fragment-ktx").versionRef("fragment")

            //Android
            library("appcompat", "androidx.appcompat", "appcompat").versionRef("appcompat")
            library("material", "com.google.android.material", "material").versionRef("material")
            library("constraintlayout", "androidx.constraintlayout", "constraintlayout").versionRef("constraintlayout")

            //Retrofit
            library("retrofit", "com.squareup.retrofit2", "retrofit").versionRef(retrofit2)
            library("retrofit-converter-gson", "com.squareup.retrofit2", "converter-gson").versionRef(retrofit2)

            //Hilt
            library("dagger-hilt-android", "com.google.dagger", "hilt-android").versionRef(daggerHilt)
            library("dagger-hilt-compiler", "com.google.dagger", "hilt-compiler").versionRef(daggerHilt)

            //Gson
            library("gson", "com.google.code.gson", "gson").versionRef("gson")

            //DataStore
            library("datastore-core", "androidx.datastore", "datastore-core").versionRef(dataStore)
            library("datastore-preferences", "androidx.datastore", "datastore-preferences").versionRef(dataStore)
        }

        create("testLibs" ) {
            version("junit", "4.13.2")
            version("androidx.test.ext.junit", "1.1.5")
            version("espresso-core", "3.5.1")

            library("junit", "junit", "junit").versionRef("junit")
            library("androidx.test.ext.junit", "androidx.test.ext", "junit").versionRef("androidx.test.ext.junit")
            library("espresso-core", "androidx.test.espresso", "espresso-core").versionRef("espresso-core")
        }
    }
}


rootProject.name = "CryptoView"
include(":app")
