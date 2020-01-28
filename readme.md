[![Build Status](https://img.shields.io/circleci/build/github/itsandreramon/TellMe?token=f24ebd30e0d413eebc536d6c4a3d0804a9cc75fe)](https://circleci.com/gh/itsandreramon/TellMe)

The Android app for TellMe. Written entirely in [Kotlin](https://kotlinlang.org/) using [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and [RxJava](https://github.com/ReactiveX/RxJava).

- Uses [Architecture Components](https://developer.android.com/topic/libraries/architecture/)
- Uses [Dagger](https://github.com/google/dagger) for dependency injection

### Code style
This project uses [ktlint](https://github.com/pinterest/ktlint) via [Spotless](https://github.com/diffplug/spotless) to format code based on official style guides from [Kotlin](https://kotlinlang.org/docs/reference/coding-conventions.html) and [Android](https://developer.android.com/kotlin/style-guide).

### API
You can obtain a valid api key by decrypting the file using:
```
$ openssl aes-256-cbc -d -in release/google-services.json.encrypted -k $KEY >> app/google-services.json
```

*Please make sure to place the corresponding ```google-services.json``` inside the ```app``` folder.*

### Disclaimer
This project was built for educational purposes and does not represent a production ready application.
