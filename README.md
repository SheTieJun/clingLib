## ClingLib

功能：Android 投屏

```
APP 突然要加投屏功能,先快速加入后面再看情况
```

### 注意事项

使用 2.1.2时出现了问题，所用使用2.1.1 

主module需要加入
```
  packagingOptions {
        exclude 'META-INF/beans.xml'
    }
```
