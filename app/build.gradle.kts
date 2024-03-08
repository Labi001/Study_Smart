plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id ("com.google.devtools.ksp")
    id("kotlin-kapt")
}

android {
    namespace = "com.labinot.bajrami.study_smart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.labinot.bajrami.study_smart"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kapt {
        correctErrorTypes = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    //System Color UI
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.30.1")

    //Splash Api
    implementation ("androidx.core:core-splashscreen:1.0.1")


    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    //Nav Controller
    implementation("androidx.navigation:navigation-compose:2.7.6")

    //Splash Api
    implementation ("androidx.core:core-splashscreen:1.0.1")

    //Compose Destination
    implementation ("io.github.raamcosta.compose-destinations:core:1.9.52")
    ksp ("io.github.raamcosta.compose-destinations:ksp:1.9.52")

    //Data Store
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-rxjava2:1.0.0")


    //Hilt-Dagger
    implementation ("com.google.dagger:hilt-android:2.50")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
    kapt ("com.google.dagger:hilt-android-compiler:2.50")
    kapt ("androidx.hilt:hilt-compiler:1.1.0")


    //ROOM DB
    implementation ("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation ("androidx.room:room-runtime:2.6.1")
    ksp ("androidx.room:room-compiler:2.6.1")

    //font
    implementation ("androidx.compose.ui:ui-text-google-fonts:1.6.0")

    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.4")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}