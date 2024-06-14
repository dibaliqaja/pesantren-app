# Pesantren App

<h1 align="center">
  <img src="https://i.imgur.com/WM2JpSy.png" width="130px"/><br/>
  Pesantren App
</h1>

Note:

- This app consume API from Project
> https://github.com/dibaliqaja/pesantren-cms

- Update baseUrl API in file `RetrofitClient.kt`


```kotlin
/**
 * /app/src/main/java/com/dibaliqaja/ponpesapp/services/RetrofitClient.kt
 */

...
...

object RetrofitClient {
    private const val baseUrl = "http://103.171.84.105/api/v1/"   // Change with your API

    ...
    ...
```

- Generate APK
    - https://code.tutsplus.com/how-to-generate-apk-and-signed-apk-files-in-android-studio--cms-37927t
