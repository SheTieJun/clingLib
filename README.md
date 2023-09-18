## ClingLib

功能：Android DLNA投屏

建议使用这位**devin1014**的[DLNA-Cast](https://github.com/devin1014/DLNA-Cast)


## 已实现
1. 基础投屏功能
2. 本地资源投屏

本项目主要是为自己的业务实现，如果有需要请自行修改，不提供解决方案

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

### 遇到的问题：
- 设备搜索问题：`cling` 2.1.2 搜索设备有问题，暂时不要用，用2.1.1
- 集成过程中：可能存在`slf4j-simple`重复： `exclude group: 'org.slf4j', module: 'slf4j-simple'`
- 投屏成功没有播放：【有些电视不会自动播放】：调用`setAVTransportURI`投屏后还要调用掉一次play才能播放 
- 构建本地服务器只能是http: 需要修改`network_security_config.xml`中的`<base-config cleartextTrafficPermitted="true">`
- 暂时不支持REFRE，项目有代码尝试，但是没有测试

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
control.canNext(callback) 
control.previous(callback)
control.canPrevious(callback) 
control.getPositionInfo(callback)
control.getMediaInfo(callback)
control.getTransportInfo(callback)
```

#### 5. 本地资源投屏，投屏期间不可关闭服务【请自行选择是否开启多进程服务】

> **原理：
> 利用`jetty`和`servlet-api`构建手机本地的服务器，又因为DLNA投屏需要再同一个局域网中间，所以电视是可以访问到的手机的资源【App要具有对应的权限】。
> 因此投屏期间不可关闭服务，否则会导致投屏失败**


1. 启动本地服务器
```Kotlin
    ClingDLNAManager.startLocalFileService(this)
```
2. 选择文件构建本地url【注意权限的获取】
``` 
    val url = ClingDLNAManager.getBaseUrl(this) + 本地路径
```
案例如下
```Kotlin

val builder = Builder().apply {
    when (type) {
        ClingPlayType.TYPE_VIDEO -> {
            setMediaType(VideoOnly)
        }
        ClingPlayType.TYPE_IMAGE -> {
            setMediaType(ImageOnly)
        }
        else -> {
            return
        }
    }
}
     pickVisualMedia(inputType = builder.build()) {
                if (it == null) return@pickVisualMedia
                val url = ClingDLNAManager.getBaseUrl(this) + FileQUtils.getFileAbsolutePath(this, it)
                control?.setAVTransportURI(url,title, type, object : ServiceActionCallback<Unit> {
                    override fun onSuccess(result: Unit) {
                        "投放成功".showToast()
                        control?.play() //有些还要重新调用一次播放
                    }

                    override fun onFailure(msg: String) {
                        "投放失败:$msg".showToast()
                    }
                })
            }

```
3. 关闭服务
```kotlin
   ClingDLNAManager.stopLocalFileService(this)
```