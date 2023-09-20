## ClingLib

功能：Android DLNA投屏

**建议使用这位`devin1014`的[DLNA-Cast](https://github.com/devin1014/DLNA-Cast)**

## 已实现
1. 基础投屏功能
2. 本地资源投屏

本项目主要是为自己的业务实现的，如果有需要请自行修改，不提供解决方案

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
                allowInsecureProtocol = true  //当前maven支持http
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
- 暂时不支持`Referer`，项目有代码尝试，但是没有测试，暂时没有条件测试，如果有需要，可以自行测试,然后告诉我结论

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
```kotlin
control = ClingDLNAManager.getInstant().connectDevice(device)
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

--------------------------------

**原理**：
1. 利用`jetty`和`servlet-api`构建**手机本地的服务器**，又因为DLNA投屏需要**手机和电视在同一个局域网中**，所以电视是可以访问到的手机的资源【App要具有对应的权限】。**因此投屏期间不可关闭服务，否则会导致播放失败.**
2. 构建本地服务器只能是http: 需要修改`network_security_config.xml`中的`<base-config cleartextTrafficPermitted="true">`
3. ~~本地服务的`contentType = "application/octet-stream"`，是一个**通用的 MIME 类型，表示二进制数据流**。所以有些辣鸡的播放器**可能无法识别**，不过你可以增加更多的判断进行设置**contentType**。【**重写LocalFileService**】~~
   - 通过传入路径的文件结尾来判断contentType，如果没有找到，就使用默认的`application/octet-stream`，如果你有更好的方法，欢迎提出来
```kotlin
        put("png", "image/png")
        put("jpg", "image/jpeg")
        put("mp4", "video/mp4")
        put("mov", "video/quicktime")
        put("wmv", "video/x-ms-wmv")
        put("m3u8", "application/x-mpegURL")
        put("mp3","audio/mpeg")
        put("m4a","audio/m4a")
        put("wav","audio/wav")
        put("aac","audio/aac")
```
--------------------------------
###### 5.1 启动本地服务器
```Kotlin
    ClingDLNAManager.startLocalFileService(this)
```
###### 5.2 选择文件构建本地url【注意权限的获取】
``` 
val url = ClingDLNAManager.getBaseUrl(this) + 本地路径
```

###### 5.3 关闭服务
```kotlin
   ClingDLNAManager.stopLocalFileService(this)
```
- 案例如下:[MainActivity.kt](https://github.com/SheTieJun/clingLib/blob/f83527d57268ffc366fe6a9571af0b2f5a89b1b5/app/src/main/java/com/shetj/clinglib/MainActivity.kt)
- 如果没有wifi: 会是 127.0.0.1，所以请在连接好wifi和设备后，在构建本地的url，防止构建出错误的url,导致无法播放，同时播放中请不要关闭本地的服务器的Service
- 链接组成 : http://xxx.xx.xx.xx:5050/clingLocaleFile//data/user/0/com.shetj.clinglib/cache/1000022406.mp4
  - 其中`xxx.xx.xx.xx`是手机链接wifi后的ip地址
  - `5050`是端口号
  - `clingLocaleFile`是固定的，重写`LocalFileService`可以修改
  - `/data/user/0/com.xxx.xxx/xxx/1000022406.mp4`是本地的路径，也可以自己结合`LocalFileService`重新定义,这样就可以不用担心暴露本地路径了

## 友情提示
- 当前项目本人已应用到项目，虽然有些地方有修改，但是基础功能都是一样的，并且可以正常使用。所以有问题，请优先自己先调试一下，找找问题，解决不了，在提提问会比较好
- 不建议直接使用，可以先去学习理解其中的原理：**DLNA、UPnP、本地服务器**等等，然后下载下来在按自己的业务进行调整修改
- 这个Lib是因为**乐播收费**才做的需求，目前已经满足自己的业务需求，后续不会再更新，直到有新的需求~ 有些地方可能不是很完善，**如果有需要，可以自行修改，如果有问题，请优先自己先调试一下，找找问题，解决不了，在提提问会比较好，我会尽量解决**