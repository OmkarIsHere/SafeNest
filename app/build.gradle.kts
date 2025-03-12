import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.services)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val gradleProperties = gradleLocalProperties(rootDir, providers)
val type : String = gradleProperties.getProperty("type", "")
val projectId : String = gradleProperties.getProperty("project_id", "")
val privateKeyId : String = gradleProperties.getProperty("private_key_id", "")
val privateKey : String = gradleProperties.getProperty("private_key", "")
val clientEmail : String = gradleProperties.getProperty("client_email", "")
val clientId : String = gradleProperties.getProperty("client_id", "")
val authUri : String = gradleProperties.getProperty("auth_uri", "")
val tokenUri : String = gradleProperties.getProperty("token_uri", "")
val authProviderX509CertUrl : String = gradleProperties.getProperty("auth_provider_x509_cert_url", "")
val clientX509CertUrl : String = gradleProperties.getProperty("client_x509_cert_url", "")
val universeDomain : String = gradleProperties.getProperty("universe_domain", "")
val mapApi : String = gradleProperties.getProperty("map_api", "")
val hereApi : String = gradleProperties.getProperty("here_api", "")

android {
    namespace = "com.safenest.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.safenest.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "type", type)
        resValue("string", "projectId", projectId)
        resValue("string", "privateKeyId", privateKeyId)
        resValue("string", "privateKey", privateKey)
        resValue("string", "clientEmail", clientEmail)
        resValue("string", "clientId", clientId)
        resValue("string", "authUri",  authUri)
        resValue("string", "tokenUri",  tokenUri)
        resValue("string", "authProviderX509CertUrl", authProviderX509CertUrl)
        resValue("string", "clientX509CertUrl", clientX509CertUrl)
        resValue("string", "universeDomain", universeDomain)
        resValue("string", "mapApi", mapApi)
        resValue("string", "hereApi", hereApi)
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    lint {
        checkReleaseBuilds = false
    }

    packaging {
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Work-Runtime
    implementation(libs.work.runtime)

    //OTP view
    implementation(libs.otpview)

    //Retrofit(HTTPs Network Service)
    implementation(libs.retrofit)
    implementation(libs.retrofitGson)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging.ktx)

    //koin(DI)
    implementation(libs.koin)

    //Map
    implementation(libs.google.map)

    //Glide(For Image)
    implementation(libs.glide)

    //Google-auth
    implementation(libs.google.auth)




}