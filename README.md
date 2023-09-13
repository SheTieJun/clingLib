## ClingLib

功能：Android DLNA投屏

建议使用这位**devin1014**的[DLNA-Cast](https://github.com/devin1014/DLNA-Cast)

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
- 【有些电视不会自动播放】：调用`setAVTransportURI`投屏后还要调用掉一次play才能播放 

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
ClingDLNAManager.getInstant().searchDevices()  //默认启动服务后会自动搜索一次
ClingDLNAManager.getInstant().getCurSearchDevices().observe(this) {
    mAdapter.setList(it)  //展示搜索结果
}
```
#### 4. 投屏相关
1. 连接
```kotlin
   control = ClingDLNAManager.getInstant().connectDevice(this, object : OnDeviceControlListener { })
```
```kotlin
control.setAVTransportURI(uri , title,type, callback)
control.setNextAVTransportURI(uri, title, callback)
control.play(speed = "1", callback)
control.pause(callback)
control.stop(callback)
control.seek(millSeconds, callback)
control.next(callback)
control.canNext(callback) {
control.callback?.onSuccess(false)
control.previous(callback)
control.canPrevious(callback) {
control.callback?.onSuccess(false)
control.getPositionInfo(callback)
control.getMediaInfo(callback)
control.getTransportInfo(callback)
```
