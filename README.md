## ClingLib

功能：Android DLNA投屏

**暂时有很多问题**

### 注意事项

```groovy

allprojects {
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            /.../
            maven { url "https://jitpack.io" }
            maven {
                url 'http://4thline.org/m2'
                allowInsecureProtocol = true  //支持http
            }
        }
    }
}
```

主module需要加入

```groovy
   packagingOptions {
    exclude 'META-INF/beans.xml'
}
```

- `cling` 2.1.2 搜索设备有问题，暂时不要用
- 可能存在`slf4j-simple`重复： `exclude group: 'org.slf4j', module: 'slf4j-simple'`
- **暂时有很多问题**

### 使用方法

#### 1. 启动服务

```kotlin
mUpnpServiceConnection = startBindUpnpService()
```

#### 2. 停止服务

```kotlin
stopUpnpService(mUpnpServiceConnection)
```

#### 3. 搜索设备

```kotlin
ClingManager.getInstant().searchDevices()  //默认启动服务后会自动搜索一次
ClingManager.getInstant().getCurSearchDevices().observe(this) {
    mAdapter.setList(it)  //展示搜索结果
}
```

 

### Tips


## 关于[cling](https://github.com/4thline/cling)

该项目基于https://github.com/4thline/cling 的工作,不幸的是，该项目进入了 EOL 状态。

所以有些功能我也不一定能加