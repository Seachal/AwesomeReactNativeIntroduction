<font style="color:rgb(28, 30, 33);">Your React Native application code may need to interact with native platform APIs that aren't provided by React Native or an existing library. You can write the integration code yourself using a </font>**<font style="color:rgb(28, 30, 33);">Turbo Native Module</font>**<font style="color:rgb(28, 30, 33);">. This guide will show you how to write one.</font>

<font style="color:rgb(28, 30, 33);">The basic steps are:</font>

1. **<font style="color:rgb(28, 30, 33);">define a typed JavaScript specification</font>**<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">using one of the most popular JavaScript type annotation languages: Flow or TypeScript;</font>
2. **<font style="color:rgb(28, 30, 33);">configure your dependency management system to run Codegen</font>**<font style="color:rgb(28, 30, 33);">, which converts the specification into native language interfaces;</font>
3. **<font style="color:rgb(28, 30, 33);">write your application code</font>**<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">using your specification; and</font>
4. **<font style="color:rgb(28, 30, 33);">write your native platform code using the generated interfaces</font>**<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">to write and hook your native code into the React Native runtime environment.</font>

<font style="color:rgb(28, 30, 33);">Lets work through each of these steps by building an example Turbo Native Module. The rest of this guide assume that you have created your application running the command:</font>

**shell**

```shell
npx @react-native-community/cli@latest init TurboModuleExample --version 0.76.0
```

