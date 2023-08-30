## ClingLib

功能：Android DLNA投屏

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

#### 4. 操作相关
```kotlin
ClingManager.playNew(url, itemType, callback)  //播放一个新的链接
ClingManager.play(controlCallback) //播放
ClingManager.pause(controlCallback) //暂停
ClingManager.stop(controlCallback) //停止
ClingManager.setVolume(volume, controlCallback) //设置音量，写了每用过
ClingManager.setMute(isMute, controlCallback) //静音，写了没用过
ClingManager.getPositionInfo(controlCallback) //获取播放进度
ClingManager.getVolume(controlCallback) //获取音量，写了没用过
```

#### 5. referer 防盗链 :待测试
用于一些播放链接有防盗链的情况，需要设置referer
```kotlin
ClingManager.setReferer(referer) //待测试
```

### Tips
暂时没有电视可以测试， 有些功能可能不支持，这些功能需要电视端支持，所以没有测试过，如果有人测试过，可以告诉我一下，我会更新到这里。


## 关于[cling](https://github.com/4thline/cling)
该项目基于https://github.com/4thline/cling 的工作,不幸的是，该项目进入了 EOL 状态。

所以有些功能我也不一定能加