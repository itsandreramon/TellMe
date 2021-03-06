package com.tellme.app

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:3.5.3"
    const val ktlint = "com.pinterest:ktlint:${Versions.ktlint}"

    object Google {
        const val material = "com.google.android.material:material:1.1.0"
        const val gmsGoogleServices = "com.google.gms:google-services:4.3.2"

        object Dagger {
            private const val version = "2.27"
            const val dagger = "com.google.dagger:dagger:$version"
            const val compiler = "com.google.dagger:dagger-compiler:$version"
        }

        object Firebase {
            const val analytics = "com.google.firebase:firebase-analytics:17.2.0"
            const val auth = "com.google.firebase:firebase-auth:19.1.0"
            const val storage = "com.google.firebase:firebase-storage:19.1.0"
            const val firestore = "com.google.firebase:firebase-firestore:21.2.0"

            object ML {
                const val language = "com.google.firebase:firebase-ml-natural-language:22.0.0"
                const val translate = "com.google.firebase:firebase-ml-natural-language-translate-model:20.0.7"
                const val id = "com.google.firebase:firebase-ml-natural-language-language-id-model:20.0.7"
            }
        }
    }

    object Kotlin {
        private const val version = "1.3.72"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"

        object Coroutines {
            private const val version = "1.3.7"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val reactive = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$version"
            const val rx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$version"
        }
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:1.1.0"
        const val coreKtx = "androidx.core:core-ktx:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta6"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0"
        const val viewPager = "androidx.viewpager2:viewpager2:1.0.0"
        const val security = "androidx.security:security-crypto:1.0.0-rc02"
        const val savedStateHandle = "androidx.savedstate:savedstate:1.0.0"

        object WorkManager {
            private const val version = "2.3.4"
            const val runtime = "androidx.work:work-runtime-ktx:$version"
        }

        object Navigation {
            private const val version = "2.2.2"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val runtime = "androidx.navigation:navigation-runtime-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }

        object Fragment {
            private const val version = "1.2.4"
            const val fragmentKtx = "androidx.fragment:fragment-ktx:$version"
        }

        object Lifecycle {
            private const val version = "2.2.0"
            const val runtime = "androidx.lifecycle:lifecycle-runtime:$version"
            const val compiler = "androidx.lifecycle:lifecycle-compiler:$version"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        }

        object Room {
            private const val version = "2.2.5"
            const val runtime = "androidx.room:room-runtime:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val testing = "androidx.room:room-testing:$version"
        }

        object Test {
            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
            const val junitExt = "androidx.test.ext:junit:1.1.1"
        }
    }

    object Square {
        object Moshi {
            private const val version = "1.9.2"
            const val moshi = "com.squareup.moshi:moshi-kotlin:$version"
            const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
        }

        object Retrofit {
            private const val version = "2.9.0"
            const val retrofit = "com.squareup.retrofit2:retrofit:$version"
            const val moshiConverter = "com.squareup.retrofit2:converter-moshi:$version"
            const val scalars = "com.squareup.retrofit2:converter-scalars:$version"
        }

        object OkHttp {
            private const val version = "4.7.2"
            const val okHttp = "com.squareup.okhttp3:okhttp:$version"
            const val logging = "com.squareup.okhttp3:logging-interceptor:$version"
        }
    }

    object ReactiveX {
        const val rxJava = "io.reactivex.rxjava2:rxjava:2.2.19"
        const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
        const val rxBinding = "com.jakewharton.rxbinding2:rxbinding:2.2.0"
        const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0"
    }

    object Uber {
        object AutoDispose {
            private const val version = "1.4.0"
            const val autoDispose = "com.uber.autodispose:autodispose:$version"
            const val android = "com.uber.autodispose:autodispose-android:$version"
            const val lifecycle = "com.uber.autodispose:autodispose-android-archcomponents:$version"
        }
    }

    object Coil {
        private const val version = "0.11.0"
        const val coil = "io.coil-kt:coil:$version"
    }

    object JakeWharton {
        const val threeTen = "com.jakewharton.threetenabp:threetenabp:1.2.1"
        const val timber = "com.jakewharton.timber:timber:4.7.1"
    }

    object Test {
        const val junit = "junit:junit:4.13"
    }
}