## Native Persistent Storage
<font style="color:rgb(28, 30, 33);">This guide will show you how to write an implementation of the</font><font style="color:rgb(28, 30, 33);"> </font>[Web Storage API](https://html.spec.whatwg.org/multipage/webstorage.html#dom-localstorage-dev)<font style="color:rgb(28, 30, 33);">:</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">localStorage</font>`<font style="color:rgb(28, 30, 33);">. The API is relatable to a React developer who might be writing application code on your project.</font>

<font style="color:rgb(28, 30, 33);">To make this work on mobile, we need to use Android and iOS APIs:</font>

+ <font style="color:rgb(28, 30, 33);">Android:</font><font style="color:rgb(28, 30, 33);"> </font>[SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences)<font style="color:rgb(28, 30, 33);">, and</font>
+ <font style="color:rgb(28, 30, 33);">iOS:</font><font style="color:rgb(28, 30, 33);"> </font>[NSUserDefaults](https://developer.apple.com/documentation/foundation/nsuserdefaults)<font style="color:rgb(28, 30, 33);">.</font>

### 1. Declare Typed Specification
<font style="color:rgb(28, 30, 33);">React Native provides a tool called</font><font style="color:rgb(28, 30, 33);"> </font>[Codegen](https://reactnative.dev/docs/the-new-architecture/what-is-codegen)<font style="color:rgb(28, 30, 33);">, which takes a specification written in TypeScript or Flow and generates platform specific code for Android and iOS. The specification declares the methods and data types that will pass back and forth between your native code and the React Native JavaScript runtime. A Turbo Native Module is both your specification, the native code you write, and the Codegen interfaces generated from your specification.</font>

<font style="color:rgb(28, 30, 33);">To create a specs file:</font>

1. <font style="color:rgb(28, 30, 33);">Inside the root folder of your app, create a new folder called</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">specs</font>`<font style="color:rgb(28, 30, 33);">.</font>
2. <font style="color:rgb(28, 30, 33);">Create a new file called</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">NativeLocalStorage.ts</font>`<font style="color:rgb(28, 30, 33);">.</font>

info

You can see all of the types you can use in your specification and the native types that are generated in the [Appendix](https://reactnative.dev/docs/appendix) documentation.

<font style="color:rgb(28, 30, 33);">Here is an implementation of the</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">localStorage</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">specification:</font>

+ **TypeScript**
+ Flow

**specs/NativeLocalStorage.ts**

```typescript
import type {TurboModule} from 'react-native';
import {TurboModuleRegistry} from 'react-native';

export interface Spec extends TurboModule {
  setItem(value: string, key: string): void;
  getItem(key: string): string | null;
  removeItem(key: string): void;
  clear(): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>(
  'NativeLocalStorage',
);
```

### 2. Configure Codegen to run
<font style="color:rgb(28, 30, 33);">The specification is used by the React Native Codegen tools to generate platform specific interfaces and boilerplate for us. To do this, Codegen needs to know where to find our specification and what to do with it. Update your</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">package.json</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">to include:</font>

**package.json**

```json
"start": "react-native start",
     "test": "jest"
   },
   "codegenConfig": {
     "name": "NativeLocalStorageSpec",
     "type": "modules",
     "jsSrcsDir": "specs",
     "android": {
       "javaPackageName": "com.nativelocalstorage"
     }
   },
   "dependencies": {
```

<font style="color:rgb(28, 30, 33);">With everything wired up for Codegen, we need to prepare our native code to hook into our generated code.</font>

+ **Android**
+ iOS

<font style="color:rgb(28, 30, 33);">Codegen is executed through the</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">generateCodegenArtifactsFromSchema</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">Gradle task:</font>

**bash**

```bash
cd android
./gradlew generateCodegenArtifactsFromSchema

BUILD SUCCESSFUL in 837ms
14 actionable tasks: 3 executed, 11 up-to-date
```

<font style="color:rgb(28, 30, 33);">This is automatically run when you build your Android application.</font>

### 3. Write Application Code using the Turbo Native Module
<font style="color:rgb(28, 30, 33);">Using</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">NativeLocalStorage</font>`<font style="color:rgb(28, 30, 33);">, here’s a modified</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">App.tsx</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">that includes some text we want persisted, an input field and some buttons to update this value.</font>

<font style="color:rgb(28, 30, 33);">The</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">TurboModuleRegistry</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">supports 2 modes of retrieving a Turbo Native Module:</font>

+ `<font style="color:rgb(28, 30, 33);">get<T>(name: string): T | null</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">which will return</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">null</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">if the Turbo Native Module is unavailable.</font>
+ `<font style="color:rgb(28, 30, 33);">getEnforcing<T>(name: string): T</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">which will throw an exception if the Turbo Native Module is unavailable. This assumes the module is always available.</font>

**App.tsx**

```tsx
import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  TextInput,
  Button,
} from 'react-native';

import NativeLocalStorage from './specs/NativeLocalStorage';

const EMPTY = '<empty>';

function App(): React.JSX.Element {
  const [value, setValue] = React.useState<string | null>(null);

  const [editingValue, setEditingValue] = React.useState<
    string | null
  >(null);

  React.useEffect(() => {
    const storedValue = NativeLocalStorage?.getItem('myKey');
    setValue(storedValue ?? '');
  }, []);

  function saveValue() {
    NativeLocalStorage?.setItem(editingValue ?? EMPTY, 'myKey');
    setValue(editingValue);
  }

  function clearAll() {
    NativeLocalStorage?.clear();
    setValue('');
  }

  function deleteValue() {
    NativeLocalStorage?.removeItem('myKey');
    setValue('');
  }

  return (
    <SafeAreaView style={{flex: 1}}>
      <Text style={styles.text}>
        Current stored value is: {value ?? 'No Value'}
      </Text>
      <TextInput
        placeholder="Enter the text you want to store"
        style={styles.textInput}
        onChangeText={setEditingValue}
      />
      <Button title="Save" onPress={saveValue} />
      <Button title="Delete" onPress={deleteValue} />
      <Button title="Clear" onPress={clearAll} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  text: {
    margin: 10,
    fontSize: 20,
  },
  textInput: {
    margin: 10,
    height: 40,
    borderColor: 'black',
    borderWidth: 1,
    paddingLeft: 5,
    paddingRight: 5,
    borderRadius: 5,
  },
});

export default App;
```

### 4. Write your Native Platform code
<font style="color:rgb(28, 30, 33);">With everything prepared, we're going to start writing native platform code. We do this in 2 parts:</font>

note

This guide shows you how to create a Turbo Native Module that only works with the New Architecture. If you need to support both the New Architecture and the Legacy Architecture, please refer to our [backwards compatibility guide](https://github.com/reactwg/react-native-new-architecture/blob/main/docs/backwards-compat.md).

+ **Android**
+ iOS

<font style="color:rgb(28, 30, 33);">Now it's time to write some Android platform code to make sure</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">localStorage</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">survives after the application is closed.</font>

<font style="color:rgb(28, 30, 33);">The first step is to implement the generated</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">NativeLocalStorageSpec</font>`<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">interface:</font>

+ Java
+ **Kotlin**

**android/app/src/main/java/com/nativelocalstorage/NativeLocalStorageModule.kt**

```kotlin
package com.nativelocalstorage

import android.content.Context
import android.content.SharedPreferences
import com.nativelocalstorage.NativeLocalStorageSpec
import com.facebook.react.bridge.ReactApplicationContext

class NativeLocalStorageModule(reactContext: ReactApplicationContext) : NativeLocalStorageSpec(reactContext) {

  override fun getName() = NAME

  override fun setItem(value: String, key: String) {
    val sharedPref = getReactApplicationContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString(key, value)
    editor.apply()
  }

  override fun getItem(key: String): String? {
    val sharedPref = getReactApplicationContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val username = sharedPref.getString(key, null)
    return username.toString()
  }

  override fun removeItem(key: String) {
    val sharedPref = getReactApplicationContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.remove(key)
    editor.apply()
  }

  override fun clear() {
    val sharedPref = getReactApplicationContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.clear()
    editor.apply()
  }

  companion object {
    const val NAME = "NativeLocalStorage"
  }
}
```

<font style="color:rgb(28, 30, 33);">Next we need to create</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">NativeLocalStoragePackage</font>`<font style="color:rgb(28, 30, 33);">. It provides an object to register our Module in the React Native runtime, by wrapping it as a Base Native Package:</font>

+ Java
+ **Kotlin**

**android/app/src/main/java/com/nativelocalstorage/NativeLocalStoragePackage.kt**

```kotlin
package com.nativelocalstorage

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class NativeLocalStoragePackage : BaseReactPackage() {

  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? =
    if (name == NativeLocalStorageModule.NAME) {
      NativeLocalStorageModule(reactContext)
    } else {
      null
    }

  override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
    mapOf(
      NativeLocalStorageModule.NAME to ReactModuleInfo(
        name = NativeLocalStorageModule.NAME,
        className = NativeLocalStorageModule.NAME,
        canOverrideExistingModule = false,
        needsEagerInit = false,
        isCxxModule = false,
        isTurboModule = true
      )
    )
  }
}
```

<font style="color:rgb(28, 30, 33);">Finally, we need to tell the React Native in our main application how to find this</font><font style="color:rgb(28, 30, 33);"> </font>`<font style="color:rgb(28, 30, 33);">Package</font>`<font style="color:rgb(28, 30, 33);">. We call this "registering" the package in React Native.</font>

<font style="color:rgb(28, 30, 33);">In this case, you add it to be returned by the</font><font style="color:rgb(28, 30, 33);"> </font>[getPackages](https://github.com/facebook/react-native/blob/8d8b8c343e62115a5509e1aed62047053c2f6e39/packages/react-native/ReactAndroid/src/main/java/com/facebook/react/ReactNativeHost.java#L233)<font style="color:rgb(28, 30, 33);"> </font><font style="color:rgb(28, 30, 33);">method.</font>

info

Later you’ll learn how to distribute your Native Modules as [npm packages](https://reactnative.dev/docs/the-new-architecture/create-module-library#publish-the-library-on-npm), which our build tooling will autolink for you.

+ Java
+ **Kotlin**

**android/app/src/main/java/com/turobmoduleexample/MainApplication.kt**

```kotlin
package com.inappmodule

import android.app.Application
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.soloader.SoLoader
import com.nativelocalstorage.NativeLocalStoragePackage

class MainApplication : Application(), ReactApplication {

  override val reactNativeHost: ReactNativeHost =
      object : DefaultReactNativeHost(this) {
        override fun getPackages(): List<ReactPackage> =
            PackageList(this).packages.apply {
              // Packages that cannot be autolinked yet can be added manually here, for example:
              // add(MyReactNativePackage())
              add(NativeLocalStoragePackage())
            }

        override fun getJSMainModuleName(): String = "index"

        override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

        override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
        override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
      }

  override val reactHost: ReactHost
    get() = getDefaultReactHost(applicationContext, reactNativeHost)

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this, false)
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      // If you opted-in for the New Architecture, we load the native entry point for this app.
      load()
    }
  }
}
```

<font style="color:rgb(28, 30, 33);">You can now build and run your code on an emulator:</font>

+ **npm**
+ Yarn

**bash**

```bash
npm run android
```

[  
](https://github.com/facebook/react-native-website/edit/main/website/versioned_docs/version-0.78/turbo-native-modules.md)

